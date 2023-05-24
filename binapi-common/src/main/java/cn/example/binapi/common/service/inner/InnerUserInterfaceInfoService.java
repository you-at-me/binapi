package cn.example.binapi.common.service.inner;

/**
 * 用户操作接口关系表的内部接口服务，用户接口关系表供管理员查看的
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 判断用户是否有相应权限调用该接口，并查询用户是否还有调用次数
     */
    boolean hasLeftNum(Long interfaceId, Long userId);

    /**
     * 调用接口统计，对调用次数+1,对剩余次数-1
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
