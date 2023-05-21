package cn.example.binapi.common.service.inner;

import cn.example.binapi.common.model.entity.User;

/**
 * 用户操作服务接口
*/
public interface InnerUserService {

    /**
     * 在数据库查询是否已分配给用户密钥
     */
    User getInvokeUser(String accessKey);

}
