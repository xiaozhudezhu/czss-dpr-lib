package com.swinginwind.czss.dpr;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	
	@Test
    public void test() {
        //  私钥
        String SKp = czssLib.genSK(20210518L, "skp.stk");
        System.out.println("SKp：" + SKp);

        //  用户账号和密码
        String STR = "123456"+"echy123456";
        System.out.println("STR："+ STR);

        String UPFi = "{\"ismotan\":false,\"memberNo\":\"123456\",\"chineseName\":\"测试\",\"englishName\":\"ceshi\"}";
        System.out.println("UPFi：" + UPFi);

        //  sm3编码
        String HSTR = czssLib.sm3(STR);
        System.out.println("HSTR：" + HSTR);
        String FSTR = czssLib.sm3("123456");

        // 字符串数据对称加密
        String cpHstr = czssLib.encString(HSTR, SKp);
        System.out.println("cpHstr：" + cpHstr);
        String cpUpfi = czssLib.encString(UPFi, SKp);
        System.out.println("cpUpfi：" + cpUpfi);


        //  函数部分
        String KFf = czssLib.genSKFpart(new Date().getTime(),"CZ1234.skf");
        System.out.println("KFf：" + KFf);

        //  生成运算字典
        String dictFf = czssLib.genDictionary(KFf, "dictionary.dict");
        System.out.println("dictFf：" + dictFf);


        //  1.2 生成密钥多项式部分
        String kz = czssLib.genSKZpart(202105181L, HSTR, "keName.skz");
        System.out.println("kz：" + kz);

        //  拼装完整私钥文件
        String sk = czssLib.composeSK(KFf, kz, "skName.stk");
        System.out.println("sk：" + sk);

        //  生成转换字典
        String tran = czssLib.genTransferDictSS(SKp, sk, "tranName.tran");
        System.out.println("tran：" + tran);

        // 密文转换
        String ciHstr = czssLib.tranSS(cpHstr,tran,dictFf);
        System.out.println("ciHstr：" + ciHstr);
        String ciUpfi = czssLib.tranSS(cpUpfi,tran,dictFf);
        System.out.println("ciUpfi：" + ciUpfi);

        // 传输给机上portal的数据
        Map<String, String> encryptObj = new HashMap<>();
        encryptObj.put("KFf", KFf);
        encryptObj.put("Dict_Ff", dictFf);
        encryptObj.put("Ci_UPFi", ciUpfi);
        encryptObj.put("tran", tran);
        Map<String, String> fsrtObject =new HashMap<>();
        fsrtObject.put(FSTR, ciHstr);


        System.out.println("===================== 起飞后 ============================");
        //  前端输入账号和密码
        String jsStr = "123456"+"echy123456";
        System.out.println("jsStr：" + jsStr);

        // sm3编码
        String jsHstr = czssLib.sm3(jsStr);
        System.out.println("jsHstr：" + jsHstr);
        String jsFstr = czssLib.sm3("123456");
        System.out.println("jsFstr：" + jsFstr);

        // 生成密钥多项式部分
        String jsSkz = czssLib.genSKZpart(202105181L, jsHstr, "jsSkz.skz");
        System.out.println("jsSkz：" + jsSkz);

        // 拼装完整私钥文件，此处KFf是在前面获取
        String jsStk = czssLib.composeSK(encryptObj.get("KFf"), jsSkz, "jsStk.stk");
        System.out.println("jsStk：" + jsStk);

        // 字符串数据对称加密
        String jsCHstr =  czssLib.encString(jsHstr, jsStk);
        System.out.println("jsCHstr：" + jsCHstr);

        // 根据FSTR查找Ci_HSTR
        String Ci_HSTR = fsrtObject.get(jsFstr);
        System.out.println("Ci_HSTR：" + Ci_HSTR);

        // 密文等值测试
        boolean result = czssLib.equalString(Ci_HSTR, jsCHstr, encryptObj.get("Dict_Ff"), encryptObj.get("tran"));
        System.out.println("result：" + result);
        String decStr = czssLib.decString(encryptObj.get("Ci_UPFi"), jsStk);
        System.out.println("decStr：" + decStr);
        if (result){
            // 字符串数据解密
            decStr = czssLib.decString(encryptObj.get("Ci_UPFi"), jsStk);
            System.out.println("decStr：" + decStr);
        } else {
            System.out.println("登录失败！");
        }
    }

}
