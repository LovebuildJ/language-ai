package com.chenxin.controller;

import cn.hutool.core.util.StrUtil;
import com.chenxin.base.BaseController;
import com.chenxin.exception.BizException;
import com.chenxin.model.R;
import com.chenxin.model.ReqBody;
import com.chenxin.model.dto.DnnModelOut;
import com.chenxin.model.dto.LexerOut;
import com.chenxin.model.dto.SimilarWordDto;
import com.chenxin.model.dto.TextDto;
import com.chenxin.service.LexerService;
import com.chenxin.util.CommonEnum;
import com.chenxin.util.consts.AiConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 词义分析控制层
 * Created by 尘心 on 2020/9/19 0019.
 */
@Api(tags = "NLP自然语言处理接口")
@RestController
@RequestMapping("/lexer")
public class LexerController extends BaseController{

    @Autowired
    private LexerService lexerService;

    @ApiOperation("词义分析(分词)")
    @PostMapping("/lexerText")
    public R lexerText(@RequestBody ReqBody<TextDto> para) {
        if (para == null||para.getParams() == null) {
            return R.error(CommonEnum.PARAM_ERROR);
        }

        String accessToken = getAccessToken();
        if (StrUtil.isBlank(accessToken)) {
            return R.error(CommonEnum.TOKEN_ERROR);
        }

        return R.success(lexerService.analyseLexer(para.getParams(),accessToken));
    }

    @ApiOperation("词义相似度计算")
    @PostMapping("/lexerText")
    public R wordSimilar(@RequestBody ReqBody<SimilarWordDto> para) {
        return R.success(lexerService.calculateWordSimilarScore(para.getParams()));
    }

    @ApiOperation("DNN语言模型计算句子通顺度")
    @PostMapping("/dnn")
    public R getSentenceDnn(@RequestBody ReqBody<TextDto> para) {
        return R.success(lexerService.analyseDnnModel(para.getParams(),getAccessToken()));
    }

    @ApiOperation("文本'变脸' 即伪原创")
    @PostMapping("/textReplace")
    public R wordReplace(@RequestBody ReqBody<TextDto> para) {
        if (para == null||para.getParams() == null) {
            return R.error(CommonEnum.PARAM_ERROR);
        }

        String accessToken = getAccessToken();
        if (StrUtil.isBlank(accessToken)) {
            return R.error(CommonEnum.TOKEN_ERROR);
        }

        LexerOut lexerOut = lexerService.analyseLexer(para.getParams(),accessToken);
        if (lexerOut == null) {
            return R.error(CommonEnum.ANALYSE_WORDS_FAIL);
        }

        // DNN语言模型校验
        DnnModelOut out = getPpl(lexerOut,accessToken);
        if (out == null) {
            // DNN计算失败, 直接返回原文本
            return R.success(para.getParams().getText());
        }

        return R.success(out.getText()==null?para.getParams():out.getText());
    }


    private DnnModelOut getPpl(LexerOut lexerOut,String accessToken) {
        if (lexerOut == null) {
            throw new BizException(CommonEnum.PARAM_ERROR);
        }
        if (StrUtil.isBlank(accessToken)) {
            throw new BizException(CommonEnum.TOKEN_NOT_FOUND);
        }

        String replaceResult = lexerService.sliceSentence(lexerOut);
        if (StrUtil.isNotBlank(replaceResult)) {
            // DNN语言模型计算通顺度
            return lexerService.analyseDnnModel(new TextDto(replaceResult),accessToken);
        }

        return null;
    }
}
