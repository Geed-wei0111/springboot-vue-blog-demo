package com.example.demo.entity.RBAC;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("user_role")
public class UserRole {
    private Integer userId;
    private Integer roleId;
}
