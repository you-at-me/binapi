package cn.example.binapi.interfaces.service;

import cn.example.binapi.common.model.entity.Auth;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

public interface AuthService extends IService<Auth> {

    String mainRedirect(HttpServletRequest request);
}
