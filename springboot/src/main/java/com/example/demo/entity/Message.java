package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("message")
@Data
public class Message {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String content;
    private String username;
    private Integer parentId;
    private Integer foreignId;

    //标签注解：该变量不存在于数据库表中
    @TableField(exist = false)
    private Message parentMessage;
}