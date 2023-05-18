package cn.example.binapi.interfaces.mapper;

import cn.example.binapi.common.model.entity.Auth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper extends BaseMapper<Auth> {
}
