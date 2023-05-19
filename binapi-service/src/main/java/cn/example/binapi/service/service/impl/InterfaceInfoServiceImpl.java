package cn.example.binapi.service.service.impl;

import cn.example.binapi.common.common.InterfaceIdRequest;
import cn.example.binapi.common.constant.CommonConstant;
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
import cn.example.binapi.service.common.ErrorCode;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.mapper.InterfaceInfoMapper;
import cn.example.binapi.service.service.InterfaceInfoService;
import cn.example.binapi.service.service.UserInterfaceInfoService;
import cn.example.binapi.service.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.example.binapi.common.constant.RedisConstant.INTERFACE_PREFIX;

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
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        if (b) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() < 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }

    @Override
    public long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (Objects.isNull(interfaceInfoAddRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        // 把当前新增的这条接口信息设置创建人ID
        interfaceInfo.setCreator(loginUser.getId());
        // 将接口信息保存在数据库当中，等接口被调用的时候再将接口信息数据缓存到 redis 当中
        boolean result = save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 新增成功直接返回接口的ID
        return interfaceInfo.getId();
    }

    @Override
    public boolean deleteInterfaceInfo(long id, HttpServletRequest request) {
        if (ObjectUtil.isEmpty(id) || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = getById(id);
        if (ObjectUtils.isEmpty(oldInterfaceInfo)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getCreator().equals(user.getId()) && userService.isNotAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 先删除数据库再删除缓存
        boolean isRemove = removeById(id);
        Object objInterfaceInfo = stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, id);
        if (ObjectUtil.isNotEmpty(objInterfaceInfo)) {
            stringRedisTemplate.opsForHash().delete(INTERFACE_PREFIX, id);
        }
        return isRemove;
    }

    @Override
    public boolean updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getCreator().equals(user.getId()) && userService.isNotAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return updateById(interfaceInfo);
    }

    @Override
    public Boolean onlineInterfaceInfo(InterfaceInfoInvokeRequest idRequest, HttpServletRequest request) {
        if (ObjectUtil.isNull(idRequest) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断接口是否存在
        long id = idRequest.getId();
        log.info("id: {}", id);
        String interfaceInfoJson = Objects.requireNonNull(stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, id)).toString();
        InterfaceInfo oldInterfaceInfo = JSONUtil.toBean(interfaceInfoJson, InterfaceInfo.class);
        if (ObjectUtils.isEmpty(oldInterfaceInfo)) { // 如果为空则从数据库当中查找
            oldInterfaceInfo = getById(id);
        }
        if (Objects.isNull(oldInterfaceInfo)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 验证接口是否可以调用，修改为远程调用
        String res = invokeInterface(idRequest, request);
        if (StrUtil.isBlank(res)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
        }
        // 更新状态，进行发布，也就是将状态置为 1 , 仅本人或者管理员才可修改
        InterfaceInfo newInterfaceInfo = new InterfaceInfo();
        newInterfaceInfo.setId(id);
        newInterfaceInfo.setStatus(InterfaceStateInfoEnum.ONLINE.getValue());
        boolean result = updateById(newInterfaceInfo);
        if (!ObjectUtil.isEmpty(stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, id))) {
            // 再次查询如果存在，则删除 redis 中旧的缓存数据，只有当该条数据再次被调用时才将其缓存当 redis 当中
            stringRedisTemplate.opsForHash().delete(INTERFACE_PREFIX, id);
        }
        return result;
    }

    @Override
    public Boolean offlineInterfaceInfo(InterfaceIdRequest idRequest) {

        if (Objects.isNull(idRequest) || idRequest.getId() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        String interfaceInfoJson = Objects.requireNonNull(stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, id)).toString();
        InterfaceInfo oldInterfaceInfo = JSONUtil.toBean(interfaceInfoJson, InterfaceInfo.class);
        if (Objects.isNull(oldInterfaceInfo)) {
            oldInterfaceInfo = getById(id);
            if (ObjectUtil.isEmpty(oldInterfaceInfo)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
        }
        // 更新接口状态
        InterfaceInfo newInterfaceInfo = new InterfaceInfo();
        newInterfaceInfo.setId(id);
        newInterfaceInfo.setStatus(InterfaceStateInfoEnum.OFFLINE.getValue());
        boolean result = updateById(newInterfaceInfo);

        if (!Objects.isNull(stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, id))) {
            stringRedisTemplate.delete(INTERFACE_PREFIX + id);
        }
        return result;
    }

    @Override
    public String invokeInterface(InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = interfaceInfoInvokeRequest.getId();
        StringBuilder userRequestParams = new StringBuilder(); // 防止trim报npe
        if (interfaceInfoInvokeRequest.getRequestParams() != null) {
            userRequestParams = new StringBuilder(interfaceInfoInvokeRequest.getRequestParams().trim());
        }
        String method = interfaceInfoInvokeRequest.getMethod();
        String url = interfaceInfoInvokeRequest.getUrl();
        log.info("invoke...userRequestParams: {}", userRequestParams);
        log.info("invoke...method: {}", method);
        log.info("invoke...url: {}", url);

        // 判断接口是否存在，首先从缓存当中获取
        String interfaceInfoJson = Objects.requireNonNull(stringRedisTemplate.opsForHash().get(INTERFACE_PREFIX, id)).toString();
        InterfaceInfo oldInterfaceInfo = JSONUtil.toBean(interfaceInfoJson, InterfaceInfo.class);
        if (ObjectUtil.isEmpty(oldInterfaceInfo)) oldInterfaceInfo = getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断接口是否已关闭
        if (oldInterfaceInfo.getStatus() == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口已关闭");
        }
        // 判断请求方法、请求地址是否为空
        if (StringUtils.isAnyBlank(method, url)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 接口调用，首先获取当前用户，然后获得对应的通用标识符和秘钥
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();

        Api api = new Api();
        api.setInterfaceId(String.valueOf(id));
        api.setId(loginUser.getId());
        api.setAccount(loginUser.getAccount());
        api.setBody(userRequestParams);
        api.setUrl(url);
        api.setMethod(method);

        Integer appId = 123; // TODO appid
        RemoteCallClient remoteCallClient = new RemoteCallClient(appId, accessKey, secretKey);
        return remoteCallClient.getResult(api);
    }

    @Override
    public InterfaceInfo getInterfaceInfoById(long id, HttpServletRequest request) {
        if (ObjectUtil.isEmpty(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = getById(id);
        if (interfaceInfo == null || interfaceInfo.getId() == null || interfaceInfo.getId() <= 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User user = userService.getLoginUser(request);
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("creator", user.getId());
        queryWrapper.eq("interface_info_id", interfaceInfo.getId());
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        interfaceInfo.setLeftNum(userInterfaceInfo.getLeftNum());
        return getById(id);
    }

    @Override
    public List<InterfaceInfo> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        // 先尝试通过 hashmap从缓存中获取，查询不到则从数据库当中查找
        List<Object> interfaceInfoLists = stringRedisTemplate.opsForHash().values(INTERFACE_PREFIX);
        if (!ObjectUtil.isEmpty(interfaceInfoLists)) {
            return interfaceInfoLists.stream().map(interfaceInfo -> (InterfaceInfo) interfaceInfo).collect(Collectors.toList());
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        return list(queryWrapper);
    }

    @Override
    public Page<InterfaceInfo> interfaceInfoService(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
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




