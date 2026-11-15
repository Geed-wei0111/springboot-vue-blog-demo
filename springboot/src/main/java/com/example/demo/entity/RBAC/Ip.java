package com.example.demo.entity.RBAC;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ip")
public class Ip {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String ip;
    private Integer ban;
}