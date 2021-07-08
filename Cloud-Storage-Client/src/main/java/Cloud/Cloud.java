package Cloud;

import Files.FileInfo;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


public class Cloud {

    private TextField textCurrentDirServer;
    private ListView<FileInfo> listCurrentDirServer;

    public Cloud (TextField textCurrentDirServer, ListView<FileInfo> listCurrentDirServer) {
        this.textCurrentDirServer = textCurrentDirServer;
        this.listCurrentDirServer = listCurrentDirServer;
    }


    public void setCurrentDir (String dir) {

        String[] s = dir.split(":");
        if (s.length == 1){
            textCurrentDirServer.setText("");
        } else {
            textCurrentDirServer.setText(s[1]);
        }
    }

    public void updateList (FileInfo[] files) {
        Platform.runLater(() -> {
            listCurrentDirServer.getItems().clear();
            for (FileInfo f : files
            ) {
                listCurrentDirServer.getItems().addAll(f);
            }

        });
    }
}
