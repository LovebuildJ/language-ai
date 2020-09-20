package com.chenxin.controller;

import cn.hutool.core.util.StrUtil;
import com.chenxin.base.BaseController;
import com.chenxin.exception.BizException;
import com.chenxin.model.R;
import com.chenxin.model.ReqBody;
import com.chenxin.model.dto.DnnModelOut;
import com.chenxin.model.dto.LexerOut;
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

    @ApiOperation("文本分词")
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
        if (StrUtil.isNotBlank(out.getPpl())) {
            // 通顺度
            double ppl = Double.valueOf(out.getPpl());
            if (ppl>AiConstant.PPL_LIMIT) {
                // 语义不通顺 , 重新替换: 十次机会, 超过十次返回原来文本
                for (int i = 0; i < AiConstant.TRY_COUNT; i++) {
                    DnnModelOut dmo =  getPpl(lexerOut,accessToken);
                    if (dmo!=null) {
                        String pl = dmo.getPpl();
                        if (StrUtil.isNotBlank(pl)) {
                            double p = Double.valueOf(pl);
                            if (p<AiConstant.PPL_LIMIT) {
                                // 语句通顺, 跳出循环, 返回
                                return R.success(dmo.getText());
                            }
                        }
                    }
                }

                return R.success(para.getParams().getText());
            }
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
