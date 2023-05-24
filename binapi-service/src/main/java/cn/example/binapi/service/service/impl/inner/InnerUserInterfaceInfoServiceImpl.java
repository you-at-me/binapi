package cn.example.binapi.service.service.impl.inner;

import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.service.UserInterfaceInfoService;
import cn.example.binapi.common.service.inner.InnerUserInterfaceInfoService;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

import static cn.example.binapi.common.common.ResponseStatus.INTERFACE_NOT_PURCHASED;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean hasLeftNum(Long interfaceId, Long userId) {
        if (interfaceId <= 0 || userId <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }

        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interface_info_id", interfaceId).eq("user_id", userId);
        UserInterfaceInfo one = userInterfaceInfoService.getOne(queryWrapper);
        if (ObjectUtil.isNull(one)) throw new BusinessException(INTERFACE_NOT_PURCHASED);

        queryWrapper.gt("left_num", 0);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);

        return !ObjectUtil.isNull(userInterfaceInfo);
    }

    /**
     * 调用次数计数
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }
}