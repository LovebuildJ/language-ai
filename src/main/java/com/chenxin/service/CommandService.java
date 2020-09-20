package com.chenxin.service;

import com.chenxin.exception.BizException;
import com.chenxin.util.CommonEnum;
import com.chenxin.util.encode.EncodeUtil;
import com.chenxin.util.nlp.SimilarWords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

/**
 * 指令业务
 * Created by 尘心 on 2020/9/20 0020.
 */
@Service
public class CommandService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public void initRedis() throws UnsupportedEncodingException {
        // 加载词库
        Map<String,String> map = SimilarWords.loadWordsMap();
        if (map == null||map.size() == 0) {
            throw new BizException(CommonEnum.IMPORT_WORDS);
        }
        // 初始化redis, 先清空所有键值
        Set<String> keys = redisTemplate.keys("*");
        if (keys.size()!=0) {
            // 清空
            redisTemplate.delete(keys);
        }

        // 将词库遍历导入redis
        for (Map.Entry<String, String> entry : map.entrySet()) {
            // 去除重复键值, 以免发生异常
            if (!redisTemplate.hasKey(entry.getKey())) {
                String key = EncodeUtil.changeCharset(entry.getKey(),"UTF-8");
                String value = EncodeUtil.changeCharset(entry.getValue(),"UTF-8");
                redisTemplate.opsForValue().append(key,value);
            }
        }
    }
}
