package UserInterface;

import Connector.Connector;
import Files.FileBuf;
import Files.FileInfo;
import User.User;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;

import java.awt.*;
import java.io.File;

public class MenuServerFiles {

    private ContextMenu contextMenu;

//    Меню выбора команд серверной части

    public MenuServerFiles (Connector connector, FileInfo fileInfo, ListView<FileInfo> listCurrentDirServer, User user) {

        contextMenu = new ContextMenu();


        if (fileInfo != null) {
            MenuItem item1 = new MenuItem("Открыть");
            item1.setOnAction((event) -> {
                connector.sendMsg("/cd:" + fileInfo.getFullName ());
            });
            contextMenu.getItems().add(item1);
        }
            MenuItem item2 = new MenuItem("Создать папку");
            item2.setOnAction((event)->{
                FileInfo newFileInfo = new FileInfo(true);
                newFileInfo.getTextField().setOnAction((a) ->{
                    connector.sendMsg("/mkdir:" + newFileInfo.getTextField().getText());
                });
                listCurrentDirServer.getItems().add(newFileInfo);
            });

            MenuItem item3 = new MenuItem("Создать файл");
            item3.setOnAction((event)->{
                FileInfo newFileInfo = new FileInfo(false);
                newFileInfo.getTextField().setOnAction((a) ->{
                    connector.sendMsg("/touch:" + newFileInfo.getTextField().getText());
                });
                listCurrentDirServer.getItems().add(newFileInfo);
            });
            contextMenu.getItems().addAll(item2, item3);



        if (fileInfo != null){

            MenuItem item4 = new MenuItem("Удалить");
            item4.setOnAction((event)->{
                connector.sendMsg("/rm:" + fileInfo.getPathServer());
            });

            MenuItem item5 = new MenuItem("Скачать");
            item5.setOnAction((event)->{
                connector.sendMsg("/download:" + fileInfo.getPathServer());
            });


            MenuItem item6 = new MenuItem("Переименовать");
            item6.setOnAction((event)->{
                fileInfo.renameFile();
                fileInfo.getTextField().setOnAction((a) ->{
                    connector.sendMsg("/rename:" + fileInfo.getFullName () + ":" + fileInfo.getTextField().getText());
                });
//
            });

            MenuItem item7 = new MenuItem("Копировать");
            item7.setOnAction((event)->{
                    MainController.fileBuf = new FileBuf(fileInfo.getPathServer(), true, false);
            });

            MenuItem item8 = new MenuItem("Вырезать");
            item8.setOnAction((event)->{
                MainController.fileBuf = new FileBuf(fileInfo.getPathServer(), true, true);
            });

            MenuItem item9 = new MenuItem("Вставить");
            item9.setOnAction((event)->{
                MainController.fileBuf.copyServer(connector, user);
                if (MainController.fileBuf.isDelete()){
                    MainController.fileBuf = null;
                }
            });
            if (MainController.fileBuf == null){
                item9.setVisible(false);
            }

            MenuItem item10 = new MenuItem("АРХИВ");
            item10.setOnAction((event)->{
//                connector.sendMsg("/rename:" + file.getName());
            });
            contextMenu.getItems().addAll(item4, item5, item6, item7, item8, item9, item10);
        }



        double x = MouseInfo.getPointerInfo().getLocation().getX();
        double y = MouseInfo.getPointerInfo().getLocation().getY();

        listCurrentDirServer.setContextMenu(contextMenu);
        contextMenu.show (listCurrentDirServer, x,y);
    }
}
