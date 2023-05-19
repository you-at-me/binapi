package cn.example.binapi.service.controller;

import cn.example.binapi.common.common.InterfaceIdRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.vo.InterfaceInfoVO;
import cn.example.binapi.service.annotation.AuthCheck;
import cn.example.binapi.service.common.BaseResponse;
import cn.example.binapi.service.common.ResultUtils;
import cn.example.binapi.service.service.InterfaceInfoService;
import cn.example.binapi.service.service.UserService;
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
@RequestMapping("/interface_info")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;


    // region 增删改查

    /**
     * 新增接口
     */
    @PostMapping("add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoService.addInterfaceInfo(interfaceInfoAddRequest, request));
    }

    /**
     * 删除
     */
    @PostMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteInterfaceInfo(@PathVariable("id") long id, HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoService.deleteInterfaceInfo(id, request));
    }

    /**
     * 更新
     */
    @PostMapping("update")
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoService.updateInterfaceInfo(interfaceInfoUpdateRequest, request));
    }

    /**
     * 上线接口
     */
    @PostMapping("online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest idRequest, HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoService.onlineInterfaceInfo(idRequest, request));
    }

    /**
     * 下线接口
     */
    @PostMapping("offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody InterfaceIdRequest idRequest) {
        return ResultUtils.success(interfaceInfoService.offlineInterfaceInfo(idRequest));
    }

    /**
     * 接口调用
     */
    @PostMapping("invoke")
    public BaseResponse<Object> invokeInterface(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) {
        String result =  interfaceInfoService.invokeInterface(interfaceInfoInvokeRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取接口的详细信息
     */
    @GetMapping("/get/{id}")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(@PathVariable long id, HttpServletRequest request) {
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoById(id, request));
    }

    /**
     * 获取列表（仅管理员可使用）
     */
    @AuthCheck(mustRole = "admin") // 必须是管理员权限才能通过
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        return ResultUtils.success(interfaceInfoService.listInterfaceInfo(interfaceInfoQueryRequest));
    }

    /**
     * 分页获取列表
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        return ResultUtils.success(interfaceInfoService.interfaceInfoService(interfaceInfoQueryRequest));
    }

    @GetMapping("/getInterfaceCount")
    public BaseResponse<Integer> getInterfaceCount(HttpServletRequest request) {
        Integer count = Math.toIntExact(interfaceInfoService.count());
        return ResultUtils.success(count);
    }

    @GetMapping("/getInterfaceInvokeCount")
    public BaseResponse<List<InterfaceInfoVO>> getInterfaceInvokeCount(HttpServletRequest request) {
        List<InterfaceInfoVO> list = interfaceInfoService.getInterfaceInfoTotalInvokeCount();
        return ResultUtils.success(list);
    }

    // endregion

}
