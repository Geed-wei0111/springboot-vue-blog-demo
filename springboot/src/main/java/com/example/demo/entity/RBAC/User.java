package com.example.demo.entity.RBAC;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;
import java.util.Set;

@TableName("user")
@Data
public class User {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private Integer age;
    private String sex;
    private String nickname;
    private String address;
    private Integer ban;
    private String avatar;

    @TableField(exist = false)
    private String token;
    @TableField(exist = false)
    private List<Integer> roles;
    @TableField(exist = false)
    private Set<Permission> permissions;
}