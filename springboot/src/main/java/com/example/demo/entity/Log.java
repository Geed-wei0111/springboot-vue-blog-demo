package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("logs")
public class Log {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String log;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    public Log(String l, Date t){
        this.log = l;
        this.time = t;
    }
    public Log(){
        this.log = null;
        this.time = null;
    }
}