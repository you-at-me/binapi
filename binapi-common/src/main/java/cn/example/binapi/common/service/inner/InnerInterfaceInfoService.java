package cn.example.binapi.common.service.inner;

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
     * 增加接口的总调用次数
     */
    boolean increaseTotalNum(long interfaceId);
}
