package com.example.demo.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.domain.entity.User;
import com.example.demo.exception.UserLoginException;

public interface UserService extends IService<User> {
    //用户添加
    void userAdd(String username, String password);

    String userLogin(Object account, Object password) throws UserLoginException;

    void userAddWithEmail(String mail,String vertify_code);

    String userLoginWithMail(String mail,String vertify_code);

    void logout(String token);

    User queryUser(Long currentId);

    void updateUser(Long currentId,User user);
    //用户修改

    //用户删除

    //用户查询


}
