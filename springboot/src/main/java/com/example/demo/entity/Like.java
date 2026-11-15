package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Geed
 * 收藏夹实体 2022-4-22
 */
@Data
@TableName("likes")
public class Like {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer blogId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    public Like(Integer userId, Integer blogId){
        this.userId = userId;
        this.blogId = blogId;
        this.time = new Date();
    }
    public Like(){

    }
}