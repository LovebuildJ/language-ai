package com.chenxin;

import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;

import java.io.*;

/**
 * 非单元测试 测试类
 * Created by 尘心 on 2020/9/18 0018.
 */
public class NotUnitTest {

    public static void main(String[] args) {
        calculateWordLength();
//        getPath();
    }

   public static void calculateWordLength() {
        String[] array = {"安定","安宁","安分守己","奉公守法","安分守纪","循规蹈矩"};
        for (String a : array) {
            for (String b : array) {
                System.out.println(a + "\t" + b + "\t之间的距离是\t" + CoreSynonymDictionary.distance(a, b));
            }
        }

    }

    public static void getPath() {

//        String filePath =File.separator+"res"+File.separator+"word.txt";
        InputStream stream = NotUnitTest.class.getResourceAsStream("/res/word.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
            String line = "";
            while ((line = br.readLine())!=null) {
                System.out.println("加载词库中 >>>>>>>>>>>>>>>>>>>>"+line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
