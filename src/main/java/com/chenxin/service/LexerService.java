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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

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
     * 切割句子, 获取需要的词义
     */
    public void sliceSentence(LexerOut lexerOut) {
        if (lexerOut == null) {
            throw new BizException(CommonEnum.PARAM_ERROR);
        }

        // 需要从处理的词性数组
        String[] wordPos = {LexerConstants.A,LexerConstants.N,LexerConstants.V,LexerConstants.AD,LexerConstants.VD};
        List<String> posList = Arrays.asList(wordPos);

        List<LexerItemBo> items = lexerOut.getItems();
        if (CollUtil.isNotEmpty(items)) {
            for (LexerItemBo item : items) {
                // 形容词, 动词, 名词(特有名词, 专属名词不进行处理)
                String pos = item.getPos();
                if (StrUtil.isBlank(pos)) {
                    if (posList.contains(pos)) {
                        // 替换成同义词


                    }
                }
            }
        }
    }

}
