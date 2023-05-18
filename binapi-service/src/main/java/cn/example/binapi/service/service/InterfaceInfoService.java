package cn.example.binapi.service.service;

import cn.example.binapi.common.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author zyshu
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-05-13 20:57:53
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean b);
}
