package com.swinginwind.czss.dpr.lib;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sun.jna.Native;
import com.swinginwind.czss.dpr.lib.CzssDprLib;
import com.swinginwind.czss.dpr.lib.CzssDprLib.CzssGoLib;


@Configuration
public class CzssDprLibConfig {
	
	@Value("${czss.dpr.libPath}")
	private String libPath;
	
	@Value("${czss.dpr.fileDir}")
	private String fileDir;
		
	@Bean("czssDprLib")
	public CzssDprLib getCzssLibAuth() {
		File fileDirf = new File(fileDir);
		if(!fileDirf.exists())
			fileDirf.mkdirs();
		CzssDprLib lib = new CzssDprLib();
		lib.setFileDir(fileDir);
		try {
			CzssGoLib czssGoLib = (CzssGoLib) Native.loadLibrary(libPath, CzssGoLib.class);
			lib.setCzssGoLib(czssGoLib);
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
		return lib;
	}

}
