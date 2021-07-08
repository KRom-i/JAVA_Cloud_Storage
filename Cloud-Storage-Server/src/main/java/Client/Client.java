package Client;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {

    private String name;
    private String currentDir;
    private Path rootClient;
    private InetAddress inetAddress;
    private boolean auth;

    public Client (String name, String root, InetAddress inetAddress) {
        this.name = name;
        this.currentDir = "";
        this.rootClient = Paths.get(root + File.separator + "1");
        this.inetAddress = inetAddress;
        this.auth = true;
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
        return "/cd:" + currentDir;
    }

    public boolean isAuth () {
        return auth;
    }

    @Override
    public String toString () {
        return String.format("Name [%s] InetAddress [%s]", name, inetAddress);
    }
}
