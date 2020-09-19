package com.chenxin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chenxin.base.BaseAuth;
import com.chenxin.base.BaseService;
import com.chenxin.exception.BizException;
import com.chenxin.model.bo.LexerItemBo;
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
import org.springframework.beans.factory.annotation.Autowired;
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
@Service
public class LexerService extends BaseAuth{

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 将句子按照词义进行切分
     * @param text 文本对象
     */
    public LexerOut analyseLexer(TextDto text,String accessToken) {
        if (text == null) {
            throw new BizException(CommonEnum.BODY_NOT_MATCH);
        }

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
                        // 加载同义词库
                        List<String> similarWords = SimilarWords.loadWords();
                        // 查找到的同义词
                        List<String> distanceList = new ArrayList<>();
                        // 计算并且替换
                        for (String similarWord : similarWords) {
                            // 计算与词库中同义词的距离
                            long distance = CoreSynonymDictionary.distance(sourceWord, similarWord);
                            if (distance == 0) {
                                if (!sourceWord.equals(similarWord)) {
                                    // 添加排除自己的同义词
                                    distanceList.add(similarWord);
                                }
                            }
                        }

                        if (distanceList.size()>0) {
                            if (distanceList.size() == 1) {
                                replaceWord = distanceList.get(distanceList.size());
                            }else {
                                // 多个同义词,随机抽取一个元素
                                Random random = new Random();
                                int n = random.nextInt(distanceList.size());
                                replaceWord = distanceList.get(n);
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

}
