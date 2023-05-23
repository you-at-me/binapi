package cn.example.binapi.service.service.impl.inner;

import cn.example.binapi.common.common.ResponseStatus;
import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.service.inner.InnerInterfaceInfoService;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.service.InterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.net.URI;

@Slf4j
@DubboService // 通过这个配置可以基于 Spring Boot 去发布 Dubbo 服务
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoService interfaceInfoService;

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
        return interfaceInfoService.getOne(queryWrapper);
    }

    @Override
    public boolean increaseTotalNum(long interfaceId) {
        if (interfaceId <= 0) throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        return interfaceInfoService.update(new UpdateWrapper<InterfaceInfo>().setSql("total_num = total_num + 1"));
    }
}
