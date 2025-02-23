package com.cultivate.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.cultivate.project.common.BaseResponse;
import com.cultivate.project.common.DeleteRequest;
import com.cultivate.project.common.ErrorCode;
import com.cultivate.project.common.ResultUtils;
import com.cultivate.project.exception.BusinessException;

import com.cultivate.project.model.dto.sysUSer.*;
import com.cultivate.project.model.entity.SysUser;
import com.cultivate.project.model.entity.User;
import com.cultivate.project.model.vo.SysUserVO;
import com.cultivate.project.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @authorhpy
 * @from  
 */
@RestController
@Api(value = "用户接口",tags = {"用户接口类"})
@RequestMapping("/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @ApiOperation("用户注册接口")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody SysUserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getLoginName();
        String userPassword = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckpassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = sysUserService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param sysuserloginrequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<SysUser> userLogin(@RequestBody SysUserLoginRequest sysuserloginrequest, HttpServletRequest request) {
        if (sysuserloginrequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = sysuserloginrequest.getLoginName();
        String userPassword = sysuserloginrequest.getPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser user = sysUserService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = sysUserService.userLogout(request);
        return ResultUtils.success(result);
    }


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<SysUserVO> getLoginUser(HttpServletRequest request) {
        SysUser user = sysUserService.getLoginUser(request);
        SysUserVO userVO = new SysUserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody SysUseraddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userAddRequest, user);
        user.setLoginName(userAddRequest.getLoginName());
        user.setPassword(userAddRequest.getPassword());
        boolean result = sysUserService.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getUserId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = sysUserService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody SysUserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = sysUserService.updateById(user);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<SysUserVO> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser user = sysUserService.getById(id);
        SysUserVO userVO = new SysUserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<SysUserVO>> listUser(SysUserQueryRequest userQueryRequest, HttpServletRequest request) {
        SysUser userQuery = new SysUser();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>(userQuery);
        List<SysUser> userList = sysUserService.list(queryWrapper);
        List<SysUserVO> userVOList = userList.stream().map(user -> {
            SysUserVO userVO = new SysUserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<SysUserVO>> listUserByPage(SysUserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        SysUser userQuery = new SysUser();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>(userQuery);
        Page<SysUser> userPage = sysUserService.page(new Page<>(current, size), queryWrapper);
        Page<SysUserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<SysUserVO> userVOList = userPage.getRecords().stream().map(user -> {
            SysUserVO userVO = new SysUserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    // endregion
}
