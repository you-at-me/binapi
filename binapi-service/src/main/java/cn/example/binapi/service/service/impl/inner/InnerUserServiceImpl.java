// package cn.example.binapi.service.service.impl.inner;
//
// import cn.alias.openapi.common.model.entity.User;
// import cn.alias.openapi.common.service.InnerUserService;
// import cn.alias.openapi.service.common.ResponseStatus;
// import cn.alias.openapi.service.exception.BusinessException;
// import cn.alias.openapi.service.mapper.UserMapper;
// import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// import org.apache.commons.lang3.StringUtils;
// import org.apache.dubbo.config.annotation.DubboService;
//
// import javax.annotation.Resource;
//
// @DubboService
// public class InnerUserServiceImpl implements InnerUserService {
//
//     @Resource
//     private UserMapper userMapper;
//
//     @Override
//     public User getInvokeUser(String accessKey) {
//         if (StringUtils.isAnyBlank(accessKey)) {
//             throw new BusinessException(ResponseStatus.PARAMS_ERROR);
//         }
//         QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//         queryWrapper.eq("access_key", accessKey);
//         return userMapper.selectOne(queryWrapper);
//     }
//
// }