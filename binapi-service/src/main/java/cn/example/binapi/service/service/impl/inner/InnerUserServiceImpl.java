package cn.example.binapi.service.service.impl.inner;


import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.mapper.UserMapper;
import cn.example.binapi.common.service.inner.InnerUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isBlank(accessKey)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("access_key", accessKey);
        return userMapper.selectOne(queryWrapper);
    }

}