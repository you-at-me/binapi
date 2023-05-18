package cn.example.binapi.service.service;

import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户调用接口信息表的接口服务
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 初始化接口免费调用次数
     */
    void addUserInterfaceInfo();

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 获取用户可用接口
     */
    IPage<InterfaceInfo> getAvailableInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest, long userId);
}
