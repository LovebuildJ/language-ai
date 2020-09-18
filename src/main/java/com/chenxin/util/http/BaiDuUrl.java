package com.chenxin.util.http;

import cn.hutool.core.util.StrUtil;
import com.chenxin.exception.BizException;
import com.chenxin.util.CommonEnum;
import com.chenxin.util.consts.AiConstant;

/**
 * @Description 百度AI 请求地址获取
 * @Date 2020/9/18 16:37
 * @Author by 尘心
 */
public class BaiDuUrl {

    /**
     * 获取真实请求路径(默认GBK)
     * @param sourceUrl   原url
     * @param accessToken 令牌
     * @return
     */
    public static String getRealUrl(String sourceUrl,String accessToken) {
        if (StrUtil.isBlank(sourceUrl)) {
            throw new BizException(CommonEnum.BODY_NOT_MATCH);
        }
        if (StrUtil.isBlank(accessToken)) {
            throw new BizException(CommonEnum.TOKEN_NOT_FOUND);
        }

        StringBuilder builder = new StringBuilder(sourceUrl);
        builder.append("?").append(AiConstant.URL_TOKEN_NAME).append("=").append(accessToken);

        return builder.toString();
    }


    /**
     * 获取真实请求路径(UTF-8)
     * @param sourceUrl   原url
     * @param accessToken 令牌
     */
    public static String getRealUtf8Url(String sourceUrl,String accessToken) {
        if (StrUtil.isBlank(sourceUrl)) {
            throw new BizException(CommonEnum.BODY_NOT_MATCH);
        }
        if (StrUtil.isBlank(accessToken)) {
            throw new BizException(CommonEnum.TOKEN_NOT_FOUND);
        }

        StringBuilder builder = new StringBuilder(sourceUrl);
        builder.append("?").append("charset=UTF-8").append("&");
        builder.append(AiConstant.URL_TOKEN_NAME).append("=").append(accessToken);

        return builder.toString();
    }
}
