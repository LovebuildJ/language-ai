package com.chenxin.util.system;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 系统工具
 * Created by 尘心 on 2020/9/18 0018.
 */
public class SystemUtil {

    /**
     * resource 资源文件夹根路径下的文件
     */
    public static String getResRootPath(String filePath) {
        //获取跟目录
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(path!=null&&!path.exists()) {
            path = new File("");
        }

        return path.getAbsolutePath()+File.separator + filePath;
    }
}
