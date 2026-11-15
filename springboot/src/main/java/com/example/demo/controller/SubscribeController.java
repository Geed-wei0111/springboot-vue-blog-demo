package com.example.demo.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.demo.common.Result;
import com.example.demo.entity.RBAC.User;
import com.example.demo.entity.Subscribe;
import com.example.demo.service.SubscribeService;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Geed
 * @since 2022-05-08
 */
@RestController
@RequestMapping("/sub")
public class SubscribeController {
    @Resource
    private SubscribeService subscribeService;
    @Resource
    private UserService userService;

    @PostMapping("/{author}/{userId}")
    public Result<?> subAuthor(@PathVariable String author, @PathVariable Integer userId){
        Integer authorId = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, author)).getId();
        Subscribe one = subscribeService.getOne(Wrappers.<Subscribe>lambdaQuery().
                                                eq(Subscribe::getUserId1, userId).
                                                eq(Subscribe::getUserId2, authorId));
        if (userId.intValue() == authorId.intValue()){
            return Result.error("-2", "关注自己干什么，别闹 =(");
        }
        if (one!=null){
            return Result.error("-1", "已经关注了哦，取关可以去收藏夹操作~");
        }else{
            subscribeService.save(new Subscribe(userId, authorId));
            return Result.success();
        }
    }

    @GetMapping("/{userId}")
    public Result<?> getSubscribe(@PathVariable Integer userId){
        //查询关注的用户ID
        List<Object> list = subscribeService.listObjs(Wrappers.<Subscribe>lambdaQuery()
                                                        .select(Subscribe::getUserId2)
                                                        .eq(Subscribe::getUserId1, userId));
        if (list.size()!=0){
            StringBuilder string = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                if (i != list.size()-1){
                    string.append(list.get(i)).append(",");
                }else{
                    string.append(list.get(i));
                }
            }
            //查询关注的用户名
            List<Object> objects = userService.listObjs(Wrappers.<User>lambdaQuery().select(User::getUsername).inSql(User::getId, string.toString()));
            return Result.success(objects);
        }else{
            return Result.error("-1", "还没有关注用户哦");
        }
    }
}