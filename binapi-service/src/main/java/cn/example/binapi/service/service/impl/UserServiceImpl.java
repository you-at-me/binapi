package cn.example.binapi.service.service.impl;

import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.common.constant.UserConstant;
import cn.example.binapi.common.model.dto.user.UserAddRequest;
import cn.example.binapi.common.model.dto.user.UserUpdateRequest;
import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.mapper.UserMapper;
import cn.example.binapi.service.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static cn.example.binapi.common.common.ResponseStatus.*;
import static cn.example.binapi.common.constant.CommonConstant.SALT;
import static cn.example.binapi.common.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String account, String password, String checkPassword, String phoneOrMail) { // 另一种注册方式，手机号和邮箱注册防水板
        // 1. 校验
        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            throw new BusinessException(PARAMS_EMPTY);
        }
        if (account.length() < 4) {
            throw new BusinessException(ACCOUNT_SHORT);
        }
        if (account.length() > 16) {
            throw new BusinessException(ACCOUNT_LONG);
        }
        if (password.length() < 6 || checkPassword.length() < 6) {
            throw new BusinessException(PASSWORD_SHORT);
        }
        if (password.length() > 20 || checkPassword.length() > 20) {
            throw new BusinessException(PASSWORD_LONG);
        }
        // 密码和校验密码相同
        if (!password.equals(checkPassword)) {
            throw new BusinessException(PASSWORD_NOT_COMPLIANT);
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ACCOUNT_EXIST);
        }
        // 2. 加密，使用 MD5 对密码加盐加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 3. 分配accessKey、secretKey
        String accessKey = DigestUtil.md5Hex(SALT + account + RandomUtil.randomNumbers(4));
        String secretKey = DigestUtil.md5Hex(SALT + account + RandomUtil.randomNumbers(8));

        // 加锁:对于任意两个字符串s和t, s.intern() == t.intern()是true则当且仅当s.equals(t)是true
        synchronized (account.intern()) { // 利用到了字符串的不可变性，用户账号必须是唯一的特性来加锁的
            // 4. 插入数据
            User user = new User();
            user.setAccount(account);
            user.setPassword(encryptPassword);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            boolean saveResult = save(user);
            if (!saveResult) {
                throw new BusinessException(ResponseStatus.REGISTER_ERROR);
            }
            return user.getId();
        }
    }

    @Override
    public User userLogin(String account, String password, HttpServletRequest request) {
        // 1. 数据校验
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(PARAMS_EMPTY);
        }
        if (account.length() < 4) {
            throw new BusinessException(ACCOUNT_ERROR);
        }
        if (password.length() < 6) {
            throw new BusinessException(PASSWORD_ERROR);
        }
        // 2. 加密校验密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        queryWrapper.eq("password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (ObjectUtil.isNull(user)) {
            log.info("user login failed, account cannot match userPassword");
            throw new BusinessException(NOT_ERROR);
        }
        if (ObjectUtil.isEmpty(request.getSession().getAttribute(USER_LOGIN_STATE))) { // 防止频繁创建session
            // 3. 如果用户状态 session 没有存过，则记录用户登录状态，将其存入 redis 缓存当中，并设置过期时间
            request.getSession().setAttribute(USER_LOGIN_STATE, user);
        }
        // stringRedisTemplate.opsForValue().set(USER_LOGIN_STATE + user.getId(), JSONUtil.toJsonStr(user));
        // stringRedisTemplate.expire(USER_LOGIN_STATE + user.getId(), USER_LOGIN_EXPIRE_TIME, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 用户注销
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (ObjectUtil.isEmpty(request)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ResponseStatus.NOT_LOGIN);
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 根据请求携带的Cookie和这里取的属性key将其Cookie值解析成对应当初所存入的值，这里当初所存入的值是User对象; 如果没有携带请求头Cookie或者携带错误的Cookie都将解析失败，找不到对应的用户User对象。
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (ObjectUtil.isEmpty(currentUser) || currentUser.getId() <= 0) {
            throw new BusinessException(ResponseStatus.NOT_LOGIN);
        }
        return currentUser;
    }

    @Override
    public long addUser(UserAddRequest userAddRequest) {
        if (ObjectUtils.isEmpty(userAddRequest)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        // 1. 数据校验
        String account = userAddRequest.getAccount();
        String password = userAddRequest.getPassword();
        if (account.length() < 4) {
            throw new BusinessException(ACCOUNT_SHORT);
        }
        if (account.length() > 16) {
            throw new BusinessException(ACCOUNT_LONG);
        }
        if (password.length() < 6) {
            throw new BusinessException(PASSWORD_SHORT);
        }
        if (password.length() > 20) {
            throw new BusinessException(PASSWORD_LONG);
        }
        User user = new User();
        // 2. 加密，使用 MD5 对密码加盐加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 3. 分配accessKey、secretKey
        String accessKey = DigestUtil.md5Hex(SALT + account + RandomUtil.randomNumbers(4));
        String secretKey = DigestUtil.md5Hex(SALT + account + RandomUtil.randomNumbers(8));
        BeanUtils.copyProperties(userAddRequest, user);
        user.setPassword(encryptPassword);
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);
        boolean result = save(user);
        if (!result) {
            throw new BusinessException(ResponseStatus.OPERATION_ERROR);
        }
        return user.getId();
    }

    @Override
    public boolean updateUser(UserUpdateRequest userUpdateRequest) {
        if (ObjectUtil.isEmpty(userUpdateRequest)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        String account = userUpdateRequest.getAccount();
        if (account.length() < 4) {
            throw new BusinessException(ACCOUNT_SHORT);
        }
        if (account.length() > 16) {
            throw new BusinessException(ACCOUNT_LONG);
        }
        User user = getById(userUpdateRequest.getId());
        if (ObjectUtil.isNull(user)) {
            throw new BusinessException(USER_NOT_EXIST);
        }
        String password = userUpdateRequest.getPassword();
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        User u = new User();
        BeanUtils.copyProperties(userUpdateRequest, u);
        if (!user.getPassword().equals(encryptPassword)) {
            u.setPassword(encryptPassword);
        }
        u.setPassword(user.getPassword());
        return updateById(u);
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

    @Override
    public Integer getStars() {
        // // 从缓存查询
        // Integer redisStars = (Integer) redisTemplate.opsForValue().get(GITHUB_STARS_PREFIX);
        // if (redisStars != null) {
        //     return redisStars;
        // }
        //
        // // 获取github stars
        // String listContent;
        // try {
        //     listContent= HttpUtil.get("https://img.shields.io/github/stars/AliasJeff?style=social");
        // }catch (Exception e){
        //     throw new BusinessException(ResponseStatus.OPERATION_ERROR,"获取GitHub Starts 超时");
        // }
        // //该操作查询时间较长
        // List<String> titles = ReUtil.findAll("<title>(.*?)</title>", listContent, 1);
        // String str = null;
        // for (String title : titles) {
        //     //打印标题
        //     String[] split = title.split(":");
        //     str = split[1];
        // }
        //
        // Integer githubStars = Integer.parseInt(str.trim());
        //
        // // 获取gitee star数（可能超出请求限制）
        // String owner = "AliasJeff";
        // String repo1 = "alias-openapi-frontend";
        // String repo2 = "alias-openapi-backend";
        // String url1 = "https://gitee.com/api/v5/repos/" + owner + "/" + repo1;
        // String url2 = "https://gitee.com/api/v5/repos/" + owner + "/" + repo2;
        //
        // OkHttpClient client = new OkHttpClient();
        //
        // Integer giteeStars = 0;
        // try {
        //     Integer starCount1 = getStarCount(client, url1);
        //     Integer starCount2 = getStarCount(client, url2);
        //     giteeStars = starCount1 + starCount2;
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        //
        // // 加入缓存
        // redisTemplate.opsForValue().set(GITHUB_STARS_PREFIX, giteeStars + githubStars, 1, TimeUnit.MINUTES);
        //
        // return githubStars + giteeStars;
        return 1000;
    }

    // private static Integer getStarCount(OkHttpClient client, String url) throws Exception {
    //     Request request = new Request.Builder()
    //             .url(url)
    //             .build();
    //
    //     Response response = client.newCall(request).execute();
    //     String responseData = response.body().string();
    //     System.out.println(responseData);
    //     JSONObject jsonObject = new JSONObject(responseData);
    //     return jsonObject.getInt("stargazers_count");
    // }

}




