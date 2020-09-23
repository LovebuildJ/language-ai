package com.chenxin.util.nlp;

import com.chenxin.model.bo.MapBo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同义词库
 * Created by 尘心 on 2020/9/18 0018.
 */
public class SimilarWords {

    /**
     * 词库容器
     */
    private static List<String> list;
    private static Map<String,String> map;

    /**
     * 加载词库
     */
    public static List<String> loadWords() {
        if (list != null&&list.size()!=0) {
            return list;
        }

        // 初始化2w
        list = new ArrayList<>(20000);
        // 使用流读取词库
        InputStream stream = SimilarWords.class.getResourceAsStream("/res/word.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
            String line = "";
            while ((line = br.readLine())!=null) {
                System.out.println("加载词库中 【>>>>>>>>>>>>>>>>>>>>】"+line);
                String[] lineArr = line.split(" ");
                for (String s : lineArr) {
                    list.add(s.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 加载词库
     */
    public static List<MapBo<String,String>> loadWordsMap() {
        List<MapBo<String,String>> mapList = new ArrayList<>(20000);
        // 使用流读取词库
        InputStream stream = SimilarWords.class.getResourceAsStream("/res/word.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        try {

            String line = "";
            while ((line = br.readLine())!=null) {
                System.out.println("加载词库中 【>>>>>>>>>>>>>>>>>>>>】"+line);
                String[] lineArr = line.split(" ");
                // 以防一行有三个词的情况
                if (lineArr.length == 2) {
                    MapBo<String, String> mapBo = new MapBo<>();
                    mapBo.setKey(lineArr[0].trim());
                    mapBo.setVal(lineArr[1].trim());
                    mapList.add(mapBo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapList;
    }
}
