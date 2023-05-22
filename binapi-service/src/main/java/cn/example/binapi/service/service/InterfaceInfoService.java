package cn.example.binapi.service.service;

import cn.example.binapi.common.common.InterfaceIdRequest;
import cn.example.binapi.common.common.InterfacePurchaseRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.vo.InterfaceInfoVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口信息表的服务接口
 *
 * @author Carl
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean b);

    long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request);

    boolean deleteInterfaceInfo(long id, HttpServletRequest request);

    boolean updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest r);

    Boolean onlineInterfaceInfo(InterfaceInfoInvokeRequest idRequest, HttpServletRequest request);

    Boolean offlineInterfaceInfo(InterfaceIdRequest idRequest);

    String purchaseInterface(InterfacePurchaseRequest interfacePurchaseRequest, HttpServletRequest request);

    String invokeInterface(InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request);

    InterfaceInfo getInterfaceInfoById(long id, HttpServletRequest request);

    List<InterfaceInfo> listInterfaceInfo(InterfaceInfoQueryRequest queryRequest, HttpServletRequest request);

    Page<InterfaceInfo> interfaceInfoService(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    List<InterfaceInfoVO> getInterfaceInfoTotalInvokeCount();
}
