package cn.example.binapi.common.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 密码确认
     */
    private String checkPassword;

    /**
     * 手机号或邮箱
     */
    private String phoneOrMail;

}