package Tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author yxh
 */
public class FileTest {
    public static void main(String[] args) throws IOException {
        File file = new File("yxh.sql");

        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] bytes = "yxh".getBytes();
        outputStream.write(bytes);
        file.createNewFile();

    }
}
