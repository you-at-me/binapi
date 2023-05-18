package cn.example.binapi.common.service;

import cn.example.binapi.common.model.entity.User;

/**
 * 用户服务
*/
public interface InnerUserService {

    /**
     * 在数据库查询是否已分配给用户密钥
     */
    User getInvokeUser(String accessKey);

}
