package com.chenxin.util.auth;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.chenxin.model.dto.BaiDuAuthOut;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 认证容器
 * @Date 2020/9/18 13:42
 * @Author by 尘心
 */
public class AuthContainer {

    /**
     * auth容器
     */
    private static Map<String,Object> authMap = new HashMap<>(10);

    /**
     * 超时时间
     */
    private static Date expiresTime;


    /**
     * 添加元素
     */
    public static void add(String key,Object value,long expiresIn) {
        if (key == null) {
            return;
        }
        // 将 秒 转换成 天 s/(60*60*24)
        long days = 0L;
        if (expiresIn>0) {
            days = expiresIn/(60*60*24);
        }

        // 获取过期时间 格式 yyyy-MM-dd , 默认T -1 天
        Calendar calendar = DateUtil.calendar(new Date());
        calendar.add(Calendar.DATE,(int)(days-1));
        // 设置过期时间
        expiresTime = calendar.getTime();
        // 添加元素
        authMap.put(key,value);
    }

    /**
     * 获取元素
     * @param key 元素键值
     */
    public static Object get(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }

        if (!authMap.containsKey(key)) {
            return null;
        }

        // 查看access token是否失效
        if (checkAndClearKey(key)) {
            return null;
        }

        return authMap.get(key);
    }

    /**
     * 获取auth对象
     * @param key 元素键值
     */
    public static BaiDuAuthOut getAuthInstance(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }

        if (!authMap.containsKey(key)) {
            return null;
        }

        // 清查并清理过期缓存
        if (checkAndClearKey(key)) {
            return null;
        }

        Object o = authMap.get(key);
        if (o instanceof BaiDuAuthOut) {
            return (BaiDuAuthOut)o;
        }

        return null;
    }

    /**
     * 检查并清理过期缓存
     * @param key 缓存键值
     * @return 若清理成功, 则返回true
     */
    private static boolean checkAndClearKey(String key) {
        // 查看access token是否失效
        if (expiresTime!=null) {
            int result = DateUtil.compare(new Date(), expiresTime);
            if (result >= 0) {
                // 已经失效移除, 需要重新获取
                authMap.remove(key);
                return true;
            }
        }

        return false;
    }
}
