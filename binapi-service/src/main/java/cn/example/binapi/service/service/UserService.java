package cn.example.binapi.service.service;


import cn.example.binapi.common.model.dto.user.UserAddRequest;
import cn.example.binapi.common.model.dto.user.UserUpdateRequest;
import cn.example.binapi.common.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param account   用户账户
     * @param password  用户密码
     * @param checkPassword 校验密码
     * @param phoneOrMail 注册手机号，可为空，当上述不通过账号注册时 手机号和邮箱必须选一个
     * @return 新用户 id
     */
    long userRegister(String account, String password, String checkPassword, String phoneOrMail);

    /**
     * 用户登录
     *
     * @param account  用户账户
     * @param password 用户密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String account, String password, HttpServletRequest request);

    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 管理员添加用户
     */
    long add(UserAddRequest userAddRequest);

    /**
     * 更新用户
     */
    boolean updateUser(UserUpdateRequest userUpdateRequest);

    /**
     * 是否为管理员
     */
    boolean isNotAdmin(HttpServletRequest request);

    /**
     * 用户注销
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取github+gitee star数
     */
    Integer getStars();
}
