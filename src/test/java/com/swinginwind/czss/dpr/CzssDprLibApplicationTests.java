package com.swinginwind.czss.dpr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.swinginwind.czss.dpr.lib.CzssDprLib;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CzssDprLibApplicationTests {
	
	@Autowired
	CzssDprLib czssLib;

	@Test
	public void contextLoads() {
		System.out.println(czssLib.genSKFpart(null, "1.skf"));
		System.out.println(czssLib.genSKZpart(null, "123Hello", "1.skz"));
		System.out.println(czssLib.composeSK("1.skf", "1.skz", "1.stk"));
		System.out.println(czssLib.genSK(null, "2.stk"));
		System.out.println(czssLib.genDictionary("1.stk", "1.dict"));
		System.out.println(czssLib.genDictionary("2.stk", "2.dict"));
		System.out.println(czssLib.genTransferDictSS("1.stk", "2.stk", "1-2.tran"));
		System.out.println(czssLib.sm3("123"));
		int count = 0;
		while(true) {
			String encStr1 = czssLib.encString("123Helloddddddddd8900dfdf你好", "1.stk");
			/*System.out.println(encStr1);
			System.out.println(czssLib.decString(encStr1, "1.stk"));
			System.out.println(czssLib.decString(encStr1, "1.stk"));
			String encStr2 = czssLib.tranSS(encStr1, "1-2.tran", "2.dict");
			System.out.println(encStr2);
			System.out.println(czssLib.decString(encStr2, "2.stk"));
			String encStr3 = czssLib.encString("123Helloddddddddd8900dfdfg", "2.stk");
			System.out.println(encStr3);
			System.out.println(czssLib.equalString(encStr2, encStr3, "2.dict", "1-2.tran"));*/
			count ++;
			if(count % 1000 == 0) {
				System.out.println(count);
				break;
			}
		}
	}

}
