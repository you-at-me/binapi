package cn.example.binapi.service.controller;

import cn.example.binapi.common.common.BaseResponse;
import cn.example.binapi.common.common.ResultUtils;
import cn.example.binapi.common.constant.UserConstant;
import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import cn.example.binapi.common.model.vo.InterfaceInfoVO;
import cn.example.binapi.service.annotation.AuthCheck;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.mapper.UserInterfaceInfoMapper;
import cn.example.binapi.service.service.InterfaceInfoService;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.example.binapi.common.common.ResponseStatus.INTERFACE_EMPTY;

/**
 * 需求:统计各接口的总调用次数占所有接口被调用次数总和的比例(饼图展示）取调用最多的前top(3)个接口，从而分析出哪些接口没有人用(降低资源、或者下线)，高频接口(增加资源、提高收费)。后端接口已写好:listTopInvokeInterfaceInfo
 *
 * 统计分析热点top的接口信息，前端利用饼图展示数据的js库：ECharts(推荐)、BizCharts、AntV(推荐，本项目使用)
 * @author Carl
 * @since 2023-05-23
 */
@RestController
@RequestMapping("analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/interfaces/top/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo(@PathVariable("id") int id) {
        List<UserInterfaceInfo> Infos = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(id);
        Map<Long, List<UserInterfaceInfo>> collect = Infos.stream().collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", collect.keySet());
        List<InterfaceInfo> interfaceInfos = interfaceInfoService.list(queryWrapper);
        if (CollectionUtil.isEmpty(interfaceInfos)) {
            throw new BusinessException(INTERFACE_EMPTY);
        }
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfos.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            Integer totalNum = collect.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVO.setTotalNum(totalNum);
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(interfaceInfoVOList);
    }
}
