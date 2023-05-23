package cn.example.binapi.service.service.impl.inner;

import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.mapper.InterfaceInfoMapper;
import cn.example.binapi.service.service.UserInterfaceInfoService;
import cn.example.binapi.common.service.inner.InnerInterfaceInfoService;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.net.URI;

@Slf4j
@DubboService // 通过这个配置可以基于 Spring Boot 去发布 Dubbo 服务
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        log.info("url: {}", url);
        URI uri = URI.create(url);
        String path = uri.getPath();
        log.info("path: {}", path);
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", path);
        queryWrapper.eq("method", method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean hasLeftNum(Long interfaceId, Long userId) {
        if (interfaceId <= 0 || userId <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }

        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interface_info_id", interfaceId).eq("user_id", userId).gt("left_num", 0);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);

        return !ObjectUtil.isNull(userInterfaceInfo);
    }
}
