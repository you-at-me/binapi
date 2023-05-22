package cn.example.binapi.interfaces.service.impl;


import cn.example.binapi.common.model.entity.Auth;
import cn.example.binapi.interfaces.mapper.AuthMapper;
import cn.example.binapi.interfaces.service.AuthService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, Auth> implements AuthService {

}
