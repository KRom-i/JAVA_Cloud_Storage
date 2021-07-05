import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

public class FileInfo {

    private File file;
    private String name;
    private String type;
    private long size;

    public FileInfo (File file) {

        this.file = file;

        if (file.isDirectory()){
            this.name = file.getName();
            this.type = "DIR";
        } else {
            this.name = getFileNameNotExtension(file);
            this.type = getFileExtension(file);
        }

        this.size = file.length();

    }

    public FileInfo (String msg) {
        String[] info = msg.split(":");
        this.file = new File(info[1]);
        this.name = info[2];
        this.type = info[3];
        this.size = Long.parseLong(info[4]);
    }


    private String getFileNameNotExtension(File file){
        String fileName = file.getName();
        if (fileName.indexOf(".") > 0) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
    }


    private String getFileExtension(File file) {

        String fileName = file.getName();
          if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){
              return fileName.substring(fileName.lastIndexOf(".")+1);
          } else {
              return "";
          }

    }

    public File getFile () {
        return file;
    }



    @Override
    public String toString () {
        return String.format("FileInfo:%s:%s:%s:%s", file, name, type, size);
    }
}
