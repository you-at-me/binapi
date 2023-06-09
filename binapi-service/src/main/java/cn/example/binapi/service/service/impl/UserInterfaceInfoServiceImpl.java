package cn.example.binapi.service.service.impl;


import cn.example.binapi.common.common.PageRequest;
import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import cn.example.binapi.common.model.dto.userInterfaceInfo.UserInterfaceInfoAddRequest;
import cn.example.binapi.common.model.dto.userInterfaceInfo.UserInterfaceInfoUpdateRequest;
import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import cn.example.binapi.service.mapper.InterfaceInfoMapper;
import cn.example.binapi.service.mapper.UserInterfaceInfoMapper;
import cn.example.binapi.service.service.InterfaceInfoService;
import cn.example.binapi.service.service.UserInterfaceInfoService;
import cn.example.binapi.service.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.example.binapi.common.constant.RedisConstant.USER_INTERFACE_PREFIX;

/**
 * 用户操作接口关系表的服务实现类，用户接口关系表供管理员查看的
 */
@Service
@Slf4j
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo> implements UserInterfaceInfoService {

    @Resource
    private UserService userService;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        if (add && userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
            throw new BusinessException(ResponseStatus.NOT_EXIST);
        }
        if (userInterfaceInfo.getLeftNum() <= 0) {
            throw new BusinessException(ResponseStatus.COUNT_NOT_FULL);
        }
    }

    @Override
    @Transactional
    public long addUserInterfaceInfo(UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {
        if (Objects.isNull(userInterfaceInfoAddRequest)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoAddRequest, userInterfaceInfo);
        // validate
        validUserInterfaceInfo(userInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfo.setUserId(loginUser.getId());
        boolean result = save(userInterfaceInfo);
        if (!result) {
            throw new BusinessException(ResponseStatus.OPERATION_ERROR);
        }
        return 0;
    }

    @Override
    @Transactional
    public Boolean deleteUserInterfaceInfo(long userInterfaceInfoId, HttpServletRequest request) {
        if (userInterfaceInfoId <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        User u = userService.getLoginUser(request);
        UserInterfaceInfo o = getById(userInterfaceInfoId);
        if (ObjectUtil.isEmpty(o)) {
            throw new BusinessException(ResponseStatus.NOT_FOUND);
        }
        if (!o.getUserId().equals(u.getId()) && userService.isNotAdmin(request)) {
            throw new BusinessException(ResponseStatus.NO_AUTH);
        }
        boolean res = removeById(userInterfaceInfoId);
        stringRedisTemplate.opsForHash().delete(USER_INTERFACE_PREFIX, String.valueOf(userInterfaceInfoId));
        return res;
    }

    @Override
    @Transactional
    public Boolean updateUserInterfaceInfo(UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest, HttpServletRequest request) {
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfo);
        // validate
        validUserInterfaceInfo(userInterfaceInfo, false);
        boolean res = updateById(userInterfaceInfo);
        if (res) {
            Long id = userInterfaceInfoUpdateRequest.getId();
            UserInterfaceInfo o = getById(id);
            if (ObjectUtil.isEmpty(o)) throw new BusinessException(ResponseStatus.NOT_FOUND);
            // User interface information must be updated in time
            stringRedisTemplate.opsForHash().put(USER_INTERFACE_PREFIX, String.valueOf(id), JSONUtil.toJsonStr(o));
        }
        return res;
    }

    @Override
    public UserInterfaceInfo getUserInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        Object obj = stringRedisTemplate.opsForHash().get(USER_INTERFACE_PREFIX, String.valueOf(id));
        UserInterfaceInfo o = null;
        if (!ObjectUtil.isEmpty(obj)) {
            o = JSONUtil.toBean(JSONUtil.toJsonStr(obj), UserInterfaceInfo.class);
        }
        if (Objects.isNull(o)) {
            o = getById(id);
            if (ObjectUtils.isEmpty(o)) throw new BusinessException(ResponseStatus.NOT_FOUND);
            stringRedisTemplate.opsForHash().put(USER_INTERFACE_PREFIX, String.valueOf(id), JSONUtil.toJsonStr(o));
        }
        return o;
    }

    /**
     * TODO 优化代码
     * <p>
     * 性能问题：可以通过在查询用户已有的接口调用记录时，使用 SQL 的 join 操作，一次性查询出所有用户和接口的关联关系，避免多次查询数据库。同时，可以将查询出的结果缓存到内存中，避免重复查询。
     * <p>
     * 内存占用问题：可以采用分批次处理的方式，每次只处理一部分用户和接口信息，避免一次性将所有数据加载到内存中。同时，可以使用游标或者流式处理的方式，避免一次性将所有数据加载到内存中。
     * <p>
     * 事务问题：可以将整个方法的事务注解移到方法的外层，避免事务嵌套问题。如果需要对每个用户的操作单独进行事务控制，可以将每个用户的操作封装为一个独立的方法，然后在外层方法中调用这些方法，分别进行事务控制。
     * <p>
     * 代码复杂度问题：可以将代码拆分为多个方法，每个方法只负责一个功能，避免嵌套层次过深。同时，可以使用注解或者配置文件的方式，将一些配置信息（如分页大小、重试次数等）提取到外部，避免硬编码。
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 使用数据库的事务保证数据的一致性
    public void initUserInterfaceInfo() {
        // 使用分页查询，每次查询一定数量的数据，减少数据库的负担
        int pageSize = 100;
        int pageNum = 1;
        List<User> allUsers = new ArrayList<>();
        List<InterfaceInfo> allInterfaces = new ArrayList<>();
        List<UserInterfaceInfo> userInterfaces;

        IPage<InterfaceInfo> interfaceInfoPage;
        do {
            interfaceInfoPage = interfaceInfoService.page(new Page<>(pageNum, pageSize));
            allInterfaces.addAll(interfaceInfoPage.getRecords());
            pageNum++;
        } while (pageNum <= interfaceInfoPage.getPages());

        pageNum = 1;
        IPage<User> userIPage;
        do {
            userIPage = userService.page(new Page<>(pageNum, pageSize));
            allUsers.addAll(userIPage.getRecords());
            pageNum++;
        } while (pageNum <= userIPage.getPages());

        // 遍历所有用户
        for (User user : allUsers) {
            long userId = user.getId();

            userInterfaces = new ArrayList<>();
            List<UserInterfaceInfo> newUserInterfaces = new ArrayList<>();

            pageNum = 1;
            IPage<UserInterfaceInfo> userInterfaceInfoIPage;
            QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            do {
                userInterfaceInfoIPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
                userInterfaces.addAll(userInterfaceInfoIPage.getRecords());
                pageNum++;
            } while (pageNum <= userInterfaceInfoIPage.getPages());

            for (InterfaceInfo interfaceInfo : allInterfaces) {
                boolean flag = false;

                // 判断用户是否已有该接口
                for (UserInterfaceInfo userInterfaceInfo : userInterfaces) {
                    if (userInterfaceInfo.getInterfaceInfoId().equals(interfaceInfo.getId())) {
                        flag = true;
                        break;
                    }
                }

                // 如果用户没有该接口，则新增一条数据
                if (!flag) {
                    UserInterfaceInfo newUserInterface = new UserInterfaceInfo();
                    newUserInterface.setUserId(userId);
                    newUserInterface.setInterfaceInfoId(interfaceInfo.getId());
                    newUserInterface.setLeftNum(20);
                    newUserInterfaces.add(newUserInterface);
                }
            }

            // 批量插入用户接口调用次数记录，使用数据库事务保证数据的一致性
            if (newUserInterfaces != null && newUserInterfaces.size() > 0) {
                this.saveBatch(newUserInterfaces);
            }

            // 遍历用户接口调用次数记录，如果对应的接口已被删除，则删除该记录
            for (UserInterfaceInfo userInterfaceInfo : userInterfaces) {
                boolean flag = false;
                for (InterfaceInfo interfaceInfo : allInterfaces) {
                    if (userInterfaceInfo.getInterfaceInfoId().equals(interfaceInfo.getId())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    this.removeById(userInterfaceInfo.getId());
                }
            }
        }
    }

    /**
     * 接口调用次数+1,剩余次数-1
     *
     * @param interfaceInfoId 接口信息ID
     * @param userId 用户ID
     * @return boolean
     */
    @Override
    @Transactional
    public boolean invokeCount(long interfaceInfoId, long userId) {
        // 判断参数是否合法
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        // 根据ID获取接口信息对象
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interface_info_id", interfaceInfoId);
        queryWrapper.eq("user_id", userId);
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);
        if (ObjectUtil.isNull(userInterfaceInfo)) {
            throw new BusinessException(ResponseStatus.INTERFACE_EMPTY);
        }
        // 判断剩余次数是否足够
        if (userInterfaceInfo.getLeftNum() <= 0) {
            throw new BusinessException(ResponseStatus.INTERFACE_NOT_FULL);
        }
        // 构造UpdateWrapper对象，设置更新条件和更新内容
        // TODO：这里最好不要直接操作数据库，要考虑高并发多流量场景，可以使用原子类缓存加锁设计业务场景
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interface_info_id", interfaceInfoId);
        updateWrapper.eq("user_id", userId);
        updateWrapper.setSql("left_num = left_num - 1, total_num = total_num + 1");
        // 执行更新操作
        return this.update(updateWrapper);
    }

    @Override
    @Transactional
    public IPage<InterfaceInfo> getAvailableInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest, long userId) {
        if (userId == 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        PageRequest pageRequest = new PageRequest();
        pageRequest.setCurrent(current);
        pageRequest.setPageSize(size);
        pageRequest.setSortField(interfaceInfoQueryRequest.getSortField());
        pageRequest.setSortOrder(interfaceInfoQueryRequest.getSortOrder());

        Page<InterfaceInfo> page = new Page<>(current, size);
        QueryWrapper<InterfaceInfo> wrapper = new QueryWrapper<>();

        return interfaceInfoMapper.getInterfaceInfoByUserId(page, userId, wrapper);
    }
}
