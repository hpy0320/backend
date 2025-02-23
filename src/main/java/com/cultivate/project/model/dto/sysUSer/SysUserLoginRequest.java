package com.cultivate.project.model.dto.sysUSer;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class SysUserLoginRequest implements Serializable {

    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 密码
     */
    private String password;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}