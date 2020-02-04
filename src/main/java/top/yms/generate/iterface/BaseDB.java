package top.yms.generate.iterface;

import java.sql.Connection;

public interface BaseDB {
	
	/**
	 * 获取数据库连接
	 */
	public Connection getConnection();
}
