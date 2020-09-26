package com.chenxin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chenxin.base.BaseAuth;
import com.chenxin.base.BaseService;
import com.chenxin.exception.BizException;
import com.chenxin.model.bo.LexerItemBo;
import com.chenxin.model.dto.*;
import com.chenxin.util.CommonEnum;
import com.chenxin.util.auth.AuthContainer;
import com.chenxin.util.consts.AiConstant;
import com.chenxin.util.consts.LexerConstants;
import com.chenxin.util.http.BaiDuUrl;
import com.chenxin.util.http.HttpHeader;
import com.chenxin.util.nlp.SimilarWords;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Description 词法分析
 * @Date 2020/9/18 15:38
 * @Author by 尘心
 */
@Slf4j
@Service
public class LexerService extends BaseAuth{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 将句子按照词义进行切分
     * @param text 文本对象
     */
    public LexerOut analyseLexer(TextDto text,String accessToken) {
        if (text == null) {
            throw new BizException(CommonEnum.BODY_NOT_MATCH);
        }

        log.info("正在切词中...");

        String realUrl = BaiDuUrl.getRealUtf8Url(NORMAL_LEXER_URL, accessToken);
        // 请求参数 , 格式 json
        String param = JSON.toJSONString(text);
        // 获取json类型请求头
        HttpHeaders jsonHeader = HttpHeader.getJsonHeader();
        HttpEntity<String> httpEntity = new HttpEntity<>(param, jsonHeader);
        // 请求数据
        ResponseEntity<String> postForEntity = restTemplate.postForEntity(realUrl, httpEntity, String.class);
        if (postForEntity.getStatusCode().equals(HttpStatus.OK)) {
            String body = postForEntity.getBody();
            return JSON.parseObject(body, LexerOut.class);
        }

        return new LexerOut();
    }

    /**
     * 切割句子, 同义词替换
     */
    public ReplaceTextOut sliceSentence(LexerOut lexerOut) {
        if (lexerOut == null) {
            throw new BizException(CommonEnum.PARAM_ERROR);
        }

        log.info("正在进行同义词替换...");

        // 替换词数统计
        int replaceCount = 0;
        // 需要从处理的词性数组
        String[] wordPos = {LexerConstants.A,LexerConstants.N,LexerConstants.V,LexerConstants.AD,LexerConstants.VD};
        List<String> posList = Arrays.asList(wordPos);

        StringBuilder builder = new StringBuilder();
        List<LexerItemBo> items = lexerOut.getItems();
        if (CollUtil.isNotEmpty(items)) {
            for (LexerItemBo item : items) {
                // 形容词, 动词, 名词(特有名词, 专属名词不进行处理)
                String pos = item.getPos();
                if (StrUtil.isNotBlank(pos)) {
                    if (posList.contains(pos)) {
                        String replaceWord = null;
                        // 替换成同义词
                        String sourceWord = item.getItem();
                        // 从redis同义词库中查找所有同义词
                        List<String> keys = new ArrayList<>();
                        Cursor<String> cursor = scan(redisTemplate,sourceWord+"*",AiConstant.KEYS_COUNT);
                        while (cursor.hasNext()) {
                            keys.add(cursor.next());
                        }

                        // 使用set, 去除相同的词, 提升替换效率
                        TreeSet<String> set = new TreeSet<>();
                        for (String key : keys) {
                            String similarWord = (String) redisTemplate.opsForValue().get(key);
                            set.add(similarWord);
                        }

                        List<String> similarList = new ArrayList<>();
                        for (String word : set) {
                            if (!StrUtil.isBlank(word)) {
                                long distance = CoreSynonymDictionary.distance(sourceWord, word);
                                if (distance == 0) {
                                    // 将符合要求的同义词收集
                                    similarList.add(word);
                                }
                            }

                            log.info("找到如下同义词!"+word);

                        }

                        if (similarList.size()!=0) {
                            if (similarList.size() == 1) {
                                replaceWord = similarList.get(0);
                            }else {
                                Random rand = new Random();
                                String randomElement = similarList.get(rand.nextInt(similarList.size()));
                                if (!sourceWord.equals(randomElement)) {
                                    // 除去自己, 从最相似的词语当中随机取一个
                                    replaceWord = randomElement;
                                }
                                log.info("随机从相似词中取一个进行替换~");
                            }

                            replaceCount++;
                        }

                        // 句子重组
                        if (StrUtil.isBlank(replaceWord)) {
                            builder.append(sourceWord);
                        }else {
                            builder.append(replaceWord);
                        }

                    }else {
                        builder.append(item.getItem());
                    }
                }else {
                    builder.append(item.getItem());
                }

            }
        }

        // 封装返回对象
        ReplaceTextOut out = new ReplaceTextOut();
        out.setSource(lexerOut.getText());
        out.setReplace(builder.toString());
        out.setReplaceCount(replaceCount);

        return out;
    }


    /**
     *  计算词义相似度
     * @param word 词语对象
     */
    public SimilarWordOut calculateWordSimilarScore(SimilarWordDto word) {
        log.info("正在计算两个单词的相似度得分");
        if (word == null) {
            throw new BizException(CommonEnum.BODY_NOT_MATCH);
        }
        // 获取源词语
        String source =  word.getSource();
        // 获取需要计算的目标词语
        String target = word.getTarget();
        if (StrUtil.isBlank(source)||StrUtil.isBlank(target)) {
            throw new BizException(CommonEnum.WORD_NULL);
        }
        // 使用NLP计算相似度得分
        long score = CoreSynonymDictionary.distance(source, target);
        SimilarWordOut swo = new SimilarWordOut();
        BeanUtils.copyProperties(word,swo);
        swo.setScore(score);
        // 返回得分
        return swo;
    }


    /**
     * DNN语言模型处理
     */
    public DnnModelOut analyseDnnModel(TextDto text,String accessToken) {
        log.info("正在进行DNN语言模型校验...");
        String realUrl = BaiDuUrl.getRealUtf8Url(DNN_LAN_MODEL_URL, accessToken);
        // 请求参数 , 格式 json
        String param = JSON.toJSONString(text);
        // 获取json类型请求头
        HttpHeaders jsonHeader = HttpHeader.getJsonHeader();
        HttpEntity<String> httpEntity = new HttpEntity<>(param, jsonHeader);
        // 请求数据
        ResponseEntity<String> postForEntity = restTemplate.postForEntity(realUrl, httpEntity, String.class);
        if (postForEntity.getStatusCode().equals(HttpStatus.OK)) {
            String body = postForEntity.getBody();
            return JSON.parseObject(body, DnnModelOut.class);
        }

        return new DnnModelOut();
    }


    /**
     * 整个文章替换
     */
    public ParagraphOut replaceParagraph(TextDto text,String accessToken) throws ExecutionException, InterruptedException {
        log.info("正在进行文章替换 ...");
        checkText(text);
        checkAccessToken(accessToken);
        // 文章
        String article = text.getText();
        List<String> paragraphs = sliceArticle(article);
        int size = paragraphs.size();
        if (size>35) {
            // 超过 3500 句, 直接抛异常
            throw new BizException(CommonEnum.TEXT_TO_LONG);
        }

        StringBuilder sb = new StringBuilder();
        int replaceTotal = 0;
        int threadCount = 0;
        if (size>0) {
            log.info("开启多线程, 批量分析, 检测, 替换...");
            if (size == 1) {
                // 一个段落, 则分多个句子进行。 一个句子一个线程
                String p =  paragraphs.get(0);
                String[] sentence = p.split(AiConstant.PERIOD);
                for (String s : sentence) {
                    Future<ArticleReplaceOut> asyncResult = getReplaceResult(s,accessToken);
                    ArticleReplaceOut arOut =asyncResult.get();

                    sb.append(arOut.getReplaceText());

                    replaceTotal+=arOut.getReplaceCount();
                    if (asyncResult.isDone()) {
                        threadCount ++;
                    }
                }
            }else if (size>1) {
                // 有多少个段落, 则开多少线程, 最多不能超过35个线程
                for (int i = 0; i < size; i++) {
                    Future<ArticleReplaceOut> asyncResult = getReplaceResult(paragraphs.get(i),accessToken);
                    ArticleReplaceOut arOut =asyncResult.get();

                    sb.append(arOut.getReplaceText());

                    replaceTotal+=arOut.getReplaceCount();
                    if (asyncResult.isDone()) {
                        threadCount ++;
                    }
                }
            }

        }

        while (threadCount < size) {
            Thread.sleep(100);
        }

        return new ParagraphOut(sb.toString(),replaceTotal);
    }

    /**
     * 异步多线程处理段落
     */
    @Async
    private Future<ArticleReplaceOut> getReplaceResult(String sentence,String accessToken) {
        log.info("线程启动, 正在进行文本分析替换检测...");

        // 替换词数统计
        int total = 0;
        // 组合段落
        StringBuilder sb = new StringBuilder();
        // 逐句分析替换 切分的句子默认没有带"。"号
        String[] sen = sentence.split(AiConstant.PERIOD);
        for (String s : sen) {
            // 分析词义
            LexerOut lo = analyseLexer(new TextDto(s),accessToken);
            // 替换结果
            ReplaceTextOut reOut = sliceSentence(lo);
            String result = reOut.getReplace();
            total += reOut.getReplaceCount();
            // DNN语言模型检测
            DnnModelOut out = analyseDnnModel(new TextDto(result),accessToken);
            if (out == null||StrUtil.isBlank(out.getText())) {
                sb.append(s);
                if (!s.endsWith("。")) {
                    sb.append("。");
                }

            }else {
                sb.append(result);
                if (!s.endsWith("。")) {
                    sb.append("。");
                }
            }
        }

        ArticleReplaceOut arOut = new ArticleReplaceOut();
        arOut.setSourceText(sentence);
        arOut.setReplaceText(sb.toString());
        arOut.setReplaceCount(total);
        // 返回替换结果
        return new AsyncResult<>(arOut);
    }

    /**
     * 将文章进行切割
     * @param article 文章
     */
    private List<String> sliceArticle(String article) {
        List<String> sentenceList = new ArrayList<>();
        // 超过范围, 进行切割
        if (article.length()>AiConstant.MAX_LENGTH) {
            // 将文章全部切割成句子
            String[] sentences = article.split(AiConstant.PERIOD);
            // 获取句子总数
            int len = sentences.length;
            // 计数器
            int count = 0;
            // 计算总数
            int title = 0;
            // 段落拼接
            StringBuilder sb = new StringBuilder();
            for (String sentence : sentences) {
                sb.append(sentence);
                if (count == 10) {
                    // 存储切分的段落
                    sentenceList.add(sb.toString());
                    // 重新赋值
                    sb = new StringBuilder();
                    // 计数器清零
                    count = 0;
                }
                if (title == len) {
                    sentenceList.add(sb.toString());
                }

                title++;
                count++;

            }

        }else {
            sentenceList.add(article);
        }

        return sentenceList;
    }

    /**
     * 校验文本
     * @param text 文本对象
     * @return 不为空返回true
     */
    private boolean checkText(TextDto text) {
        if (text == null) {
            throw new BizException(CommonEnum.TEXT_NULL);
        }
        // 判断内容
        String article = text.getText();
        if (StrUtil.isBlank(article)) {
            throw new BizException(CommonEnum.TEXT_NULL);
        }
        if (article.length()>100000) {
            throw new BizException(CommonEnum.TEXT_TO_LONG);
        }
        return true;
    }

    /**
     * 校验access token
     * @param accessToken 请求令牌
     * @return 令牌不为空 返回true
     */
    private boolean checkAccessToken(String accessToken) {
        if (StrUtil.isBlank(accessToken)) {
            throw new BizException(CommonEnum.TOKEN_NOT_FOUND);
        }
        return true;
    }

    /**
     * redis扫描相似键值
     */
    private Cursor<String> scan(RedisTemplate stringRedisTemplate, String match, int count){
        ScanOptions scanOptions = ScanOptions.scanOptions().match(match).count(count).build();
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) stringRedisTemplate.getKeySerializer();
        return (Cursor) stringRedisTemplate.executeWithStickyConnection((RedisCallback) redisConnection ->
                new ConvertingCursor<>(redisConnection.scan(scanOptions), redisSerializer::deserialize));
    }
}
