package Tool;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yxh
 */
public class ExcelToSql {
    public static void main(String[] args) throws Exception {
        long begin = System.currentTimeMillis();
        //读取excel文件
        Workbook book = new XSSFWorkbook("/Users/yxh/Desktop/用户表.xlsx");
        Sheet table = book.getSheet("Sheet1");
        //生成sql文件
        File file = new File("用户角色初始化脚本.sql");
        FileOutputStream outputStream = new FileOutputStream(file);
        file.createNewFile();
        //向sql文件中写数据
        int count = 0;
        for (int i = 1; i < table.getLastRowNum() + 1; i++) {
            //当前行数据
            Row row = table.getRow(i);
            //先删除
            String delete = String.format("delete from user where user_id ='%s';", row.getCell(0));
            //再添加
            String insert = String.format("insert into user (user_id,user_name,status) values ('%s','%s','1');", row.getCell(0), row.getCell(1));
            //写操作
            outputStream.write(delete.getBytes());
            outputStream.write("\r".getBytes());
            outputStream.write(insert.getBytes());
            outputStream.write("\r".getBytes());
            //处理多角色情况
            String[] role = row.getCell(3).getStringCellValue().split(",");
            List<String> list = new ArrayList<>();
            //中文角色转化为角色编码
            for (String s : role) {
                switch (s.trim()) {
                    case "催收管理岗":
                        list.add("001");
                        break;
                    case "总部管理岗":
                        list.add("002");
                        break;
                    case "催收组员":
                        list.add("003");
                        break;
                    case "催收组长":
                        list.add("004");
                        break;
                    default:
                        System.out.println("sql文件生成失败，存在角色名称未被转换：" + s + "（第" + (i + 1) + "行）");
                        file.delete();
                        return;
                }
            }
            for (String s : list) {
                String deleteRole = String.format("delete from user_role where user_id = '%s' and role_id = '%s';", row.getCell(0), s);
                String insertRole = String.format("insert into user_role (user_id,role_id) values ('%s','%s');", row.getCell(0), s);
                //写操作
                outputStream.write(deleteRole.getBytes());
                outputStream.write("\r".getBytes());
                outputStream.write(insertRole.getBytes());
                outputStream.write("\r".getBytes());
            }
            count++;
        }
        outputStream.close();
        long end = System.currentTimeMillis();
        System.out.printf("sql文件处理完成，总计处理%s条用户数据，共耗时%sms。", count, end - begin);
    }
}
