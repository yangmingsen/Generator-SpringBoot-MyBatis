package top.yms.generate.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import top.yms.generate.entity.FieldsInfo;
import top.yms.generate.iterface.BaseDB;
import top.yms.generate.iterface.impl.DBConnection;

public class TableUtils {
	private static Connection connection = null;

	static {
		BaseDB baseDB = new DBConnection();
		connection = baseDB.getConnection();
	}

	/**
	 * 获取所有的表名信息
	 * 
	 * @return
	 */
	public static List<String> getAllTablesNames() {
		List<String> tableNames = new LinkedList<String>();
		if (connection == null) {
			System.out.println("数据库连接信息不正确,无法获取表名信息...");
			return tableNames;
		}
		try {
			java.sql.DatabaseMetaData databaseMetaData = connection.getMetaData();
			ResultSet rs = databaseMetaData.getTables(null, null, "%", new String[] { "TABLE" });
			while (rs.next()) {
				tableNames.add(rs.getString(3));//获取表名  => TABLE_NAME    
			}
		} catch (Exception e) {
			System.out.println("获取信息发生异常:" + e.getMessage());
			e.printStackTrace();
		}
		return tableNames;
	}
	

	/***
	 * 获取所有表信息
	 * @return
	 */
	public static Map<String, List<FieldsInfo>> getAllTablesInfo() {
		List<String> listTableNames = getAllTablesNames();//获取所有表名
		
		return getTableInfoFromDB(listTableNames);
		
	}
	
	/**
	 * 获取指定表信息
	 * 
	 * @return
	 */
	public static Map<String, List<FieldsInfo>> getSpecificTablesInfo(String [] tableNames) {
		List<String> listTNames = getAllTablesNames();//获取所有表名
	
		List<String> listTableNames = new ArrayList<String>();
		
		//找到tableNames 和listTableNames的交集
		for(int i=0; i<tableNames.length; i++) {
			
			int j=0;
			int jL = listTNames.size();
			
			for (; j<jL; j++) {
				if (tableNames[i].equals(listTNames.get(j))) {
					listTableNames.add(tableNames[i]);
					break;
				}
			}
			if (j == jL) {
				System.out.println("Exception: 表"+tableNames[i]+"在数据库中不存在");
			}
			
		}
		
		if (listTableNames.size() == 0) {
			System.out.println("Exception: 没有表需要生成");
			return null;
		}
		
		
		
		return getTableInfoFromDB(listTableNames);
	}
	
	
	/**
	 * 从数据库中获取表信息
	 * 
	 * @return
	 */
	private static Map<String, List<FieldsInfo>> getTableInfoFromDB(List<String> listTableNames) {
		Map<String, List<FieldsInfo>> tablesInfoMap = new HashMap<String, List<FieldsInfo>>();//所有表信息  表名=>表信息
		
		for (String tableName : listTableNames) {
			List<FieldsInfo> listFieldsInfos = new LinkedList<FieldsInfo>();
			
			try {
				DatabaseMetaData data = connection.getMetaData();
				ResultSet resultSet = data.getColumns(null, "%", tableName, "%"); //获得某个表的列信息
				while (resultSet.next()) {
					FieldsInfo persistent = new FieldsInfo();
					persistent.setName(resultSet.getString("COLUMN_NAME")); //获取设置字段名
					persistent.setType(resultSet.getString("TYPE_NAME"));   //获取设置字段类型
					persistent.setDesc(resultSet.getString("REMARKS"));     //获取设置字段描述
					// 默认第一项为主键
					if (listFieldsInfos.size() == 0) {
						persistent.setPrimary(Boolean.TRUE);
					}
					listFieldsInfos.add(persistent);
				}
			} catch (Exception e) {
				System.out.println(String.format("解析%s发生错误：", tableName) + e.getMessage());
				e.printStackTrace();
			}
			
			tablesInfoMap.put(tableName, listFieldsInfos);
		}
		
		
		return tablesInfoMap;
		
	}
	
	

	
	
	/**
	 * 获取指定表信息
	 * 
	 * @return
	 */
	@Deprecated
	public static List<FieldsInfo> getTableInfo(String tableName) {
		List<FieldsInfo> fieldsInfos = new LinkedList<FieldsInfo>();
		List<String> tableNames = getAllTablesNames();

		if (!tableNames.contains(tableName)) {
			System.out.println("表名不存在！");
			return fieldsInfos;
		}
		
		try {
			DatabaseMetaData data = connection.getMetaData();
			ResultSet resultSet = data.getColumns(null, "%", tableName, "%");
			while (resultSet.next()) {
				FieldsInfo persistent = new FieldsInfo();
				persistent.setName(resultSet.getString("COLUMN_NAME"));
				persistent.setType(resultSet.getString("TYPE_NAME"));
				persistent.setDesc(resultSet.getString("REMARKS"));
				// 默认第一项为主键
				if (fieldsInfos.size() == 0) {
					persistent.setPrimary(Boolean.TRUE);
				}
				fieldsInfos.add(persistent);
			}
		} catch (Exception e) {
			System.out.println(String.format("解析%s发生错误：", tableName) + e.getMessage());
			e.printStackTrace();
		}

		return fieldsInfos;
	}


}
