package cn.example.binapi.service.service;

import cn.example.binapi.common.model.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 帖子表的操作服务接口
 */
public interface PostService extends IService<Post> {

    /**
     * 校验
     *
     * @param add 是否为创建校验
     */
    void validPost(Post post, boolean add);
}
