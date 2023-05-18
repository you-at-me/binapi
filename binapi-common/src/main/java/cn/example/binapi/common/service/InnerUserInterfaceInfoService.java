package cn.example.binapi.common.service;

/**
 * 针对表(user_interface_info(用户调用接口关系))的数据库操作Service
*/
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
