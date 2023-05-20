package cn.example.binapi.service.service;

import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户操作接口关系表的接口服务，用户接口关系表供管理员查看的
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 初始化接口免费调用次数
     */
    void addUserInterfaceInfo();

    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 接口调用次数+1
     */
    boolean invokeCount(long interfaceInfoId, long creator);

    /**
     * 获取用户可用接口
     */
    IPage<InterfaceInfo> getAvailableInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest, long creator);
}
