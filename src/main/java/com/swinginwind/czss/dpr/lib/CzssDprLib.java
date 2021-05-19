package com.swinginwind.czss.dpr.lib;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class CzssDprLib {

	private CzssGoLib czssGoLib;

	private String fileDir;

	/**
	 * 生成密钥函数部分
	 * 
	 * @param seed
	 *            随机数种子，非必填
	 * @param filename
	 *            文件名，非必填
	 * @return 密钥函数部分文件URL
	 */
	public String genSKFpart(Long seed, String filename) {
		if (seed == null)
			seed = System.currentTimeMillis();
		if (StringUtils.isEmpty(filename))
			filename = UUID.randomUUID() + ".skf";
		czssGoLib.GenSKFpart(seed, new CzssGoLib.GoString.ByValue(fileDir + File.separator + filename));
		return filename;
	}

	/**
	 * 生成密钥多项式部分
	 * 
	 * @param seed
	 *            随机数种子
	 * @param hashstr
	 *            用于身份认证的字符串Hash
	 * @param filename
	 *            文件名
	 * @return 密钥多项式部分文件URL
	 */
	public String genSKZpart(Long seed, String hashstr, String filename) {
		if (seed == null)
			seed = System.currentTimeMillis();
		if (StringUtils.isEmpty(filename))
			filename = UUID.randomUUID() + ".skz";
		czssGoLib.GenSKZpart(seed, new CzssGoLib.GoString.ByValue(hashstr),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + filename));
		return filename;
	}

	/**
	 * 拼装完整私钥文件
	 * 
	 * @param fpart
	 *            函数部分文件URL
	 * @param zpart
	 *            多项式部分文件URL
	 * @param filename
	 *            文件名
	 * @return 私钥文件URL
	 */
	public String composeSK(String fpart, String zpart, String filename) {
		if (StringUtils.isEmpty(filename))
			filename = UUID.randomUUID() + ".stk";
		czssGoLib.ComposeSK(new CzssGoLib.GoString.ByValue(fileDir + File.separator + fpart),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + zpart),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + filename));
		return filename;
	}

	/**
	 * 生成私钥文件
	 * 
	 * @param seed
	 *            随机数种子
	 * @param filename
	 *            文件名
	 * @return 私钥文件URL
	 */
	public String genSK(Long seed, String filename) {
		if (seed == null)
			seed = System.currentTimeMillis();
		if (StringUtils.isEmpty(filename))
			filename = UUID.randomUUID() + ".stk";
		czssGoLib.GenSK(seed, new CzssGoLib.GoString.ByValue(fileDir + File.separator + filename));
		return filename;
	}

	/**
	 * 
	 * @param seed
	 * @param skfile
	 * @param filename
	 * @return
	 *//*
		 * public String genPK(Long seed, String skfile, String filename) { if
		 * (seed == null) seed = System.currentTimeMillis(); if
		 * (StringUtils.isEmpty(filename)) filename = UUID.randomUUID() +
		 * ".pck"; czssGoLib.GenPK(seed, new CzssGoLib.GoString.ByValue(skfile),
		 * new CzssGoLib.GoString.ByValue(fileDir + File.separator + filename));
		 * return filename; }
		 */

	/**
	 * 生成运算字典（含镜像密钥及运算密钥）
	 * 
	 * @param skfile
	 *            私钥或函数部分文件URL
	 * @param filename
	 *            文件名
	 * @return 运算字典文件URL
	 */
	public String genDictionary(String skfile, String filename) {
		if (StringUtils.isEmpty(filename))
			filename = UUID.randomUUID() + ".dict";
		czssGoLib.GenDictionary(new CzssGoLib.GoString.ByValue(fileDir + File.separator + skfile),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + filename));
		return filename;
	}

	/**
	 * 生成转换字典（ss: 私钥-私钥，含转换密钥）
	 * 
	 * @param sk_out
	 *            转出私钥文件URL
	 * @param sk_in
	 *            转入私钥文件URL
	 * @param filename
	 *            文件名
	 * @return 转换字典文件URL
	 */
	public String genTransferDictSS(String sk_out, String sk_in, String filename) {
		if (StringUtils.isEmpty(filename))
			filename = UUID.randomUUID() + ".tran";
		czssGoLib.GenTransferDictSS(new CzssGoLib.GoString.ByValue(fileDir + File.separator + sk_out),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + sk_in),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + filename));
		return filename;
	}

	/**
	 * 字符串数据对称加密
	 * 
	 * @param message
	 *            明文字符串
	 * @param skfile
	 *            私钥文件URL
	 * @return 密文base64(byte)
	 */
	public String encString(String message, String skfile) {
		Pointer pointer = czssGoLib.EncString(new CzssGoLib.GoString.ByValue(message),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + skfile));
		String result = pointer.getString(0);
		free(pointer);
		return result;
	}

	/**
	 * 字符串数据解密
	 * 
	 * @param cipher
	 *            密文字符串base64(byte)
	 * @param skfile
	 *            私钥文件URL
	 * @return 明文字符串
	 */
	public String decString(String cipher, String skfile) {
		Pointer pointer = czssGoLib.DecString(new CzssGoLib.GoString.ByValue(cipher),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + skfile));
		String result = pointer.getString(0);
		free(pointer);
		return result;
	}

	/**
	 * 密文转换SS
	 * 
	 * @param cipher
	 *            源密文字符串base64(byte)
	 * @param tranfile
	 *            转换字典SS URL
	 * @param dictfile
	 *            运算字典 URL
	 * @return 目标密文字符串base64
	 */
	public String tranSS(String cipher, String tranfile, String dictfile) {
		Pointer pointer = czssGoLib.TranSS(new CzssGoLib.GoString.ByValue(cipher),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + tranfile),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + dictfile));
		String result = pointer.getString(0);
		free(pointer);
		return result;
	}

	/**
	 * 密文等值测试
	 * 
	 * @param c1
	 *            密文字符串1 base64(byte)
	 * @param c2
	 *            密文字符串2 base64(byte)
	 * @param dictfile
	 *            运算字典URL
	 * @param tranfile
	 *            运算字典URL
	 * @return 密文是否相等
	 */
	public boolean equalString(String c1, String c2, String dictfile, String tranfile) {
		return czssGoLib.EqualString(new CzssGoLib.GoString.ByValue(c1), new CzssGoLib.GoString.ByValue(c2),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + dictfile),
				new CzssGoLib.GoString.ByValue(fileDir + File.separator + tranfile));
	}
	
	/**
	 * SM3
	 * @param str
	 * @return 加密后的字符串（base64）
	 */
	public String sm3(String str) {
		Pointer pointer = czssGoLib.SM3(new CzssGoLib.GoString.ByValue(str));
		String result = pointer.getString(0);
		free(pointer);
		return result;
	}
	
	private void free(Pointer pointer) {
		pointer.clear(pointer.getString(0).getBytes().length);
		long peer = Pointer.nativeValue(pointer);
		//Native.free(peer);//手动释放内存
		Pointer.nativeValue(pointer, 0);//避免Memory对象被GC时重复执行Nativ.free()方法
	}


	/**
	 * @return the czssGoLib
	 */
	public CzssGoLib getCzssGoLib() {
		return czssGoLib;
	}

	/**
	 * @param czssGoLib
	 *            the czssGoLib to set
	 */
	public void setCzssGoLib(CzssGoLib czssGoLib) {
		this.czssGoLib = czssGoLib;
	}

	/**
	 * @return the fileDir
	 */
	public String getFileDir() {
		return fileDir;
	}

	/**
	 * @param fileDir
	 *            the fileDir to set
	 */
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	public static interface CzssGoLib extends Library {

		void GenSKFpart(long seed, GoString.ByValue filename);

		void GenSKZpart(long seed, GoString.ByValue hashstr, GoString.ByValue filename);

		void ComposeSK(GoString.ByValue fpart, GoString.ByValue zpart, GoString.ByValue filename);

		void GenSK(long seed, GoString.ByValue filename);

		void GenPK(long seed, GoString.ByValue skfile, GoString.ByValue filename);

		void GenDictionary(GoString.ByValue skfile, GoString.ByValue filename);

		void GenTransferDictSS(GoString.ByValue sk_out, GoString.ByValue sk_in, GoString.ByValue filename);

		Pointer EncString(GoString.ByValue message, GoString.ByValue skfile);

		Pointer DecString(GoString.ByValue cipher, GoString.ByValue skfile);

		Pointer TranSS(GoString.ByValue cipher, GoString.ByValue tranfile, GoString.ByValue dictfile);

		boolean EqualString(GoString.ByValue c1, GoString.ByValue c2, GoString.ByValue dictfile,
				GoString.ByValue tranfile);

		Pointer SM3(GoString.ByValue str);
		
		// C type struct { const char *p; GoInt n; }
		public class GoString extends Structure {
			public static class ByValue extends GoString implements Structure.ByValue {
				public ByValue() {
				}

				public ByValue(String s) {
					this.p = s;
					this.n = s.getBytes().length;
				}

			}

			public String p;
			public long n;

			protected List<String> getFieldOrder() {
				return Arrays.asList(new String[] { "p", "n" });
			}
		}

	}

}
