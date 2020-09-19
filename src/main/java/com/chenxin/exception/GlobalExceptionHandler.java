package com.chenxin.exception;

import com.chenxin.model.R;
import com.chenxin.util.CommonEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description 全局异常处理
 * @Date 2020/9/18 14:47
 * @Author by 尘心
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** 日志打印 */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /** 处理业务异常 */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public R bizExceptionHandler(HttpServletRequest request,BizException e) {
        logger.error("发生业务异常！原因是：{}",e.getErrorMsg());
        return R.error(e.getErrorCode(),e.getErrorMsg());
    }

    /** 处理空指针异常 */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public R nullPointException(HttpServletRequest request,NullPointerException e) {
        logger.error("发生空指针异常！原因是：{}",e.getMessage());
        e.printStackTrace();
        return R.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }

    /** 处理未知异常 */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public R otherException(HttpServletRequest request,NullPointerException e) {
        logger.error("发生未知异常！原因是：{}",e.getMessage());
        return R.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }
}
