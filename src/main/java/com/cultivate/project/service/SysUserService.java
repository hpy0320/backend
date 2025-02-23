package com.cultivate.project.service;

import com.cultivate.project.model.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cultivate.project.model.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
* @author Dear
* @description 针对表【sys_user(用户信息表)】的数据库操作Service
* @createDate 2025-01-01 21:40:27
*/
public interface SysUserService extends IService<SysUser> {
    /**
     * 用户注册
     *
     * @param loginName   用户账户
     * @param Password  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String loginName, String Password, String checkPassword);

    /**
     * 用户登录
     *
     * @param loginName  用户账户
     * @param Password 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    SysUser userLogin(String loginName, String Password, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    SysUser getLoginUser(HttpServletRequest request);

//    /**
//     * 是否为管理员
//     *
//     * @param request
//     * @return
//     */
//    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

}
