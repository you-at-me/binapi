package cn.example.binapi.service.controller;

import cn.example.binapi.common.constant.UserConstant;
import cn.example.binapi.common.model.dto.user.*;
import cn.example.binapi.common.model.entity.User;
import cn.example.binapi.common.model.vo.UserVO;
import cn.example.binapi.service.annotation.AuthCheck;
import cn.example.binapi.service.common.BaseResponse;
import cn.example.binapi.service.common.ResponseStatus;
import cn.example.binapi.service.common.ResultUtils;
import cn.example.binapi.service.exception.BusinessException;
import cn.example.binapi.service.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户接口，用户的登录状态都是依赖 session 的，请求必须携带 Cookie
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // region 登录相关

    /**
     * 用户注册
     */
    @PostMapping("register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (ObjectUtil.isNull(userRegisterRequest)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        String account = userRegisterRequest.getAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String phoneOrMail = userRegisterRequest.getPhoneOrMail();
        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            return ResultUtils.error(ResponseStatus.PARAMS_ERROR);
        }
        // 成功返回用户ID
        long id = userService.userRegister(account, password, checkPassword, phoneOrMail);
        return ResultUtils.success(id);
    }

    /**
     * 用户登录
     */
    @PostMapping("login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (Objects.isNull(userLoginRequest)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getAccount();
        String userPassword = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     */
    @PostMapping("logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("getLoginUser")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    // endregion

    // region 增删改查

    /**
     * 管理员创建用户
     */
    @PostMapping("addUser")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE) // 只有管理员才能创建用户
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        long userId = userService.addUser(userAddRequest);
        return ResultUtils.success(userId);
    }

    /**
     * 删除用户
     */
    @PostMapping("/deleteUser/{id}")
    @AuthCheck
    public BaseResponse<Boolean> deleteUser(@PathVariable("id") long id) {
        if (ObjectUtils.isEmpty(id) || id <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("updateUser")
    @AuthCheck
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        boolean result = userService.updateUser(userUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户
     */
    @GetMapping("/getUser/{id}")
    @AuthCheck
    public BaseResponse<UserVO> getUserById(@PathVariable("id") long id) {
        if (ObjectUtil.isEmpty(id) || id <= 0) {
            throw new BusinessException(ResponseStatus.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     */
    @GetMapping("listUser")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserVO>> listUser(UserQueryRequest userQueryRequest) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页获取用户列表
     */
    @GetMapping("listUserPages")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserPages(UserQueryRequest userQueryRequest, @RequestParam(value = "current", required = false, defaultValue = "0") long current, @RequestParam(value = "size", required = false, defaultValue = "10") long size) {
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    // endregion
}
