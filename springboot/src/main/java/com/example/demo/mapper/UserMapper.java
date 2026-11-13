package com.example.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.RBAC.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author Geed
 */
public interface UserMapper extends BaseMapper<User> {
    @Update("update user set avatar = #{url} where id = #{id}")
    Integer updateAvatarById(@Param("url")String url, @Param("id")Integer id);
}