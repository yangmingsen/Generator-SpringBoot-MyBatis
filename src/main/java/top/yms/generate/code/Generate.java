package top.yms.generate.code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import top.yms.generate.config.GlobalConfig;
import top.yms.generate.entity.FieldsInfo;
import top.yms.generate.iterface.impl.DBConnection;
import top.yms.generate.utils.DateHelper;
import top.yms.generate.utils.FormatUtils;

public class Generate {

	private static final String RN = "\r\n";
	private static final String SPACE = "	";

	private static String outPath = "";
	private static String author = "";
	private static boolean entityGet = false;
	private static boolean entitySet = false;
	private static boolean entityConstructor = false;

	private static boolean controller = false;
	private static boolean service = false;
	private static boolean mapper = false;
	private static boolean entity = false;
	private static boolean angularJS = false;

	private static int outputTo = -1;

	static {

		outPath = GlobalConfig.getValue("outPath");
		author = GlobalConfig.getValue("author");

		entityGet = new Boolean(GlobalConfig.getValue("entityGet")).booleanValue();
		entitySet = new Boolean(GlobalConfig.getValue("entitySet")).booleanValue();
		entityConstructor = new Boolean(GlobalConfig.getValue("entityConstructor")).booleanValue();

		controller = new Boolean(GlobalConfig.getValue("controller")).booleanValue();
		service = new Boolean(GlobalConfig.getValue("service")).booleanValue();
		mapper = new Boolean(GlobalConfig.getValue("mapper")).booleanValue();
		entity = new Boolean(GlobalConfig.getValue("entity")).booleanValue();
		angularJS = new Boolean(GlobalConfig.getValue("angularJS")).booleanValue();

		outputTo = new Integer(GlobalConfig.getValue("outputTo")).intValue();

	}

	private static void writeToFile(String tableName, StringBuilder sb, String dirName) {
		System.out.println(String.format("\n开始导出%s.java文件：======", FormatUtils.getTableNameToJavaClassName(tableName)));

		try {
			File file = new File(outPath + "/" + dirName);
			if (!file.exists()) {
				file.mkdirs();
			}
			OutputStream out = new FileOutputStream(
					outPath + "/" + dirName + "/" + FormatUtils.getTableNameToJavaClassName(tableName) + ".java");
			PrintStream p = new PrintStream(out);
			p.print(sb.toString());
			p.close();
			out.close();
			System.out
					.println(String.format(" %s文件导出成功：====== \n", FormatUtils.getTableNameToJavaClassName(tableName)));

		} catch (Exception e) {
			System.out.println("文件导出异常：" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 生成entity代码入口
	 * 
	 * @createTime 2020-02-03 12:18
	 */
	private static void entityFile(String tableName, List<FieldsInfo> listFields) {
		String entityName = FormatUtils.getTableNameToJavaClassName(tableName);
		StringBuilder sb = new StringBuilder(RN);

		// 包名

		// 说明介绍部分
		sb.append("/***").append(RN).append("*描述: ").append(RN).append("* ").append(RN).append("* @author ")
				.append(author).append(RN).append("* @createTime ").append(DateHelper.getYYYY_MM_DD_HH_MM_SS())
				.append(RN).append("*/").append(RN);

		// start
		sb.append("public class ").append(entityName);
		sb.append(" {").append(RN);

		// 插入属性名称
		for (FieldsInfo info : listFields) {
			sb.append(RN).append(SPACE).append("// ").append(info.getDesc()).append(RN).append(SPACE).append("private ")
					.append(FormatUtils.sqlTypeToJava(info.getType())).append(" ")
					.append(FormatUtils.getFieldsNameToJavaAttrName(info.getName())).append(";").append(RN);
		}

		// 插入构造器
		if (entityConstructor) {
			sb.append(RN).append(SPACE).append("public ").append(entityName).append("(");
			for (int i = 0; i < listFields.size(); i++) {
				FieldsInfo info = listFields.get(i);

				if (i + 1 == listFields.size()) {
					sb.append(FormatUtils.sqlTypeToJava(info.getType())).append(" ")
							.append(FormatUtils.getFieldsNameToJavaAttrName(info.getName())).append(") {").append(RN);
				} else {
					sb.append(FormatUtils.sqlTypeToJava(info.getType())).append(" ")
							.append(FormatUtils.getFieldsNameToJavaAttrName(info.getName())).append(", ");
				}
			}

			for (FieldsInfo info : listFields) {
				sb.append(SPACE).append(SPACE).append("this.")
						.append(FormatUtils.getFieldsNameToJavaAttrName(info.getName())).append(" = ")
						.append(FormatUtils.getFieldsNameToJavaAttrName(info.getName())).append(";").append(RN);
			}
			sb.append(SPACE).append("}").append(RN);
		}

		// 插入getter setter方法
		for (FieldsInfo info : listFields) {
			// getter
			if (entityGet) {
				sb.append(RN).append(SPACE).append("public ").append(FormatUtils.sqlTypeToJava(info.getType()))
						.append(" ").append("get").append(FormatUtils.getJavaStandardName(1, info.getName(), "_"))
						.append("() {").append(RN).append(SPACE).append(SPACE).append("return this.")
						.append(FormatUtils.getFieldsNameToJavaAttrName(info.getName())).append(";").append(RN)
						.append(SPACE).append("}").append(RN).append(SPACE);
			}

			// setter
			if (entitySet) {
				sb.append(RN).append(SPACE).append("public void set")
						.append(FormatUtils.getJavaStandardName(1, info.getName(), "_")).append("(")
						.append(FormatUtils.sqlTypeToJava(info.getType())).append(" ")
						.append(FormatUtils.getFieldsNameToJavaAttrName(info.getName())).append(") {").append(RN)
						.append(SPACE).append(SPACE).append("this.")
						.append(FormatUtils.getFieldsNameToJavaAttrName(info.getName())).append(" = ")
						.append(FormatUtils.getFieldsNameToJavaAttrName(info.getName())).append(";").append(RN)
						.append(SPACE).append("}").append(RN);
			}

		}
		sb.append("}").append(RN);

		outControl(entityName, sb, "entity");// write to file
	}

	/**
	 * 生成service代码入口
	 * 
	 * @createTime 2020-02-03 12:18
	 */
	private static void serviceFile(String tableName, List<FieldsInfo> listFields) {
		String serviceName = FormatUtils.getTableNameToJavaClassName(tableName);
		String attrMapperName = FormatUtils.getFieldsNameToJavaAttrName(tableName) + "Mapper";

		StringBuilder sb = new StringBuilder(RN);

		// 包名

		// 说明介绍部分
		sb.append("/***").append(RN).append("*描述: ").append(RN).append("* ").append(RN).append("* @author ")
				.append(author).append(RN).append("* @createTime ").append(DateHelper.getYYYY_MM_DD_HH_MM_SS())
				.append(RN).append("*/").append(RN);

		// 主体程序
		sb.append("@Service").append(RN).append("public class ").append(serviceName).append("Service {").append(RN)
				.append(RN);

		// 主体程序 注入部分
		sb.append(SPACE).append("@Autowired").append(RN);
		sb.append(SPACE).append("private ").append(serviceName).append("Mapper ").append(attrMapperName).append(";")
				.append(RN).append(RN);

		// 主体程序 add method
		sb.append(SPACE).append("public int add(").append(serviceName).append(" entity) {").append(RN);
		sb.append(SPACE).append(SPACE).append("return ").append(attrMapperName).append(".add(");
		for (int i = 0; i < listFields.size(); i++) {
			if (i + 1 == listFields.size()) {
				sb.append("entity.get").append(FormatUtils.getJavaStandardName(1, listFields.get(i).getName(), "_"))
						.append("()");
			} else {
				sb.append("entity.get").append(FormatUtils.getJavaStandardName(1, listFields.get(i).getName(), "_"))
						.append("(),");
			}
		}
		sb.append(");").append(RN);
		sb.append(SPACE).append("}").append(RN).append(RN);

		// 主体程序 update method
		sb.append(SPACE).append(" public int update(").append(serviceName).append(" entity) {").append(RN);
		sb.append(SPACE).append(SPACE).append("return ").append(attrMapperName).append(".update(");
		for (int i = 0; i < listFields.size(); i++) {
			if (i + 1 == listFields.size()) {
				sb.append("entity.get").append(FormatUtils.getJavaStandardName(1, listFields.get(i).getName(), "_"))
						.append("()");
			} else {
				sb.append("entity.get").append(FormatUtils.getJavaStandardName(1, listFields.get(i).getName(), "_"))
						.append("(),");
			}
		}
		sb.append(");").append(RN);
		sb.append(SPACE).append("}").append(RN).append(RN);

		// 主体程序 findOne method
		FieldsInfo fis = null;
		for (FieldsInfo fi : listFields) {// 找到主键
			if (fi.isPrimary()) {
				fis = fi;
				break;
			}
		}
		String idString = FormatUtils.getFieldsNameToJavaAttrName(fis.getName());// 主键名
		sb.append(SPACE).append(" public ").append(serviceName).append(" findOne(")
				.append(FormatUtils.sqlTypeToJava(fis.getType())).append(" ").append(idString).append(") {").append(RN);
		sb.append(SPACE).append(SPACE).append("return ").append(attrMapperName).append(".findOne(").append(idString)
				.append(");").append(RN);
		sb.append(SPACE).append("}").append(RN).append(RN);

		// 主体程序 findAll method
		sb.append(SPACE).append(" public List<").append(serviceName).append("> findAll() {").append(RN);
		sb.append(SPACE).append(SPACE).append("return ").append(attrMapperName).append(".findAll();").append(RN);
		sb.append(SPACE).append("}").append(RN).append(RN);

		sb.append("}").append(RN);// 结尾

		outControl(serviceName + "Service", sb, "service");// write to file
	}

	/**
	 * 生成mapper代码入口
	 * 
	 * @createTime 2020-02-03 12:18
	 */
	private static void mapperFile(String tableName, List<FieldsInfo> listFields) {
		String mapperName = FormatUtils.getTableNameToJavaClassName(tableName);
		StringBuilder sb = new StringBuilder(RN);

		// 包名

		// 说明介绍部分
		sb.append("/***").append(RN).append("*描述: ").append(RN).append("* ").append(RN).append("* @author ")
				.append(author).append(RN).append("* @createTime ").append(DateHelper.getYYYY_MM_DD_HH_MM_SS())
				.append(RN).append("*/").append(RN);

		// 主体程序
		sb.append("@Mapper").append(RN).append("public interface ").append(mapperName).append("Mapper {").append(RN)
				.append(RN);

		/***
		 * 主体程序 add interface
		 */
		// 注解部分
		sb.append(SPACE).append("@Insert(\"INSERT INTO ").append(tableName).append(" (");
		for (int i = 0; i < listFields.size(); i++) {
			if (i + 1 == listFields.size()) {
				sb.append(listFields.get(i).getName() + ")");
			} else {
				sb.append(listFields.get(i).getName() + ",");
			}
		}
		sb.append("VALUES(");
		for (int i = 0; i < listFields.size(); i++) {
			if (i + 1 == listFields.size()) {
				sb.append("#{").append(listFields.get(i).getName()).append("})\")").append(RN);
			} else {
				sb.append("#{").append(listFields.get(i).getName()).append("},");
			}
		}
		// interface 部分
		sb.append(SPACE).append("public int add(");
		for (int i = 0; i < listFields.size(); i++) {
			if (i + 1 == listFields.size()) {
				sb.append("@Param(\"").append(listFields.get(i).getName()).append("\") ")
						.append(FormatUtils.sqlTypeToJava(listFields.get(i).getType())).append(" ")
						.append(listFields.get(i).getName() + ");").append(RN).append(RN);
			} else {
				sb.append("@Param(\"").append(listFields.get(i).getName()).append("\") ")
						.append(FormatUtils.sqlTypeToJava(listFields.get(i).getType())).append(" ")
						.append(listFields.get(i).getName() + ", ");
			}
		}

		/***
		 * 主体程序 update interface
		 */
		// 注解部分
		// 找到 where id
		FieldsInfo fi = null;
		for (FieldsInfo fi1 : listFields) {
			if (fi1.isPrimary()) {
				fi = fi1;
				break;
			}
		}
		sb.append(SPACE).append("@Update(\"UPDATE ").append(tableName).append(" SET ");
		for (int i = 0; i < listFields.size(); i++) {
			if (listFields.get(i).isPrimary()) { // 如果是主键 跳过
				continue;
			}

			if (i + 1 == listFields.size()) {
				sb.append(listFields.get(i).getName()).append("=#{").append(listFields.get(i).getName()).append("}")
						.append(" WHERE ").append(fi.getName()).append("=#{").append(fi.getName()).append("}\")")
						.append(RN);
			} else {
				sb.append(listFields.get(i).getName()).append("=#{").append(listFields.get(i).getName()).append("},");
			}
		}

		// interface 部分
		sb.append(SPACE).append("public int update(");
		for (int i = 0; i < listFields.size(); i++) {
			if (i + 1 == listFields.size()) {
				sb.append("@Param(\"").append(listFields.get(i).getName()).append("\") ")
						.append(FormatUtils.sqlTypeToJava(listFields.get(i).getType())).append(" ")
						.append(listFields.get(i).getName() + ");").append(RN).append(RN);
			} else {
				sb.append("@Param(\"").append(listFields.get(i).getName()).append("\") ")
						.append(FormatUtils.sqlTypeToJava(listFields.get(i).getType())).append(" ")
						.append(listFields.get(i).getName() + ", ");
			}
		}

		/***
		 * 主体程序 findOne interface
		 */
		// 注解部分
		sb.append(SPACE).append("@Select(\"SELECT ");
		for (int i = 0; i < listFields.size(); i++) {
			if (i + 1 == listFields.size()) {
				// sb.append(listFields.get(i).getName());

				sb.append(listFields.get(i).getName()).append(" AS ")
						.append(FormatUtils.getFieldsNameToJavaAttrName(listFields.get(i).getName()));

			} else {
				// sb.append(listFields.get(i).getName() + ",");
				sb.append(listFields.get(i).getName()).append(" AS ")
						.append(FormatUtils.getFieldsNameToJavaAttrName(listFields.get(i).getName())).append(", ");
			}
		}
		sb.append(" FROM ").append(tableName);
		sb.append(" WHERE ").append(fi.getName()).append("=#{").append(fi.getName()).append("}\")").append(RN);
		// interface 部分
		sb.append(SPACE).append("public ").append(mapperName).append(" findOne(@Param(\"").append(fi.getName())
				.append("\") ").append(FormatUtils.sqlTypeToJava(fi.getType())).append(" ").append(fi.getName())
				.append(");").append(RN).append(RN);

		/***
		 * 主体程序 findAll interface
		 */
		// 注解部分
		sb.append(SPACE).append("@Select(\"SELECT ");
		for (int i = 0; i < listFields.size(); i++) {
			if (i + 1 == listFields.size()) {
				sb.append(listFields.get(i).getName()).append(" AS ")
						.append(FormatUtils.getFieldsNameToJavaAttrName(listFields.get(i).getName()));

			} else {
				sb.append(listFields.get(i).getName()).append(" AS ")
						.append(FormatUtils.getFieldsNameToJavaAttrName(listFields.get(i).getName())).append(", ");
			}
		}
		sb.append(" FROM ").append(tableName).append("\")").append(RN);
		// interface 部分
		sb.append(SPACE).append("public List<").append(mapperName).append("> findAll();").append(RN).append(RN);

		sb.append("}").append(RN);// 结尾

		outControl(mapperName + "Mapper", sb, "mapper");// write to file
	}

	/***
	 * 生成controller代码入口
	 * 
	 * @createTime 2020-02-04 13:16
	 * @param tableName
	 * @param listFields
	 */
	private static void controllerFile(String tableName, List<FieldsInfo> listFields) {
		String controllerName = FormatUtils.getTableNameToJavaClassName(tableName);
		String entityAttrName = FormatUtils.getFieldsNameToJavaAttrName(tableName);
		String autowiredAttrName = entityAttrName+"Service";
		StringBuilder sb = new StringBuilder(RN);

		// 包名

		// 说明介绍部分
		sb.append(writeClassAnnotation());
		
		/***
		 *主体程序 
		 * 
		 */ 
		sb.append("@RestController").append(RN).append("@RequestMapping(\"/").append(FormatUtils.getRequestMappingName(tableName, "_"))
			.append("\")").append(RN).append("public class ").append(controllerName).append("Controller {").append(RN)
				.append(RN);
		
		
		/***
		 *  注入部分
		 */
		sb.append(SPACE).append("@Autowired").append(RN);
		sb.append(SPACE).append("private ").append(controllerName).append("Service ").append(autowiredAttrName).append(";")
		.append(RN).append(RN);
		
		/***
		 *  add 部分
		 */
		//注释部分
		sb.append(writeMethodAnnotation());
		//add mehtod 部分
		sb.append(SPACE).append("@RequestMapping(\"/add\")").append(RN);
		sb.append(SPACE).append("public Result add(@RequestBody ").append(controllerName).append(" ").append(entityAttrName).append(") {").append(RN); 
		sb.append(SPACE).append(SPACE).append("try {").append(RN);
		sb.append(SPACE).append(SPACE).append(SPACE).append(autowiredAttrName).append(".add(").append(entityAttrName).append(");").append(RN);
		sb.append(SPACE).append(SPACE).append(SPACE).append("return new Result(true, \"增加成功\");").append(RN);
		sb.append(SPACE).append(SPACE).append("} catch (Exception e) {").append(RN);
		sb.append(SPACE).append(SPACE).append(SPACE).append("e.printStackTrace();").append(RN);
		sb.append(SPACE).append(SPACE).append(SPACE).append("return new Result(false, \"增加失败\");").append(RN);
		sb.append(SPACE).append(SPACE).append("}").append(RN);
		sb.append(SPACE).append("}").append(RN).append(RN);
		
		
		/***
		 *  update 部分
		 */
		//注释部分
		sb.append(writeMethodAnnotation());
		//update mehtod 部分
		sb.append(SPACE).append("@RequestMapping(\"/update\")").append(RN);
		sb.append(SPACE).append("public Result update(@RequestBody ").append(controllerName).append(" ").append(entityAttrName).append(") {").append(RN); 
		sb.append(SPACE).append(SPACE).append("try {").append(RN);
		sb.append(SPACE).append(SPACE).append(SPACE).append(autowiredAttrName).append(".update(").append(entityAttrName).append(");").append(RN);
		sb.append(SPACE).append(SPACE).append(SPACE).append("return new Result(true, \"更新成功\");").append(RN);
		sb.append(SPACE).append(SPACE).append("} catch (Exception e) {").append(RN);
		sb.append(SPACE).append(SPACE).append(SPACE).append("e.printStackTrace();").append(RN);
		sb.append(SPACE).append(SPACE).append(SPACE).append("return new Result(false, \"更新失败\");").append(RN);
		sb.append(SPACE).append(SPACE).append("}").append(RN);
		sb.append(SPACE).append("}").append(RN).append(RN);
		
		
		/***
		 *  findOne 部分
		 */
		//注释部分
		sb.append(writeMethodAnnotation());
		
		FieldsInfo fis = null;
		for (FieldsInfo fi : listFields) {// 找到主键
			if (fi.isPrimary()) {
				fis = fi;
				break;
			}
		}
		//findOne mehtod 部分
		sb.append(SPACE).append("@RequestMapping(\"/findOne\")").append(RN);
		sb.append(SPACE).append("public ").append(controllerName).append(" findOne(")
			.append(FormatUtils.sqlTypeToJava(fis.getType())).append(" ")
			.append(FormatUtils.getFieldsNameToJavaAttrName(fis.getName())).append(") {").append(RN); 
		sb.append(SPACE).append(SPACE).append("return ").append(autowiredAttrName).append(".findOne(")
			.append(FormatUtils.getFieldsNameToJavaAttrName(fis.getName())).append(");").append(RN);
		sb.append(SPACE).append("}").append(RN).append(RN);
		
		
		
		/***
		 *  findAll 部分
		 */
		//注释部分
		sb.append(writeMethodAnnotation());
		//findAll mehtod 部分
		sb.append(SPACE).append("@RequestMapping(\"/findAll\")").append(RN);
		sb.append(SPACE).append("public List<").append(controllerName).append("> findAll() {").append(RN);
		sb.append(SPACE).append(SPACE).append("return ").append(autowiredAttrName).append(".findAll();").append(RN);
		sb.append(SPACE).append("}").append(RN).append(RN);
		

		// 结尾
		sb.append("}").append(RN);
		
		outControl(controllerName + "Controller", sb, "controller");// write to file
	}

	/***
	 * 方法注释部分
	 * @return
	 */
	private static String writeMethodAnnotation() {
		StringBuilder sb = new StringBuilder();

		sb.append(SPACE).append("/***").append(RN).append(SPACE).append(" *描述: ").append(RN).append(SPACE)
				.append(" * ").append(RN).append(SPACE).append(" * @author ").append(author).append(RN).append(SPACE)
				.append(" * @createTime ").append(DateHelper.getYYYY_MM_DD_HH_MM()).append(RN).append(SPACE)
				.append(" */").append(RN);

		return sb.toString();
	}
	
	/***
	 * 类注释部分
	 * @return
	 */
	private static String writeClassAnnotation() {
		StringBuilder sb = new StringBuilder();

		sb.append("/***").append(RN)
			.append(" *描述: ").append(RN)
			.append(" * ").append(RN)
			.append(" * @author ").append(author).append(RN)
			.append(" * @createTime ").append(DateHelper.getYYYY_MM_DD_HH_MM()).append(RN)
			.append(" */").append(RN);

		return sb.toString();
	}

	private static void consoleString(StringBuilder sb) {
		System.out.println(sb.toString());
	}

	private static void outControl(String tableName, StringBuilder sb, String dirName) {

		switch (outputTo) {
		case 1:
			consoleString(sb);
			break;
		case 2:
			writeToFile(tableName, sb, dirName);// write to file
			break;
		default:
			break;
		}
	}

	public static void entityCode(String tableName, List<FieldsInfo> listFields) {
		entityFile(tableName, listFields);
	}

	public static void serviceCode(String tableName, List<FieldsInfo> listFields) {
		serviceFile(tableName, listFields);
	}

	public static void mapperCode(String tableName, List<FieldsInfo> listFields) {
		mapperFile(tableName, listFields);
	}
	
	
	public static void controllerCode(String tableName, List<FieldsInfo> listFields) {
		controllerFile(tableName, listFields);
	}
	

	@Deprecated
	public static void esmCode(String tableName, List<FieldsInfo> listFields) {
		entityFile(tableName, listFields);
		serviceFile(tableName, listFields);
		mapperFile(tableName, listFields);
	}

	/**
	 * 自动生成代码入口
	 * 
	 * @createTime 2020-02-04 12:18
	 * @param allTablesInfo 表信息
	 */
	public static void myCode(Map<String, List<FieldsInfo>> allTablesInfo) {
		for (String tableName : allTablesInfo.keySet()) {
			List<FieldsInfo> listFields = allTablesInfo.get(tableName);

			// entity Code
			if (entity) {
				entityCode(tableName, listFields);
			}

			// mapper Code
			if (mapper) {
				mapperCode(tableName, listFields);
			}

			// service Code
			if (service) {
				serviceCode(tableName, listFields);
			}

			// controller Code
			if (controller) {

			}
			// angularJS Code
			if (angularJS) {

			}
		}
	}

}
