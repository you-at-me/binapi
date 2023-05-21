package cn.example.binapi.common.service.inner;

/**
 * 用户操作接口关系表的内部接口服务，用户接口关系表供管理员查看的
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
