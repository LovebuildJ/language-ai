package com.chenxin.controller;

import cn.hutool.core.util.StrUtil;
import com.chenxin.base.BaseController;
import com.chenxin.exception.BizException;
import com.chenxin.model.R;
import com.chenxin.model.ReqBody;
import com.chenxin.model.dto.*;
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

import java.util.concurrent.ExecutionException;

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
    @PostMapping("/wordSimilar")
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
        ParagraphOut out = getPpl(lexerOut,accessToken);
        return R.success(out);
    }

    @ApiOperation("文章AI伪原创")
    @PostMapping("/aiArticle")
    public R articleReplace(@RequestBody ReqBody<TextDto> para) {
        try {
            return R.success(lexerService.replaceParagraph(para.getParams(),getAccessToken()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return R.error(CommonEnum.AI_ARTICLE_ERROR);
    }

    private ParagraphOut getPpl(LexerOut lexerOut,String accessToken) {
        if (lexerOut == null) {
            throw new BizException(CommonEnum.PARAM_ERROR);
        }
        if (StrUtil.isBlank(accessToken)) {
            throw new BizException(CommonEnum.TOKEN_NOT_FOUND);
        }

        ReplaceTextOut rto = lexerService.sliceSentence(lexerOut);
        String replaceResult = rto.getReplace();
        if (StrUtil.isNotBlank(replaceResult)) {
            // DNN语言模型计算通顺度
            DnnModelOut out =  lexerService.analyseDnnModel(new TextDto(replaceResult),accessToken);
            return new ParagraphOut(out.getText(),rto.getReplaceCount());
        }

        return new ParagraphOut(lexerOut.getText(),0);
    }
}
