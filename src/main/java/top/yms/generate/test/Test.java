package top.yms.generate.test;

import java.util.List;

import top.yms.generate.App;
import top.yms.generate.code.Generate;
import top.yms.generate.entity.FieldsInfo;
import top.yms.generate.utils.TableUtils;

public class Test {
	public static void main(String[] args) {

		// App.start();
		singleTableTest();
	}

	public static void singleTableTest() {
		String tableName = "as_user";

		List<FieldsInfo> listFields = TableUtils.getTableInfo(tableName);

		Generate.controllerCode(tableName, listFields);
	}

}
