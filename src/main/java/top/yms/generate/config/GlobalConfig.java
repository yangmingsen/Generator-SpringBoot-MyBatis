package top.yms.generate.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import top.yms.generate.code.Generate;

public class GlobalConfig {
	
	private GlobalConfig() {}
	
	private static Map<String, String> map = 
			new HashMap<String, String>();
	
	static {
		InputStream in = GlobalConfig.class.getClassLoader().getResourceAsStream("application.properties");
		
		if (in == null) {
			System.out.println("class of GlobalConfig无法找到配置文件application.properties");
		}

		Properties prop = new Properties();
		try {
			prop.load(in);
		
			for (String key : prop.stringPropertyNames()) {
				map.put(key,prop.getProperty(key));
			}
			
		} catch (Exception e) {
			System.out.println("GlobalConfig load config file application.properties fails："+e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static String getValue(String key) {
		return map.get(key);
	}
	
	public static String putKV(String key, String value) {
		return map.put(key,value);
	}
	
	public static void main(String[] args) {
		
	}
	
}
