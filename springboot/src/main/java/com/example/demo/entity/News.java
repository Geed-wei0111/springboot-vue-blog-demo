package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Geed
 */
@TableName("news")
@Data
public class News {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    @TableField(exist = false)
    private List<String> tags;
    private String author;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
}