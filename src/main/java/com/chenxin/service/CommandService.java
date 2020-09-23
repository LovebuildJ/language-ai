package com.chenxin.service;

import com.chenxin.exception.BizException;
import com.chenxin.model.bo.MapBo;
import com.chenxin.util.CommonEnum;
import com.chenxin.util.encode.EncodeUtil;
import com.chenxin.util.nlp.SimilarWords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
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
        List<MapBo<String, String>> mapBos = SimilarWords.loadWordsMap();
        if (mapBos == null||mapBos.size() == 0) {
            throw new BizException(CommonEnum.IMPORT_WORDS);
        }
        // 初始化redis, 先清空所有键值
        Set<String> keys = redisTemplate.keys("*");
        if (keys.size()!=0) {
            // 清空
            redisTemplate.delete(keys);
        }

        int suffix = 0;
        // 将词库遍历导入redis
        for (MapBo<String, String> map : mapBos) {
            String key = EncodeUtil.changeCharset(map.getKey(),"UTF-8");
            String value = EncodeUtil.changeCharset(map.getVal(),"UTF-8");

            // 去除重复键值, 以免发生异常
            if (!redisTemplate.hasKey(key)) {
                redisTemplate.opsForValue().append(key,value);
            }else {
                // 相同键值添加后缀
                key = key+suffix;
                redisTemplate.opsForValue().append(key,value);
                suffix++;
            }
        }

    }
}
