package Client;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {

    private String name;
    private String currentDir;
    private Path rootClient;


    public Client (String name, String rootClient) {
        this.name = name;
        this.currentDir = "";
        this.rootClient = Paths.get(rootClient);
    }


    public String getName () {
        return name;
    }

    //    changenick (nickname) - изменение имени пользователя
    public void rename (String name) {
        this.name = name;
    }

    public String getCurrentDir () {
        return currentDir;
    }

    public void setCurrentDir (String currentDir) {
        this.currentDir = currentDir;
    }

    public Path getCurrentPath () {
        return Paths.get(rootClient + currentDir);
    }

    public Path getRootClient(){
        return rootClient;
    }
    public void stepBack () {
        String[] strings = currentDir.split("/");
        currentDir = "";

        if (strings.length > 2) {
            for (int i = 1; i < strings.length - 1; i++) {
                currentDir += "/" + strings[i];
            }
        }
    }

    public String currentDirInfo () {
        return "/currentDir:" + currentDir;
    }
}
