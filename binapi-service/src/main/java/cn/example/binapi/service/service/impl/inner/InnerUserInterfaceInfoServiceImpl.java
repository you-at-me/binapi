// package cn.example.binapi.service.service.impl.inner;
//
// import cn.alias.openapi.common.service.InnerUserInterfaceInfoService;
// import cn.alias.openapi.service.service.UserInterfaceInfoService;
// import org.apache.dubbo.config.annotation.DubboService;
//
// import javax.annotation.Resource;
//
// @DubboService
// public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
//
//     @Resource
//     private UserInterfaceInfoService userInterfaceInfoService;
//
//     /**
//      * 调用次数计数
//      */
//     @Override
//     public boolean invokeCount(long interfaceInfoId, long userId) {
//         return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
//     }
// }