import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import java.io.*;

import javafx.scene.control.*;



public class MainController {

    public TextField textCurrentDirServer;
    public ListView<FileInfo> listCurrentDirServer;
    public TextField textCurrentDirUser;
    public ListView<FileInfo> listCurrentDirUser;


    public static File userDir = new File("C:\\Users\\Роман\\Documents\\TestDown\\");
    public Connector connector;

    public void initialize () {
        updateListFilesUser(userDir);
        connector = new Connector(textCurrentDirServer, listCurrentDirServer);
        connector.start();
    }


    public void selectionCurrentDirServer (ActionEvent actionEvent)  {

    }

    public void searchServerFile (ActionEvent actionEvent) {
    }

    public void getRootServerDir (ActionEvent actionEvent) {
        connector.sendMsg("/cd:~");
    }

    public void getServerDirStepBack (ActionEvent actionEvent) {
        connector.sendMsg("/cd:..");
    }

    public void openServerDirOrFile (MouseEvent mouseEvent) {
        if (!mouseEvent.getButton().toString().equals("SECONDARY")){
            if (mouseEvent.getClickCount() == 2){
                File file = listCurrentDirServer.getSelectionModel().getSelectedItem().getFile();
                connector.sendMsg("/cd:" + file.getName());
            }
        } else {
            new MenuServerFiles(connector, listCurrentDirServer.getSelectionModel().getSelectedItem().getFile().getName(), listCurrentDirServer);
        }
    }




    public void selectionCurrentDirUser (ActionEvent event) {
        updateListFilesUser(new File(textCurrentDirUser.getText()));
    }

    public void searchUserFile (ActionEvent actionEvent) {

    }

    public void openUserDirOrFile (MouseEvent mouseEvent) {

        File file = listCurrentDirUser.getSelectionModel().getSelectedItem().getFile();

        if (!mouseEvent.getButton().toString().equals("SECONDARY")){

            if (mouseEvent.getClickCount() == 2){
                if (!file.isDirectory()){
                    connector.sendFile(file);
                } else {
                    updateListFilesUser(file);
                }
            }

        } else {
            new MenuUserFiles(connector, file, listCurrentDirServer);
        }
    }

    private void updateListFilesUser(File file){

        if (file.exists() && file.isDirectory()){
            File finalFile = file;
            Platform.runLater(()->{
                userDir = finalFile;
                setTextCurrentDirUser();
                listCurrentDirUser.getItems().clear();
                for (File f: finalFile.listFiles()
                ) {
                    listCurrentDirUser.getItems().add(new FileInfo(f));
                }

            });

        } else {
            setTextCurrentDirUser();
        }
    }

    private void setTextCurrentDirUser(){
        textCurrentDirUser.clear();
        textCurrentDirUser.setText(userDir + File.separator);
    }


}
