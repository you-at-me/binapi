package cn.example.binapi.service.service.impl;

import cn.example.binapi.common.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.service.common.ErrorCode;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.mapper.InterfaceInfoMapper;
import cn.example.binapi.service.service.InterfaceInfoService;
import cn.example.binapi.service.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 接口信息表实现类
 * @author Carl
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    @Resource
    private UserService userService;

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean b) {
        if (ObjectUtil.isEmpty(interfaceInfo)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        if (b) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() < 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }

    @Override
    public long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (Objects.isNull(interfaceInfoAddRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        // 把当前新增的这条接口信息设置创建人ID
        interfaceInfo.setCreator(loginUser.getId());
        // 将接口信息保存在数据库当中，等接口被调用的时候再将接口信息数据缓存到 redis 当中
        boolean result = save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 新增成功直接返回接口的ID
        return interfaceInfo.getId();
    }
}




