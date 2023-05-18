package cn.example.binapi.service.mapper;

import cn.example.binapi.common.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户操作接口信息表实体映射接口
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);

    @Select("SELECT * FROM tb_user_interface_info WHERE id = #{id} FOR UPDATE")
    UserInterfaceInfo selectByIdForUpdate(Long id);
}




