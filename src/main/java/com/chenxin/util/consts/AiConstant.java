package com.chenxin.util.consts;

/**
 * @Description AI 常量类
 * @Date 2020/9/18 10:59
 * @Author by 尘心
 */
public final class AiConstant {

    /** 授权类型：固定写死 */
    public static final String GRANT_TYPE = "client_credentials";

    /** 日期格式 */
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";

    /** token 存储的key 值 */
    public static final String TOKEN_KEY = "accessToken";

    /** access 为必传参数, 此为参数名称 */
    public static final String URL_TOKEN_NAME = "access_token";

    /** 通顺度, 默认低于500 通顺度就算通过 */
    public static final double PPL_LIMIT = 1000.00d;

    /** 重试次数：默认重试10次 */
    public static final int TRY_COUNT = 10;
}
