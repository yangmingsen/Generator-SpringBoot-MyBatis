package top.yms.generate.iterface.impl;

import java.io.InputStream;
import java.sql.DriverManager;
import java.util.Properties;

import java.sql.Connection;

import top.yms.generate.config.GlobalConfig;
import top.yms.generate.iterface.BaseDB;

public class DBConnection implements BaseDB {
	
	private Connection connection = null;

	/**
	 * 获取数据库连接 impl
	 */
	public Connection getConnection() {
		return connection;
	}

	
	public DBConnection() {
		try {

			String driver = GlobalConfig.getValue("driver");
			String url = GlobalConfig.getValue("url");
			String username = GlobalConfig.getValue("username");
			String password = GlobalConfig.getValue("password");

			Class.forName(driver);//加载mysql驱动
			connection = DriverManager.getConnection(url, username, password);

			System.out.println("MySQL Successful connetcion");

		} catch (Exception e) {
			System.out.println("数据库连接失败："+e.getMessage());
			e.printStackTrace();

		}
	}
	
	/**
	 * 关闭数据库
	 * @param connection
	 */
	public void destory() {
		if(connection!=null) {
			try {
				connection.close();
				System.out.println("数据库关闭成功!");
			} catch (Exception e) {
				System.out.println("数据库关闭失败:"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	

}
