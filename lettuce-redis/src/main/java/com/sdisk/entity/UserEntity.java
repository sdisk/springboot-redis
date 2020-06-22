package com.sdisk.entity;

import java.io.Serializable;

/**
 * @description: 实体类实现Serializable进行序列化，方便反序列化
 * @author: Mr.Huang
 * @create: 2020-06-19 13:52
 **/
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 2901571229736043771L;

    private Long id;
    private String username;
    private String gender;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "UserEntity{" + "id=" + id + ", username='" + username + '\'' + ", gender='" + gender + '\'' + '}';
    }
}
