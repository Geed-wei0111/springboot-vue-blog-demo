package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.RBAC.Permission;
import com.example.demo.mapper.PermissionMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Resource
    PermissionMapper permissionMapper;

    //新增
    @PostMapping
    public Result<?> save(@RequestBody Permission permission){
        permissionMapper.insert(permission);
        return Result.success();
    }

    //更新
    @PutMapping
    public Result<?> update(@RequestBody Permission permission){
        permissionMapper.updateById(permission);
        return Result.success();
    }

    //删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        permissionMapper.deleteById(id);
        return Result.success();
    }

    //获取单个权限
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(permissionMapper.selectById(id));
    }

    //获取所有权限
    @GetMapping("/all")
    public Result<?> all() {
        return Result.success(permissionMapper.selectList(null));
    }

    //查询
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search){
        LambdaQueryWrapper<Permission> wrapper = Wrappers.lambdaQuery();
//        如果search不为空加上，查到null的数据
        if (StrUtil.isNotBlank(search)){
            wrapper.like(Permission::getComment, search);
        }
        Page<Permission> page = permissionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }
}