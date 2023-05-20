// package cn.example.binapi.service.service.impl.inner;
//
// import cn.alias.openapi.common.model.entity.InterfaceInfo;
// import cn.alias.openapi.common.model.entity.UserInterfaceInfo;
// import cn.alias.openapi.common.service.InnerInterfaceInfoService;
// import cn.alias.openapi.service.common.ResponseStatus;
// import cn.alias.openapi.service.exception.BusinessException;
// import cn.alias.openapi.service.mapper.InterfaceInfoMapper;
// import cn.alias.openapi.service.service.UserInterfaceInfoService;
// import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.StringUtils;
// import org.apache.dubbo.config.annotation.DubboService;
//
// import javax.annotation.Resource;
// import java.net.URI;
//
// @Slf4j
// @DubboService
// public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
//
//     @Resource
//     private InterfaceInfoMapper interfaceInfoMapper;
//
//     @Resource
//     private UserInterfaceInfoService userInterfaceInfoService;
//
//     @Override
//     public InterfaceInfo getInterfaceInfo(String url, String method) {
//         if (StringUtils.isAnyBlank(url, method)) {
//             throw new BusinessException(ResponseStatus.PARAMS_ERROR);
//         }
//         log.info("url: {}", url);
//         URI uri = URI.create(url);
//         String path = uri.getPath();
//         log.info("path: {}", path);
//         QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
//         queryWrapper.eq("url", path);
//         queryWrapper.eq("method", method);
//         return interfaceInfoMapper.selectOne(queryWrapper);
//     }
//
//     @Override
//     public boolean hasCount(Long interfaceId, Long userId) {
//         if (interfaceId <= 0 || userId <= 0) {
//             throw new BusinessException(ResponseStatus.PARAMS_ERROR);
//         }
//
//         UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(new QueryWrapper<UserInterfaceInfo>()
//                 .eq("interface_info_id", interfaceId)
//                 .eq("user_id", userId)
//                 .gt("left_num", 0));
//
//         return userInterfaceInfo != null;
//     }
// }
