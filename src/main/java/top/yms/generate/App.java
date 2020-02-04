package top.yms.generate;

import java.util.List;
import java.util.Map;

import top.yms.generate.code.Generate;
import top.yms.generate.config.GlobalConfig;
import top.yms.generate.entity.FieldsInfo;
import top.yms.generate.utils.TableUtils;

/**
 * The Code of Generator
 *
 */
public class App {
	public static void main(String[] args) {
		start();
	}
	
	private static boolean allTables = false;
	private static String[] specificTables = null;
	
	static {
		allTables = new Boolean(GlobalConfig.getValue("allTables")).booleanValue();
		specificTables = GlobalConfig.getValue("specificTables").split(",");
	}
	
	/**
	 * 执行入口
	 */
	public static void start() {
		Map<String, List<FieldsInfo>> allTablesInfo = null;
		
		/***
		 * 获取表信息
		 */
		if (allTables) {
			allTablesInfo = TableUtils.getAllTablesInfo();
		} else {
			allTablesInfo = TableUtils.getSpecificTablesInfo(specificTables);
		}
		
		if (allTablesInfo == null) {
			System.out.println("Exception: allTablesInfo=null in App.start()");
			return ;
		}
		
		//start generate code
		Generate.myCode(allTablesInfo);
	}
	
}
