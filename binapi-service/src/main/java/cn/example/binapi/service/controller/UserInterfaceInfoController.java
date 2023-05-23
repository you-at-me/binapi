package cn.example.binapi.service.controller;


import cn.example.binapi.common.common.BaseResponse;
import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.common.common.ResultUtils;
import cn.example.binapi.common.constant.CommonConstant;
import cn.example.binapi.common.constant.UserConstant;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import cn.example.binapi.common.model.dto.userInterfaceInfo.UserInterfaceInfoAddRequest;
import cn.example.binapi.common.model.dto.userInterfaceInfo.UserInterfaceInfoQueryRequest;
import cn.example.binapi.common.model.dto.userInterfaceInfo.UserInterfaceInfoUpdateRequest;
import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import cn.example.binapi.service.annotation.AuthCheck;
import cn.example.binapi.service.mapper.UserInterfaceInfoMapper;
import cn.example.binapi.service.service.InterfaceInfoService;
import cn.example.binapi.service.service.UserInterfaceInfoService;
import cn.example.binapi.service.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.example.binapi.common.constant.RedisConstant.USER_INTERFACE_PREFIX;


@RestController
@RequestMapping("/userInterfaceInfo")
@Slf4j
@CrossOrigin
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping("add")
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {
        return ResultUtils.success(userInterfaceInfoService.addUserInterfaceInfo(userInterfaceInfoAddRequest, request));
    }

    @PostMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@PathVariable("id") long userInterfaceInfoId, HttpServletRequest request) {
        return ResultUtils.success(userInterfaceInfoService.deleteUserInterfaceInfo(userInterfaceInfoId, request));
    }

    @PostMapping("update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest, HttpServletRequest request) {
        return ResultUtils.success(userInterfaceInfoService.updateUserInterfaceInfo(userInterfaceInfoUpdateRequest, request));
    }

    /**
     * 根据 id 获取
     */
    @GetMapping("/get/{id}")
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoById(@PathVariable("id") long id) {
        return ResultUtils.success(userInterfaceInfoService.getUserInterfaceInfoById(id));
    }

    @GetMapping("available")
    public BaseResponse<IPage<InterfaceInfo>> getAvailableInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null || loginUser.getId() == 0) {
            throw new BusinessException(ResponseStatus.USER_NOT_EXIST);
        }

        IPage<InterfaceInfo> interfaceInfoIPage = (IPage<InterfaceInfo>) redisTemplate.opsForValue().get(USER_INTERFACE_PREFIX + interfaceInfoQueryRequest.toString());
        if (interfaceInfoIPage != null) {
            return ResultUtils.success(interfaceInfoIPage);
        }

        long userId = loginUser.getId();
        interfaceInfoIPage = userInterfaceInfoService.getAvailableInterfaceInfo(interfaceInfoQueryRequest, userId);
        redisTemplate.opsForValue().set(USER_INTERFACE_PREFIX + interfaceInfoIPage.toString(), interfaceInfoIPage, 60, TimeUnit.MINUTES);
        return ResultUtils.success(interfaceInfoIPage);
    }

    /**
     * 单个用户获取自己的接口列表信息
     */
    @GetMapping("/list/{id}")
    public BaseResponse<List<UserInterfaceInfo>> listSingleUserInterfaceInfo(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest, @PathVariable("id") long userId) {
        // TODO 每个用户自己应该也可以查看自己购买的接口信息列表
        return ResultUtils.success(null);
    }

    /**
     * 获取列表（仅管理员可使用）
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<UserInterfaceInfo>> listUserInterfaceInfo(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        UserInterfaceInfo userInterfaceInfoQuery = new UserInterfaceInfo();
        if (userInterfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoQuery);
        }
        List<UserInterfaceInfo> userInterfaceInfoList = (List<UserInterfaceInfo>) redisTemplate.opsForValue().get(USER_INTERFACE_PREFIX + userInterfaceInfoQuery.toString());
        if (userInterfaceInfoList != null) {
            return ResultUtils.success(userInterfaceInfoList);
        }

        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfoQuery);
        userInterfaceInfoList = userInterfaceInfoService.list(queryWrapper);
        redisTemplate.opsForValue().set(USER_INTERFACE_PREFIX + userInterfaceInfoQuery, userInterfaceInfoList, 60, TimeUnit.MINUTES);
        return ResultUtils.success(userInterfaceInfoList);
    }

    /**
     * 分页获取列表
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceInfoByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest, HttpServletRequest request) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        UserInterfaceInfo userInterfaceInfoQuery = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoQueryRequest, userInterfaceInfoQuery);
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();

        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }

        Page<UserInterfaceInfo> userInterfaceInfoPage = (Page<UserInterfaceInfo>) redisTemplate.opsForValue().get(USER_INTERFACE_PREFIX + userInterfaceInfoQuery);
        if (userInterfaceInfoPage != null) {
            return ResultUtils.success(userInterfaceInfoPage);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userInterfaceInfoQuery);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size), queryWrapper);
        redisTemplate.opsForValue().set(USER_INTERFACE_PREFIX + userInterfaceInfoQuery, userInterfaceInfoPage, 60, TimeUnit.MINUTES);
        return ResultUtils.success(userInterfaceInfoPage);
    }

    @GetMapping("getInvokeCount")
    public BaseResponse<Integer> getInvokeCount(HttpServletRequest request) {
        // 创建Wrapper对象
        QueryWrapper<UserInterfaceInfo> wrapper = new QueryWrapper<>();
        // 查询条件
        wrapper.eq("is_delete", 0);
        // 聚合函数sum()
        wrapper.select("sum(total_num) as totalNum");
        // 执行查询
        Map<String, Object> resultMap = userInterfaceInfoMapper.selectMaps(wrapper).get(0);
        // 获取总调用次数的总和
        return ResultUtils.success(Integer.parseInt(resultMap.get("totalNum").toString()));
    }

    @GetMapping("/getStars")
    public BaseResponse<Integer> getStars(HttpServletRequest request) throws IOException {
        return ResultUtils.success(userService.getStars());
    }

}
