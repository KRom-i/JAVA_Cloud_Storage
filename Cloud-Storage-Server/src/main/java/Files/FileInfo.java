package Files;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;

public class FileInfo {

    private String path;
    private String name;
    private String type;
    private long size;

    public FileInfo (File file, String path) {

        this.path = path;

        if (file.isDirectory()){
            this.name = file.getName();
            this.type = "DIR";
        } else {
            this.name = getFileNameNotExtension(file);
            this.type = getFileExtension(file);
        }

        this.size = file.length();

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

    @Override
    public String toString () {
        return String.format("FileInfo:%s:%s:%s:%s", path, name, type, size);
    }
}
