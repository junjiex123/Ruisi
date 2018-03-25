package me.yluo.ruisiapp.api.entity;

import java.util.Date;


public class ApiResult<T extends Variables> {

    public int Version;
    public String Charset;
    public T Variables;

}