package cn.example.binapi.service.service.inner;

/**
 * 用户调用接口信息表的接口服务
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
