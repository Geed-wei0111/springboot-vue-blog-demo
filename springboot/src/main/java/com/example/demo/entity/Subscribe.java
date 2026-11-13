package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Geed
 * @since 2022-05-08
 */
public class Subscribe implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关注者（ID）
     */
    private Integer userId1;

    /**
     * 被关注者（ID）
     */
    private Integer userId2;

    public Subscribe(Integer userId, Integer authorId) {
        this.userId1 = userId;
        this.userId2 = authorId;
    }
    public Subscribe(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUserId1() {
        return userId1;
    }

    public void setUserId1(Integer userId1) {
        this.userId1 = userId1;
    }
    public Integer getUserId2() {
        return userId2;
    }

    public void setUserId2(Integer userId2) {
        this.userId2 = userId2;
    }

    @Override
    public String toString() {
        return "Subscribe{" +
            "id=" + id +
            ", userId1=" + userId1 +
            ", userId2=" + userId2 +
        "}";
    }
}
