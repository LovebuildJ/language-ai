package com.chenxin;

import com.chenxin.auth.BaiDuAuth;
import com.chenxin.model.dto.BaiDuAuthOut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiApplicationTests {

	@Autowired
	private BaiDuAuth baiDuAuth;

	@Test
	void contextLoads() {

		BaiDuAuthOut accessToken = baiDuAuth.getAccessToken();
		System.out.println(accessToken);
	}

}
