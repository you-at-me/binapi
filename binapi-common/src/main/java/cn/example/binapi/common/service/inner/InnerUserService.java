package cn.example.binapi.common.service.inner;

import cn.example.binapi.common.model.entity.User;

/**
 * 用户操作服务接口
*/
public interface InnerUserService {

    /**
     * 根据用户的通用标识符查询出对应的用户
     */
    User getInvokeUser(String accessKey);

}
