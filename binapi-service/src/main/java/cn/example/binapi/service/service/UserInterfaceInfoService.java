package cn.example.binapi.service.service;

import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import cn.example.binapi.common.model.dto.userInterfaceInfo.UserInterfaceInfoAddRequest;
import cn.example.binapi.common.model.dto.userInterfaceInfo.UserInterfaceInfoUpdateRequest;
import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户操作接口关系表的接口服务，用户接口关系表供管理员查看的
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    long addUserInterfaceInfo(UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request);

    Boolean deleteUserInterfaceInfo(long userInterfaceInfoId, HttpServletRequest request);

    Boolean updateUserInterfaceInfo(UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest, HttpServletRequest request);

    UserInterfaceInfo getUserInterfaceInfoById(long id);

    /**
     * 初始化接口免费调用次数
     */
    void initUserInterfaceInfo();

    /**
     * 接口调用次数+1,剩余次数-1
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 获取用户可用接口
     */
    IPage<InterfaceInfo> getAvailableInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest, long userId);
}
