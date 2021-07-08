package User;

import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import Files.*;

public class User {


    private String currentDir = "";
    private ListView<FileInfo> listCurrentDirUser;
    private TextField textCurrentDirUser;
    private ChoiceBox<String> root;

    public User (ChoiceBox<String> root, ListView<FileInfo> listCurrentDirUser, TextField textCurrentDirUser){
        this.listCurrentDirUser = listCurrentDirUser;
        this.textCurrentDirUser = textCurrentDirUser;
        this.root = root;
        root.getItems().addAll(getRootPaths());
        root.setValue(getStartRoot());
    }

//    Открытие предыдущей директории пользователя
    public void stepBack () {
        String[] strings = currentDir.split("/");
        currentDir = "";

        if (strings.length > 2) {
            for (int i = 1; i < strings.length - 1; i++) {
                currentDir += "/" + strings[i];
            }
        }
    }

    //    Открытие новой директории или файла
    public void openDirOrFile(String nameFile){
        File file = new File(getCurPath() + File.separator + nameFile);
        if (file.exists()){
            if (file.isDirectory()){
                currentDir += "/" + nameFile;
            } else {
                System.out.println("method open file: " + file.getAbsolutePath());
            }
        }
        updateListFiles();
    }

    public String currentDirInfo () {
        return currentDir;
    }

    public void stepHome(){
        currentDir = "";
    }

    public String getCurPath () {
        return root.getValue() + File.separator + currentDir;
    }

//    Устатовить текущю директорию пользователя
    public void setCurDir (String dir) {

        if (dir.length() > 0){
            if (!dir.startsWith("/")){
                dir = "/" + dir;
            }
            File file = new File(root + dir);
            if (file.isDirectory()){
                currentDir = dir;
            } else {
                System.out.println("method open file: " + file.getAbsolutePath());
            }
        } else {
            currentDir = "";
        }

    }

    //    Создание нового файла
    public void createFile(String fileName) {

        Path newFile = Paths.get(getCurPath() + File.separator + fileName);

        try {
            Files.createFile(newFile);
        } catch (Exception e){
            e.printStackTrace();
        }

        updateListFiles();
    }

    //  Создание новой директории
    public void createDirectory (String path) {

        Path newDir = Paths.get(getCurPath() + File.separator + path);
        try {
            Files.createDirectories(newDir);
        } catch (Exception e){
            e.printStackTrace();
        }

        updateListFiles();
    }

    //   Удаление файла или директории
    public void delete(String path) {
        Path pathDel = Paths.get(path);
        try {
            Files.delete(pathDel);
        } catch (Exception io) {
            io.printStackTrace();
        }

        updateListFiles();
    }

    //   Переименовать файл или директорию
    public void renameFile (String nameSrcFile, String nameDestFile) {

        File srcFile = new File(getCurPath() +  File.separator + nameSrcFile);
        File destFile = new File(getCurPath() +  File.separator + nameDestFile);

        srcFile.renameTo(destFile);
        updateListFiles();
    }

    public void updateListFiles(){

        File file = new File(getCurPath());

        Platform.runLater(()->{

            listCurrentDirUser.getItems().clear();
            for (File f: file.listFiles()
            ) {
                listCurrentDirUser.getItems().add(new FileInfo(f));
            }
            textCurrentDirUser.setText(currentDirInfo());
        });


    }

    private String getStartRoot () {
        return getRootPaths()[0];
    }

    private String[] getRootPaths () {
        File[] roots = File.listRoots();
        String[] rootsStr = new String[roots.length];
        for (int i = 0; i < roots.length; i++) {
            rootsStr[i] = roots[i].toString().replace("\\", "");

        }
        return rootsStr;
    }

    public void copyFile(String path) {

        Path src = Paths.get(path);

        Path target = Paths.get(getCurPath() + File.separator + src.getFileName());

        try {
            Files.copy(src, target);
        } catch (
                IOException e) {
        }

        updateListFiles();
    }
}
