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
@TableName("favorites")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer blogId;
    private Integer folderId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;
}