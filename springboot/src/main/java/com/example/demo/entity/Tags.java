package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Geed
 */
@TableName("tags")
@Data
public class Tags {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Integer blogId;
    private String tag;

    public Tags(String str, Integer blogId) {
        this.id = null;
        this.tag = str;
        this.blogId = blogId;
    }
    public Tags(){

    }
}