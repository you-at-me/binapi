package cn.example.binapi.service.service.impl;

import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.mapper.PostMapper;
import cn.example.binapi.common.model.entity.Post;
import cn.example.binapi.common.model.enums.PostGenderEnum;
import cn.example.binapi.common.model.enums.PostReviewStatusEnum;
import cn.example.binapi.service.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 帖子表服务实现类
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        Integer age = post.getAge();
        Integer gender = post.getGender();
        String content = post.getContent();
        String job = post.getJob();
        String place = post.getPlace();
        String education = post.getEducation();
        String loveExp = post.getLoveExp();
        Integer reviewStatus = post.getReviewStatus();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(content, job, place, education, loveExp) || ObjectUtils.anyNull(age, gender)) {
                throw new BusinessException(ResponseStatus.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ResponseStatus.PARAMS_CONTENT_ERROR);
        }
        if (reviewStatus != null && !PostReviewStatusEnum.getValues().contains(reviewStatus)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        if (age != null && (age < 18 || age > 100)) {
            throw new BusinessException(ResponseStatus.PARAMS_NOT_COMPLIANT);
        }
        if (gender != null && !PostGenderEnum.getValues().contains(gender)) {
            throw new BusinessException(ResponseStatus.PARAMS_GENDER_COMPLIANT);
        }
    }
}




