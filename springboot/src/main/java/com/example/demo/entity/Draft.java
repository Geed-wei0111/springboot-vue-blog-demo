package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("draft")
public class Draft {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private String tags;
}