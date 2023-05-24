package cn.example.binapi.service.mapper;

import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户操作接口关系表实体映射接口
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(@Param("limit") int limit);

    @Select("SELECT * FROM tb_user_interface_info WHERE id = #{id} FOR UPDATE")
    UserInterfaceInfo selectByIdForUpdate(@Param("id") Long id);
}
