package com.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 50, message = "用户名长度必须在1到50之间")
    private String name;

    @TableField("student_id")
    private String studentId;

    private String role;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("avatar_path")
    private String avatarPath;

    @TableField("reset_token")
    private String resetToken;

    @TableField("reset_token_expire")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resetTokenExpire;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private LocalDateTime createTime;
}
