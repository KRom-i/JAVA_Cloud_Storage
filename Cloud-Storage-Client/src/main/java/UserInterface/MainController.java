package UserInterface;

import Cloud.Cloud;
import Connector.Connector;
import Files.*;
import User.User;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import java.io.*;
import javafx.scene.control.*;


public class MainController {

    public TextField textCurrentDirServer;
    public ListView<FileInfo> listCurrentDirServer;

    public TextField textCurrentDirUser;
    public ListView<FileInfo> listCurrentDirUser;
    public ChoiceBox<String> choiceRootUserDir;

    private User user;
    private Cloud cloud;

    public static Connector connector;
    public static FileBuf fileBuf;

    public void initialize () {

        cloud = new Cloud(textCurrentDirServer, listCurrentDirServer);

        user = new User(choiceRootUserDir, listCurrentDirUser, textCurrentDirUser);
        user.updateListFiles();

        connector = new Connector(cloud, user);
        connector.start();

    }

    public void selectionCurrentDirServer (ActionEvent actionEvent)  {

        String cmd = textCurrentDirServer.getText();
        if (cmd.length() > 0){
            connector.sendMsg("//cd:" + cmd);
        } else {
            connector.sendMsg("/cd:~");
        }

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
                File file = null;

                try {
                    file = listCurrentDirServer.getSelectionModel().getSelectedItem().getFile();
                } catch (NullPointerException e){

                }

                if (file != null){
                    connector.sendMsg("/cd:" + file.getName());
                }

            }
        } else {
            FileInfo fileInfo = null;
            try {
                fileInfo = listCurrentDirServer.getSelectionModel().getSelectedItem();
            } catch (NullPointerException e){

            }
            new MenuServerFiles(connector, fileInfo, listCurrentDirServer, user);
        }
    }

    public void selectionCurrentDirUser (ActionEvent event) {
        user.setCurDir(textCurrentDirUser.getText());
        updateListFilesUser();
    }

    public void searchUserFile (ActionEvent actionEvent) {

    }

    public void openUserDirOrFile (MouseEvent mouseEvent) {

        if (!mouseEvent.getButton().toString().equals("SECONDARY")){

            if (mouseEvent.getClickCount() == 2){
                File file = null;

                try {
                    file = listCurrentDirUser.getSelectionModel().getSelectedItem().getFile();
                } catch (NullPointerException e){

                }

                if (file != null){
                    user.openDirOrFile(file.getName());
                }
            }

        } else {
            FileInfo fileInfo = null;
            try {
                fileInfo = listCurrentDirUser.getSelectionModel().getSelectedItem();
            } catch (NullPointerException e){

            }
            new MenuUserFiles(connector, fileInfo, listCurrentDirUser, user);
        }
    }

    public void getRootUserDir (ActionEvent event) {
        user.stepHome();
        updateListFilesUser();
    }

    public void getUserDirStepBack (ActionEvent event) {
        user.stepBack();
        updateListFilesUser();
    }

    private void updateListFilesUser(){
        user.updateListFiles();
    };

}
