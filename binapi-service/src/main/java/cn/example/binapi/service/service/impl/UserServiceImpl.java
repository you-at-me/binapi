package cn.example.binapi.service.service.impl;

import cn.example.binapi.common.constant.UserConstant;
import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.service.common.ErrorCode;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.mapper.UserMapper;
import cn.example.binapi.service.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.example.binapi.common.constant.UserConstant.USER_LOGIN_EXPIRE_TIME;
import static cn.example.binapi.common.constant.UserConstant.USER_LOGIN_STATE;
import static cn.example.binapi.service.common.ErrorCode.PARAMS_ERROR;


/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "zyshu";

    @Override
    public long userRegister(String account, String password, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            throw new BusinessException(PARAMS_ERROR, "参数为空");
        }
        if (account.length() < 4) {
            throw new BusinessException(PARAMS_ERROR, "用户账号过短");
        }
        if (password.length() < 6 || checkPassword.length() < 6) {
            throw new BusinessException(PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!password.equals(checkPassword)) {
            throw new BusinessException(PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (account.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account", account);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(PARAMS_ERROR, "账号重复");
            }
            // 2. 加密，使用 MD5 对密码加盐加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setAccount(account);
            user.setPassword(encryptPassword);
            boolean saveResult = save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public User userLogin(String account, String password, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(PARAMS_ERROR, "参数为空");
        }
        if (account.length() < 4) {
            throw new BusinessException(PARAMS_ERROR, "账号错误");
        }
        if (password.length() < 6) {
            throw new BusinessException(PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        queryWrapper.eq("password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (ObjectUtil.isNull(user)) {
            log.info("user login failed, account cannot match userPassword");
            throw new BusinessException(PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 通过 session 记录用户的登录状态，然后将其存入 redis 缓存当中，并设置过期时间
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        stringRedisTemplate.opsForValue().set(USER_LOGIN_STATE + user.getId(), JSONUtil.toJsonStr(user));
        stringRedisTemplate.expire(USER_LOGIN_STATE + user.getId(), USER_LOGIN_EXPIRE_TIME, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 获取当前登录用户
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (Objects.isNull(currentUser) || ObjectUtil.isEmpty(currentUser.getId())) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 先从缓存当中查询，如果查不到则从数据库当中查询
        String userJson = stringRedisTemplate.opsForValue().get(USER_LOGIN_STATE + currentUser.getId());
        if (StrUtil.isNotEmpty(userJson)) {
            currentUser = JSONUtil.toBean(userJson, User.class);
        } else {
            currentUser = this.getById(currentUser.getId());
        }
        if (ObjectUtils.isEmpty(currentUser)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否不是管理员
     */
    @Override
    public boolean isNotAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user == null || !UserConstant.ADMIN_ROLE.equals(user.getRole());
    }

    /**
     * 用户注销
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        // 移除登录态，并移除缓存当中的数据
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        stringRedisTemplate.delete(USER_LOGIN_STATE + user.getId());
        return true;
    }

}




