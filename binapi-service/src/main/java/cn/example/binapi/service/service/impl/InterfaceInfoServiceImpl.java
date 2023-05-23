package cn.example.binapi.service.service.impl;

import cn.example.binapi.common.common.InterfaceIdRequest;
import cn.example.binapi.common.common.InterfacePurchaseRequest;
import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.common.constant.CommonConstant;
import cn.example.binapi.common.constant.UserConstant;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import cn.example.binapi.common.model.enums.InterfaceStateInfoEnum;
import cn.example.binapi.common.model.vo.InterfaceInfoVO;
import cn.example.binapi.sdk.Model.Api;
import cn.example.binapi.sdk.client.RemoteCallClient;
import cn.example.binapi.service.mapper.InterfaceInfoMapper;
import cn.example.binapi.service.service.InterfaceInfoService;
import cn.example.binapi.service.service.UserInterfaceInfoService;
import cn.example.binapi.service.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.example.binapi.common.constant.CommonConstant.*;
import static cn.example.binapi.common.constant.RedisConstant.INTERFACE_PREFIX;
import static cn.example.binapi.common.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 接口信息表实现类
 *
 * @author Carl
 */
@Service
@Slf4j
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    @Resource
    private UserService userService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean b) {
        if (ObjectUtil.isEmpty(interfaceInfo)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        if (b && StringUtils.isAnyBlank(name)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ResponseStatus.PARAMS_TOO_LONG);
        }
    }

    @Override
    @Transactional
    public long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (Objects.isNull(interfaceInfoAddRequest)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        // 把当前新增的这条接口信息设置创建人ID
        interfaceInfo.setUserId(loginUser.getId());
        // 将接口信息保存在数据库当中，等接口被调用的时候再将接口信息数据缓存到 redis 当中
        boolean result = save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ResponseStatus.OPERATION_ERROR);
        }
        // 新增成功直接返回接口的ID
        return interfaceInfo.getId();
    }

    @Override
    @Transactional
    public boolean deleteInterfaceInfo(long id, HttpServletRequest request) {
        if (ObjectUtil.isEmpty(id) || id <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = getById(id);
        if (ObjectUtils.isEmpty(oldInterfaceInfo)) {
            throw new BusinessException(ResponseStatus.NOT_FOUND);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && userService.isNotAdmin(request)) {
            throw new BusinessException(ResponseStatus.NO_AUTH);
        }
        // 先删除数据库再删除缓存
        boolean isRemove = removeById(id);
        Object o = stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, String.valueOf(id));
        if (ObjectUtil.isNotEmpty(o)) {
            stringRedisTemplate.opsForHash().delete(INTERFACE_PREFIX, String.valueOf(id));
        }
        return isRemove;
    }

    @Override
    @Transactional
    public boolean updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo o = getById(id);
        if (Objects.isNull(o)) {
            throw new BusinessException(ResponseStatus.NOT_FOUND);
        }
        User user = userService.getLoginUser(request);
        // 仅本人或管理员可修改，更新操作不用更新缓存，只有接口被调用的时候再去构建缓存
        if (!o.getUserId().equals(user.getId()) && userService.isNotAdmin(request)) {
            throw new BusinessException(ResponseStatus.NO_AUTH);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        validInterfaceInfo(interfaceInfo, false);

        boolean res = updateById(interfaceInfo);
        if (res) {
            // 接口信息必须要及时更新，尤其接口的调用次数要实时
            stringRedisTemplate.opsForHash().put(INTERFACE_PREFIX, String.valueOf(id), JSONUtil.toJsonStr(o));
        }
        return res;
    }

    @Override
    public Boolean onlineInterfaceInfo(InterfaceInfoInvokeRequest idRequest, HttpServletRequest request) {
        if (ObjectUtil.isNull(idRequest) || idRequest.getId() <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        Object obj = stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, String.valueOf(id));
        InterfaceInfo o = null;
        if (!ObjectUtil.isEmpty(obj)) {
            o = JSONUtil.toBean(JSONUtil.toJsonStr(obj), InterfaceInfo.class);
        }
        if (Objects.isNull(o)) o = getById(id);
        if (ObjectUtil.isEmpty(o)) throw new BusinessException(ResponseStatus.INTERFACE_EMPTY);
        // 仅本人或管理员可修改该接口的状态
        User u = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (!o.getUserId().equals(u.getId()) && userService.isNotAdmin(request)) {
            throw new BusinessException(ResponseStatus.NO_AUTH);
        }
        // 验证接口是否可以被远程调用
        String res = onlineInvokeInterface(idRequest, u);
        if (INTERFACE_CALL_FAILED.equals(res)) {
            throw new BusinessException(ResponseStatus.INTERFACE_NOT_USED);
        }
        // 更新状态，进行发布，也就是将状态置为 1
        InterfaceInfo newInterfaceInfo = new InterfaceInfo();
        newInterfaceInfo.setId(id);
        newInterfaceInfo.setStatus(InterfaceStateInfoEnum.ONLINE.getValue());
        boolean result = updateById(newInterfaceInfo);
        Object strObj = stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, String.valueOf(id));
        if (!ObjectUtil.isEmpty(strObj)) {
            // 再次查询如果存在，则删除 redis 中旧的缓存数据，只有当该条数据再次被调用时才将其缓存当 redis 当中
            stringRedisTemplate.opsForHash().delete(INTERFACE_PREFIX, id);
        }
        return result;
    }

    private String onlineInvokeInterface(InterfaceInfoInvokeRequest idRequest, User u) {
        String result = getRemoteResult(idRequest, u);
        return StrUtil.isBlank(result) ? INTERFACE_CALL_FAILED : result;
    }

    private String getRemoteResult(InterfaceInfoInvokeRequest infoRequest, User u) {
        StringBuilder requestParams = new StringBuilder(); // 防止trim报npe
        if (infoRequest.getRequestParams() != null) {
            requestParams = new StringBuilder(infoRequest.getRequestParams().trim());
        }
        String method = infoRequest.getMethod();
        String url = infoRequest.getUrl();
        if (StringUtils.isAnyBlank(method, url)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        Api api = new Api();
        api.setInterfaceId(String.valueOf(infoRequest.getId()));
        api.setId(u.getId());
        api.setAccount(u.getAccount());
        api.setBody(requestParams);
        api.setUrl(url);
        api.setMethod(method);
        Integer appId = RandomUtil.randomInt(10000);
        stringRedisTemplate.opsForValue().set(APPID, String.valueOf(appId));
        stringRedisTemplate.expire(APPID, APPID_EXPIRE, TimeUnit.SECONDS);
        // Remote call, passing in the current user to obtain the corresponding universal identifier and secret key
        RemoteCallClient remoteCallClient = new RemoteCallClient(appId, u.getAccessKey(), u.getSecretKey());
        return remoteCallClient.getResult(api);
    }

    @Override
    public Boolean offlineInterfaceInfo(InterfaceIdRequest idRequest) {
        if (Objects.isNull(idRequest) || idRequest.getId() == 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        Object obj = stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, id);
        InterfaceInfo o = JSONUtil.toBean(JSONUtil.toJsonStr(obj), InterfaceInfo.class);
        if (Objects.isNull(o)) {
            o = getById(id);
            if (ObjectUtil.isEmpty(o)) {
                throw new BusinessException(ResponseStatus.NOT_FOUND);
            }
        }
        // update interface status
        InterfaceInfo newInterfaceInfo = new InterfaceInfo();
        newInterfaceInfo.setId(id);
        newInterfaceInfo.setStatus(InterfaceStateInfoEnum.OFFLINE.getValue());
        boolean result = updateById(newInterfaceInfo);

        if (!Objects.isNull(stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, String.valueOf(id)))) {
            stringRedisTemplate.opsForHash().delete(INTERFACE_PREFIX, String.valueOf(id));
        }
        return result;
    }

    @Override
    @Transactional
    public String purchaseInterface(InterfacePurchaseRequest interfacePurchaseRequest, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        Long interfaceId = interfacePurchaseRequest.getId();
        InterfaceInfo interfaceInfo = getById(interfaceId);
        if (ObjectUtil.isEmpty(interfaceInfo)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        UpdateWrapper<InterfaceInfo> updateInterface = new UpdateWrapper<>();
        Long userId = user.getId();
        int purchaseNum = interfacePurchaseRequest.getPurchaseNum();
        updateInterface.eq("id", interfaceId);
        updateInterface.eq("user_id", userId);
        updateInterface.setSql("left_num = left_num - " + purchaseNum);
        // TODO Calculate the total price of the interface, then pay, and the deduction can only be called after the response is successful
        boolean u = update(updateInterface);
        boolean s = false;
        if (u) { // If the interface information table deducts the number of remaining interfaces called successfully, then assume that the information that the user operates the interface exists
            UpdateWrapper<UserInterfaceInfo> updateUserInterface = new UpdateWrapper<>();
            updateUserInterface.eq("user_id", userId);
            updateUserInterface.eq("interface_info_id", interfaceId);
            updateUserInterface.setSql("left_num = left_num + " + purchaseNum);
            s = userInterfaceInfoService.update(updateUserInterface);
            if (!s) { // If it fails, it means a new user, then add user interface info
                UserInterfaceInfo o = new UserInterfaceInfo();
                o.setUserId(userId);
                o.setInterfaceInfoId(interfaceId);
                o.setLeftNum(purchaseNum);
                o.setStatus(interfaceInfo.getStatus());
                s = userInterfaceInfoService.save(o);
            }
        }
        if (!s && u) { // s failed, to ensure atomicity, perform the following operations
            u = false;
            while (!u) { // Use loops to solve the following database update problems
                u = updateById(interfaceInfo); // Tips:What to do if the update fails here？then loop
            }
        }
        return u && s ? INTERFACE_PURCHASE_SUCCESS : INTERFACE_PURCHASE_FAILED;
    }

    @Override
    public String invokeInterface(InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        if (Objects.isNull(interfaceInfoInvokeRequest) || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        // To determine whether the interface exists, first obtain it from the cache
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(INTERFACE_PREFIX);
        log.info("map::" + map);
        InterfaceInfo o = null;
        Object m;
        long id = interfaceInfoInvokeRequest.getId();
        if (ObjectUtil.isEmpty(map) || Objects.isNull(m = map.get(id)) || ObjectUtils.isEmpty(o = JSONUtil.toBean(JSONUtil.toJsonStr(m), InterfaceInfo.class))) {
            log.info("o:" + o);
            o = getById(id);
            if (ObjectUtil.isNull(o)) throw new BusinessException(ResponseStatus.NOT_FOUND);
            String interfaceJson = JSONUtil.toJsonStr(o);
            stringRedisTemplate.opsForHash().put(INTERFACE_PREFIX, String.valueOf(id), interfaceJson);
        }
        if (o.getStatus() == InterfaceStateInfoEnum.OFFLINE.getValue()) {
            throw new BusinessException(ResponseStatus.INTERFACE_CLOSURE);
        }
        User user = userService.getLoginUser(request);
        String result = getRemoteResult(interfaceInfoInvokeRequest, user);
        if (StrUtil.isBlank(result)) return INTERFACE_CALL_FAILED;
        // 在接口被成功调用的时候，远程接口网关已经对调用次数做统计了，这里只更新缓存直接覆盖即可。
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(); // TODO 可单独开启一个线程去执行
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.eq("interface_info_id", id);
        UserInterfaceInfo u = userInterfaceInfoService.getOne(queryWrapper);
        stringRedisTemplate.opsForHash().put(INTERFACE_PREFIX, String.valueOf(id), JSONUtil.toJsonStr(u));
        log.info(result);
        return result;
    }

    @Override
    public InterfaceInfo getInterfaceInfoById(long id, HttpServletRequest request) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        InterfaceInfo o = getById(id);
        if (ObjectUtil.isEmpty(o)) {
            throw new BusinessException(ResponseStatus.NOT_FOUND);
        }
        return o;
    }

    @Override
    public List<InterfaceInfo> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (!ObjectUtil.isEmpty(interfaceInfoQueryRequest)) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        User u = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (u.getRole().equals(UserConstant.DEFAULT_ROLE)) {
            QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", u.getId());
            List<UserInterfaceInfo> userInterfaceInfos = userInterfaceInfoService.list(queryWrapper);
            List<InterfaceInfo> res = new ArrayList<>();
            userInterfaceInfos.forEach(ui -> res.add(getById(ui.getInterfaceInfoId())));
            return res;
        }
        // 先尝试通过 hashmap 从缓存中获取，查询不到则从数据库当中查找
        List<Object> interfaceInfoLists = stringRedisTemplate.opsForHash().values(INTERFACE_PREFIX);
        if (!ObjectUtil.isEmpty(interfaceInfoLists)) {
            return interfaceInfoLists.stream().map(interfaceInfo -> (InterfaceInfo) interfaceInfo).collect(Collectors.toList());
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> list = list(queryWrapper);
        Map<String, String> map = new HashMap<>();
        list.forEach(info -> map.put(info.getId().toString(), JSONUtil.toJsonStr(info)));
        stringRedisTemplate.opsForHash().putAll(INTERFACE_PREFIX, map);
        return list;
    }

    @Override
    public Page<InterfaceInfo> interfaceInfoService(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // content 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return page(new Page<>(current, size), queryWrapper);
    }

    @Override
    public List<InterfaceInfoVO> getInterfaceInfoTotalInvokeCount() {
        return interfaceInfoMapper.getInterfaceTotalInvokeCounts();
    }
}




