package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserMapper userMapper;

    //新增
    @PostMapping
    public Result<?> save(@RequestBody User user){
        if (user.getPassword() == null){
            user.setPassword("111111"); //设置默认密码
        }
        userMapper.insert(user);
        return Result.success();
    }

    //用户登录
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user){
        User result = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword()));
        if (result == null){
            return Result.error("-1", "用户名或密码错误");
        }
        return Result.success(result);
    }

    //用户注册
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user){
        User result = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()));
        if (result != null){
            return Result.error("-1", "用户名已存在~");
        }else{
            userMapper.insert(user);
        }
        return Result.success();
    }

    //更新单个数据
    @PutMapping
    public Result<?> update(@RequestBody User user){
        userMapper.updateById(user);
        return Result.success();
    }

    //删除单个数据
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id){
        userMapper.deleteById(id);
        return Result.success();
    }

//    利用分页插件，模糊  查询  数据库中的数据
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search){
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
//        如果search不为空加上，查到null的数据
        if (StrUtil.isNotBlank(search)){
            wrapper.like(User::getUsername, search);
        }
        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    // 跳转测试！！！根据用户ID查询单条数据
    @GetMapping("/id")
    public Result<?> find(@RequestParam Integer id){
        User user = userMapper.selectById(id);
        if (user != null){
            return Result.success(user);
        }
        return Result.error("-1", "error");
    }
}