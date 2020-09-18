package com.chenxin;

import com.chenxin.auth.BaiDuAuth;
import com.chenxin.model.dto.BaiDuAuthOut;
import com.chenxin.model.dto.LexerOut;
import com.chenxin.model.dto.TextDto;
import com.chenxin.service.LexerService;
import com.hankcs.hanlp.dictionary.CoreSynonymDictionary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiApplicationTests {

	@Autowired
	private BaiDuAuth baiDuAuth;

	@Autowired
	private LexerService lexerService;

	@Test
	void contextLoads() {

		BaiDuAuthOut accessToken = baiDuAuth.getAccessToken();
		System.out.println(accessToken);
	}

	@Test
	void analyseLexer() {
		TextDto textDto = new TextDto();
		textDto.setText("我是一个中国人, 不是giao桑");
		LexerOut lexerOut = lexerService.analyseLexer(textDto,"24.79acab96e4c32ee735dded7e63e01ff4.2592000.1602994463.282335-22686292");
		System.out.println(lexerOut);
	}

	/**
	 * 计算词义的距离
	 */
	@Test
	void calculateWordLength() {
		String[] array = {"香蕉","苹果","菠萝","足球","窗户"};
		for (String a : array) {
			for (String b : array) {
				System.out.println(a + "\t" + b + "\t之间的距离是\t" + CoreSynonymDictionary.distance(a, b));
			}
		}

	}
}
