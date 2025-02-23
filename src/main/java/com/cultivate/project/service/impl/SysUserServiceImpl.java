package com.cultivate.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cultivate.project.common.ErrorCode;
import com.cultivate.project.constant.UserConstant;
import com.cultivate.project.exception.BusinessException;
import com.cultivate.project.model.entity.SysUser;
import com.cultivate.project.model.entity.User;
import com.cultivate.project.service.SysUserService;
import com.cultivate.project.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author Dear
* @description 针对表【sys_user(用户信息表)】的数据库操作Service实现
* @createDate 2025-01-01 21:40:27
*/
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{

    @Resource
    private SysUserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "hpy";

    @Override
    public long userRegister(String loginName, String Password, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(loginName, Password, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (loginName.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (Password.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!Password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (loginName.intern()) {
            // 账户不能重复
            QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("loginName", loginName);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + Password).getBytes());
            // 3. 分配 accessKey, secretKey
            // 4. 插入数据
            SysUser user = new SysUser();
            user.setLoginName(loginName);
            user.setPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getUserId();
        }
    }

    @Override
    public SysUser userLogin(String loginName, String Password, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(loginName, Password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (loginName.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误长度大于4");
        }
        if (Password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度大于8");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + Password).getBytes());
        // 查询用户是否存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name", loginName);
        queryWrapper.eq("Password", encryptPassword);
        SysUser user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, loginName cannot match Password");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return user;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public SysUser getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        SysUser currentUser = (SysUser) userObj;
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getUserId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

//    /**
//     * 是否为管理员
//     *
//     * @param request
//     * @return
//     */
//    @Override
//    public boolean isAdmin(HttpServletRequest request) {
//        // 仅管理员可查询
//        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
//        SysUser user = (SysUser) userObj;
//        return user != null && UserConstant.ADMIN_ROLE.equals(user.getUserRole());
//    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }






}




