package com.example.demo.domain.entity;

import lombok.Data;

@Data
public class Result <T>{
    String msg ;
    String error;
    T data;//业务数据

    public Result(String msg, String error, T data) {
        this.msg = msg;
        this.error = error;
        this.data = data;
    }

    public static <T> Result<T> success(String msg,T data){
        return new Result<>(msg,null,data);
    }

    public static Result<?> success(String msg){
        return new Result<>(msg,null,null);
    }

    public static Result<?> error(String error){
        return new Result<>(null,error,null);
    }
}
