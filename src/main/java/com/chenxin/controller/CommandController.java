package com.chenxin.controller;

import com.chenxin.base.BaseController;
import com.chenxin.model.R;
import com.chenxin.model.ReqBody;
import com.chenxin.model.dto.LoginDto;
import com.chenxin.service.CommandService;
import com.chenxin.util.CommonEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

/**
 * 指令接口
 * <p>
 *     包含一些初始化的指令接口
 * </p>
 * Created by 尘心 on 2020/9/20 0020.
 */
@Api(tags = "指令接口")
@RestController
@RequestMapping("/command")
public class CommandController extends BaseController{

    @Autowired
    private CommandService commandService;

    @ApiOperation("初始化redis数据库")
    @PostMapping("/initRedis")
    public R initRedis(@RequestBody ReqBody<LoginDto> para) {
        if (para == null) {
            return R.error(CommonEnum.BODY_NOT_MATCH);
        }

        LoginDto dto = para.getParams();
        if (dto == null) {
            return R.error(CommonEnum.USERNAME_PASS_ERRPR);
        }

        try {
            commandService.initRedis();
        } catch (UnsupportedEncodingException e) {
            return R.error(CommonEnum.IMPORT_WORDS);
        }
        return R.success("初始化词库至redis成功!");
    }
}
