package cn.example.binapi.service.mapper;

import cn.example.binapi.common.model.entity.InterfaceInfo;
import cn.example.binapi.common.model.vo.InterfaceInfoVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 接口信息表实体映射
 * @author Carl
 */
@Mapper
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {

    // @Select("SELECT ii.id AS `id`, ii.name AS `name`, ii.description AS `description`, ii.method AS `method`, ii.request_params AS `requestParams`, ii.request_header AS `requestHeader`, ii.price AS `price`, ii.url AS `url`, ui.left_num AS `leftNum` FROM tb_user_interface_info ui LEFT JOIN tb_interface_info ii ON ii.id = ui.interface_info_id WHERE ui.creator = #{userId} AND ui.left_num > 0 AND ui.is_delete = 0 AND (ii.is_delete = 0 OR ii.is_delete IS NULL) ${ew.customSqlSegment}")
    IPage<InterfaceInfo> getInterfaceInfoByUserId(Page<InterfaceInfo> page, @Param("userId") Long userId, @Param(Constants.WRAPPER) Wrapper<InterfaceInfo> wrapper);

    List<InterfaceInfoVO> getInterfaceTotalInvokeCounts();

}




