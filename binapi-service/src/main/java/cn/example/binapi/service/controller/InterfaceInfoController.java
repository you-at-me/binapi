package cn.example.binapi.service.controller;

import cn.example.binapi.common.common.BaseResponse;
import cn.example.binapi.common.common.InterfaceIdRequest;
import cn.example.binapi.common.common.InterfacePurchaseRequest;
import cn.example.binapi.common.common.ResultUtils;
import cn.example.binapi.common.constant.UserConstant;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.vo.InterfaceInfoVO;
import cn.example.binapi.service.annotation.AuthCheck;
import cn.example.binapi.service.annotation.RateLimiter;
import cn.example.binapi.service.service.InterfaceInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    // region 增删改查

    /**
     * 新增接口
     */
    @PostMapping("add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoService.addInterfaceInfo(interfaceInfoAddRequest, request));
    }

    /**
     * 删除接口
     */
    @PostMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteInterfaceInfo(@PathVariable("id") long id, HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoService.deleteInterfaceInfo(id, request));
    }

    /**
     * 更新接口
     */
    @PostMapping("update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoService.updateInterfaceInfo(interfaceInfoUpdateRequest, request));
    }

    /**
     * 上线接口
     */
    @PostMapping("online")
    @RateLimiter(limit = 100, milTimeout = 10)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest idRequest, HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoService.onlineInterfaceInfo(idRequest, request));
    }

    /**
     * 下线接口
     */
    @PostMapping("offline")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody InterfaceIdRequest idRequest) {
        return ResultUtils.success(interfaceInfoService.offlineInterfaceInfo(idRequest));
    }

    /**
     * 接口购买，确定购买次数，给某个用户分配某条接口的权限
     */
    @PostMapping("purchase")
    public BaseResponse<String> purchaseInterface(@RequestBody InterfacePurchaseRequest interfacePurchaseRequest, HttpServletRequest request) {
        String res = interfaceInfoService.purchaseInterface(interfacePurchaseRequest, request);
        return ResultUtils.success(res);
    }

    /**
     * 接口调用
     */
    @PostMapping("invoke")
    @RateLimiter
    public BaseResponse<Object> invokeInterface(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        String result = interfaceInfoService.invokeInterface(interfaceInfoInvokeRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取接口的详细信息
     */
    @GetMapping("/get/{id}")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(@PathVariable long id, HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoById(id, request));
    }

    /**
     * 根据用户身份获取接口列表信息
     */
    @GetMapping("list")
    @AuthCheck(anyRole = {UserConstant.ADMIN_ROLE, UserConstant.DEFAULT_ROLE})
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest r) {
        return ResultUtils.success(interfaceInfoService.listInterfaceInfo(interfaceInfoQueryRequest, r));
    }

    /**
     * 分页获取列表
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        return ResultUtils.success(interfaceInfoService.interfaceInfoService(interfaceInfoQueryRequest));
    }

    @GetMapping("/getInterfaceCount")
    public BaseResponse<Integer> getInterfaceCount() {
        Integer count = Math.toIntExact(interfaceInfoService.count());
        return ResultUtils.success(count);
    }

    /**
     * 接口信息表与用户接口信息表关联查询，查询出每个被调用的接口信息及其各自被调用的总次数
     */
    @GetMapping("/getInterfaceInvokeCount")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<InterfaceInfoVO>> getInterfaceInvokeCount() {
        List<InterfaceInfoVO> list = interfaceInfoService.getInterfaceInfoTotalInvokeCount();
        return ResultUtils.success(list);
    }

    // endregion

}
