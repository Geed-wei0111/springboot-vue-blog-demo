package com.example.demo.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.Result;
import com.example.demo.entity.Folder;
import com.example.demo.service.FolderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Geed
 * @since 2022-05-08
 */
@RestController
@RequestMapping("/folder")
public class FolderController {
    @Resource
    FolderService folderService;

    @GetMapping("/list/{userId}")
    public Result<?> getList(@PathVariable Integer userId){
        List<Folder> list = folderService.list(Wrappers.<Folder>lambdaQuery().eq(Folder::getUserId, userId));
        return Result.success(list);
    }
}