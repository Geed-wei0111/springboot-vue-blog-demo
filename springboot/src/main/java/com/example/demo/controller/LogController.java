package com.example.demo.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.Log;
import com.example.demo.service.LogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**系统日志控制器
 * @author Geed
 */
@RestController
@RequestMapping("/log")
public class LogController {
    @Resource
    LogService logService;

    /**
     * 展示日志
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return list
     */
    @GetMapping("/list")
    public Result<?> getList(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        Page<Log> page = logService.page(new Page<>(pageNum, pageSize), Wrappers.<Log>lambdaQuery().orderByDesc(Log::getId));
        return Result.success(page);
    }
}