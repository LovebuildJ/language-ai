package com.chenxin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chenxin.base.BaseAuth;
import com.chenxin.base.BaseService;
import com.chenxin.exception.BizException;
import com.chenxin.model.bo.LexerItemBo;
import com.chenxin.model.dto.DnnModelOut;
import com.chenxin.model.dto.LexerOut;
import com.chenxin.model.dto.TextDto;
import com.chenxin.util.CommonEnum;
import com.chenxin.util.auth.AuthContainer;
import com.chenxin.util.consts.AiConstant;
import com.chenxin.util.consts.LexerConstants;
import com.chenxin.util.http.BaiDuUrl;
import com.chenxin.util.http.HttpHeader;
import com.chenxin.util.nlp.SimilarWords;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @Description 词法分析
 * @Date 2020/9/18 15:38
 * @Author by 尘心
 */
@Slf4j
@Service
public class LexerService extends BaseAuth{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 将句子按照词义进行切分
     * @param text 文本对象
     */
    public LexerOut analyseLexer(TextDto text,String accessToken) {
        if (text == null) {
            throw new BizException(CommonEnum.BODY_NOT_MATCH);
        }

        log.info("正在切词中...");

        String realUrl = BaiDuUrl.getRealUtf8Url(NORMAL_LEXER_URL, accessToken);
        // 请求参数 , 格式 json
        String param = JSON.toJSONString(text);
        // 获取json类型请求头
        HttpHeaders jsonHeader = HttpHeader.getJsonHeader();
        HttpEntity<String> httpEntity = new HttpEntity<>(param, jsonHeader);
        // 请求数据
        ResponseEntity<String> postForEntity = restTemplate.postForEntity(realUrl, httpEntity, String.class);
        if (postForEntity.getStatusCode().equals(HttpStatus.OK)) {
            String body = postForEntity.getBody();
            return JSON.parseObject(body, LexerOut.class);
        }

        return new LexerOut();
    }

    /**
     * 切割句子, 同义词替换
     */
    public String sliceSentence(LexerOut lexerOut) {
        if (lexerOut == null) {
            throw new BizException(CommonEnum.PARAM_ERROR);
        }

        log.info("正在进行同义词替换...");

        // 需要从处理的词性数组
        String[] wordPos = {LexerConstants.A,LexerConstants.N,LexerConstants.V,LexerConstants.AD,LexerConstants.VD};
        List<String> posList = Arrays.asList(wordPos);

        StringBuilder builder = new StringBuilder();
        List<LexerItemBo> items = lexerOut.getItems();
        if (CollUtil.isNotEmpty(items)) {
            for (LexerItemBo item : items) {
                // 形容词, 动词, 名词(特有名词, 专属名词不进行处理)
                String pos = item.getPos();
                if (StrUtil.isNotBlank(pos)) {
                    if (posList.contains(pos)) {
                        String replaceWord = null;
                        // 替换成同义词
                        String sourceWord = item.getItem();
                        // 从redis同义词库中查找所有同义词
                        List<String> keys = new ArrayList<>();
                        Cursor<String> cursor = scan(redisTemplate,sourceWord+"*",AiConstant.KEYS_COUNT);
                        while (cursor.hasNext()) {
                            keys.add(cursor.next());
                        }

//                        List<String> similarWordList = new ArrayList<>();
                        // 使用set, 去除相同的词, 提升替换效率
                        TreeSet<String> set = new TreeSet<>();
                        for (String key : keys) {
                            String similarWord = (String) redisTemplate.opsForValue().get(key);
                            set.add(similarWord);
                        }

                        List<String> similarList = new ArrayList<>();
                        for (String word : set) {
                            if (!StrUtil.isBlank(word)) {
                                long distance = CoreSynonymDictionary.distance(sourceWord, word);
                                if (distance == 0) {
                                    // 将符合要求的同义词收集
                                    similarList.add(word);
                                }
                            }

                            log.info("找到如下同义词!"+word);

                        }

                        if (similarList.size()!=0) {
                            if (similarList.size() == 1) {
                                replaceWord = similarList.get(0);
                            }else {
                                Random rand = new Random();
                                String randomElement = similarList.get(rand.nextInt(similarList.size()));
                                if (!sourceWord.equals(randomElement)) {
                                    // 除去自己, 从最相似的词语当中随机取一个
                                    replaceWord = randomElement;
                                }
                                log.info("随机从相似词中取一个进行替换~");
                            }
                        }

                        // 句子重组
                        if (StrUtil.isBlank(replaceWord)) {
                            builder.append(sourceWord);
                        }else {
                            builder.append(replaceWord);
                        }

                    }else {
                        builder.append(item.getItem());
                    }
                }else {
                    builder.append(item.getItem());
                }

            }
        }

        return builder.toString();
    }

    /**
     * DNN语言模型处理
     */
    public DnnModelOut analyseDnnModel(TextDto text,String accessToken) {
        log.info("正在进行DNN语言模型校验...");
        String realUrl = BaiDuUrl.getRealUtf8Url(DNN_LAN_MODEL_URL, accessToken);
        // 请求参数 , 格式 json
        String param = JSON.toJSONString(text);
        // 获取json类型请求头
        HttpHeaders jsonHeader = HttpHeader.getJsonHeader();
        HttpEntity<String> httpEntity = new HttpEntity<>(param, jsonHeader);
        // 请求数据
        ResponseEntity<String> postForEntity = restTemplate.postForEntity(realUrl, httpEntity, String.class);
        if (postForEntity.getStatusCode().equals(HttpStatus.OK)) {
            String body = postForEntity.getBody();
            return JSON.parseObject(body, DnnModelOut.class);
        }

        return new DnnModelOut();
    }

    /**
     * redis扫描相似键值
     */
    private Cursor<String> scan(RedisTemplate stringRedisTemplate, String match, int count){
        ScanOptions scanOptions = ScanOptions.scanOptions().match(match).count(count).build();
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) stringRedisTemplate.getKeySerializer();
        return (Cursor) stringRedisTemplate.executeWithStickyConnection((RedisCallback) redisConnection ->
                new ConvertingCursor<>(redisConnection.scan(scanOptions), redisSerializer::deserialize));
    }
}
