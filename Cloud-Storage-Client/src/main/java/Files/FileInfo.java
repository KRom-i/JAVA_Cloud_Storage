package Files;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.io.File;


public class FileInfo extends HBox {

    private File file;
    private String name;
    private String type;
    private long size;

    private Label labelName;
    private TextField textFieldName;

    public FileInfo(boolean dir){

        if (dir){
            type = "Новая напка";
        } else {
            type = "Текстовый файл.txt";
        }

        textFieldName = new TextField(type);
        getChildren().add(textFieldName);
    }

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

        labelName = new Label(name);

        textFieldName = new TextField(name);
        textFieldName.setManaged(false);
        textFieldName.setVisible(false);

        Label labelType = new Label(type);

        Label labelSize = new Label(size + "");

        setSpacing(15);
        getChildren().addAll(labelName, textFieldName, labelType, labelSize);
    }

    public FileInfo (String msg) {
        String[] info = msg.split(":");
        this.file = new File(info[1]);
        this.name = info[2];
        this.type = info[3];
        this.size = Long.parseLong(info[4]);

        labelName = new Label(name);

        textFieldName = new TextField(name);
        textFieldName.setManaged(false);
        textFieldName.setVisible(false);

        Label labelType = new Label(type);

        Label labelSize = new Label(size + "");

        setSpacing(15);
        getChildren().addAll(labelName, textFieldName, labelType, labelSize);
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

    public String getFullName () {
        return file.getName();
    }

    public void renameFile(){
        labelName.setManaged(false);
        labelName.setVisible(false);
        textFieldName.setManaged(true);
        textFieldName.setVisible(true);
        textFieldName.setFocusTraversable(true);
    }

    public String getPath(){
        return file.getAbsolutePath();
    }

    public String getPathServer(){
        return file.toString();
    }

    public TextField getTextField(){
        return textFieldName;
    }

    @Override
    public String toString () {
        return String.format("FileInfo:%s:%s:%s:%s", file, name, type, size);
    }
}


