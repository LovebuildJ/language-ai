package com.chenxin.base;

import com.chenxin.auth.BaiDuAuth;
import com.chenxin.model.dto.BaiDuAuthOut;
import com.chenxin.util.auth.AuthContainer;
import com.chenxin.util.consts.AiConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller 公共父类
 * Created by 尘心 on 2020/9/19 0019.
 */
@Component
public abstract class BaseController {

    @Autowired
    private BaiDuAuth baiDuAuth;

    /**
     * 获取访问令牌
     */
    public String getAccessToken() {
        // 容器当中已经有了, 则直接从容器当中获取
        if (AuthContainer.isContains(AiConstant.TOKEN_KEY)) {
            return AuthContainer.getAuthToken(AiConstant.TOKEN_KEY);
        }
        // 容器当中没有, 则去获取访问令牌
        BaiDuAuthOut out = baiDuAuth.getAccessToken();
        if (out!=null) {
            return out.getAccess_token();
        }

        return null;
    }
}
