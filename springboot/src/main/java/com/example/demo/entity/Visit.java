package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Geed
 * 访问量实体 2022-4-22
 */
@Data
@TableName("visits")
public class Visit {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer blogId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;
    private Integer count;

    public Visit(Integer userId, Integer blogId, Date date, int count) {
        this.id = null;
        this.userId = userId;
        this.blogId = blogId;
        this.time = date;
        this.count = count;
    }
    public Visit(){

    }
}