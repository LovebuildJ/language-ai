package com.chenxin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 统一请求体
 * Created by 尘心 on 2020/9/19 0019.
 */
@Data
@ApiModel("统一请求体")
public class ReqBody<T> {

    @ApiModelProperty("应用名称")
    private String appName;

    /**
     * <p>
     *     此项目默认不启用, 只需传参数即可, 其他信息均可传递 ""
     *     签名规则：
     *      1. 服务中台颁发 appName 和 appSecret
     *      2. 前端根据 appName + JSON(param) + version + timestamp + appSecret 生成字符串
     *      3. sha512算法加密字符串
     *      4. 服务中台进行拦截和验证请求, 验证通过则放行。
     * </p>
     */
    @ApiModelProperty("签名")
    private String sign;

    @ApiModelProperty("请求参数对象(DTO)")
    private T params;

    @ApiModelProperty("版本号")
    private String version;

    @ApiModelProperty("时间戳")
    private String timestamp;
}
