package cn.example.binapi.common.service;

import cn.example.binapi.common.model.entity.InterfaceInfo;

/**
* @author Carl
*/
public interface InnerInterfaceInfoService {

    /**
     * 从数据库中查询接口是否存在（请求路径、请求方法）
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

    /**
     * 查询用户是否还有调用次数
     */
    boolean hasCount(Long interfaceId, Long userId);
}
