package top.yms.generate.utils;

public class FormatUtils {
	/**
	 * 表名转java类名
	 * 
	 * example: as_order  => AsOrder
	 * 			as_aus_db => AsAusDb
	 * 			as 		  => As
	 * 			""        => ""
	 * @param tableName
	 * @return
	 */
	public static String getTableNameToJavaClassName(String tableName) {
		return getJavaStandardName(1, tableName, "_");
	}
	
	/**
	 * 字段名转java属性名
	 * 
	 * example: as_order  => asOrder
	 * 			as_aus_db => asAusDb
	 * 			as 		  => as
	 * 			""        => ""
	 * @param tableName
	 * @return
	 */
	public static String getFieldsNameToJavaAttrName(String tableName) {
		return getJavaStandardName(2, tableName, "_");
	}
	
	/***
	 * 获取 RequestMappingName
	 * 
	 * example: as_order  => order
	 * 			as_aus_db => ausDb
	 * 			""        => ""
	 * 
	 * @param name
	 * @param regex
	 * @return
	 */
	public static String getRequestMappingName(String name,String regex) {
		boolean empty = isEmpty(name);
		if (empty) return "";
		
		StringBuilder result = new StringBuilder("");
		if (name.contains(regex)) {
			String[] tmp = name.split(regex);
			if (tmp.length == 0)
				return "";

			if (tmp != null && tmp.length > 1) {
				for (int i = 1; i < tmp.length; i++) {
					if (tmp[i].length() < 1) {
						continue;
					}
					result.append(tmp[i].substring(0, 1).toUpperCase() + tmp[i].substring(1));
				}
			}
			return result.substring(0,1).toLowerCase()+result.substring(1);
		} else {
			return name;
		}
		
		
	}
	

	/***
	 * 获取标准Java命名统一方法
	 * @param choose 1.获取类名;  2.获取方法名或属性名
	 * @param name 名字
	 * @param regex 分隔符
	 * 
	 * 注意：
	 *		不要传入null,"" 字符
	 * 
	 * @return
	 */
	public static String getJavaStandardName(int choose, String name, String regex) {
		if ( isEmpty(name) ) {
			return "";
		}
		
		switch (choose) {
		case 1:
			return getJavaStandardClassName(name, regex);

			
		case 2:
			return getJavaStandardMethodName(name, regex);

		default:
			return "";
		}
	}

	private static String getJavaStandardClassName(String name, String regex) {
		StringBuilder result = new StringBuilder("");
		if (name.contains(regex)) {
			String[] tmp = name.split(regex);
			if (tmp.length == 0)
				return "";

			if (tmp != null && tmp.length > 1) {
				for (int i = 1; i < tmp.length; i++) {
					if (tmp[i].length() < 1) {
						continue;
					}
					result.append(tmp[i].substring(0, 1).toUpperCase() + tmp[i].substring(1));
				}
			}
			return tmp[0].substring(0, 1).toUpperCase() + tmp[0].substring(1) + result.toString();
		} else {
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
	}

	private static String getJavaStandardMethodName(String name, String regex) {
		StringBuilder result = new StringBuilder("");

		if (name.contains(regex)) {
			String[] tmp = name.split(regex);
			if (tmp.length == 0)
				return "";

			if (tmp != null && tmp.length > 1) {
				for (int i = 1; i < tmp.length; i++) {
					if (tmp[i].length() < 1) {
						continue;
					}
					result.append(tmp[i].substring(0, 1).toUpperCase() + tmp[i].substring(1));
				}
			}
			return tmp[0] + result.toString();
		} else {
			return name;
		}
	}

	/**
	 * 给定字符串是否为null
	 * 
	 * @param str
	 * @return true 如果为null或""  otherwise 为false
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.equals("") || str.length() == 0) {
			return true;
		}
		return false;
	}


	public static String sqlTypeToJava(String type) {
		String result = "";
		
		if (type.indexOf(" ") > -1) {
			String[] split = type.split(" ");
			type = split[0];
		}
		
		switch (type.toLowerCase()) {
			case "int":
				result = "Integer";
				break;
			case "bigint":
				result = "Long";
				break;
			case "decimal":
				result = "Double";
				break;
			case "longtext":
				result = "String";
				break;
			case "varchar":
				result = "String";
				break;
			case "datetime":
				result = "String";
				break;
			case "tinyint":
				result = "Integer";
				break;
			case "bit":
				result = "Boolean";
				break;
			case "date":
				result = "Date";
				break;
			case "timestamp":
				result = "String";
				break;
			case "double":
				result = "Double";
				break;
			case "float":
				result = "Float";
				break;
			case "char":
				result = "Character";
				break;
			default:
				result = "Object";
				break;
		}

		return result;
	}
	
}
