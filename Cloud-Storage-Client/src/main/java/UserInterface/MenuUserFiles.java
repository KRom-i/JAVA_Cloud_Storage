package UserInterface;

import Connector.Connector;
import Files.FileBuf;
import Files.FileInfo;
import User.User;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;

import java.awt.*;

public class MenuUserFiles {

    private ContextMenu contextMenu;


//    Меню выбора команд пользовательской части

    public MenuUserFiles (Connector connector, FileInfo fileInfo, ListView listCurrentDirUser, User user) {

        contextMenu = new ContextMenu();

        if (fileInfo != null) {
            MenuItem item1 = new MenuItem("Открыть");
            item1.setOnAction((event) -> {
                user.openDirOrFile(fileInfo.getFullName ());
            });
            contextMenu.getItems().add(item1);
        }
        MenuItem item2 = new MenuItem("Создать папку");
        item2.setOnAction((event)->{
            FileInfo newFileInfo = new FileInfo(true);
            newFileInfo.getTextField().setOnAction((a) ->{
                user.createDirectory(newFileInfo.getTextField().getText());
            });
            listCurrentDirUser.getItems().add(newFileInfo);
        });

        MenuItem item3 = new MenuItem("Создать файл");
        item3.setOnAction((event)->{
            FileInfo newFileInfo = new FileInfo(false);
            newFileInfo.getTextField().setOnAction((a) ->{
                user.createFile(newFileInfo.getTextField().getText());
            });
            listCurrentDirUser.getItems().add(newFileInfo);
        });
        contextMenu.getItems().addAll(item2, item3);



        if (fileInfo != null){

            MenuItem item4 = new MenuItem("Удалить");
            item4.setOnAction((event)->{
                user.delete(fileInfo.getPath());
            });

            MenuItem item5 = new MenuItem("Загрузить на сервер");
            item5.setOnAction((event)->{
                connector.sendFile(fileInfo.getFile());
                });
//


            MenuItem item6 = new MenuItem("Переименовать");
            item6.setOnAction((event)->{
                fileInfo.renameFile();
                fileInfo.getTextField().setOnAction((a) ->{
                    user.renameFile(fileInfo.getFullName (), fileInfo.getTextField().getText());
                });
//
            });

            MenuItem item7 = new MenuItem("Копировать");
            item7.setOnAction((event)->{
                MainController.fileBuf = new FileBuf(fileInfo.getFile().getAbsolutePath(), false, false);
            });

            MenuItem item8 = new MenuItem("Вырезать");
            item8.setOnAction((event)->{
                MainController.fileBuf = new FileBuf(fileInfo.getFile().getAbsolutePath(), false, true);
            });


            MenuItem item10 = new MenuItem("АРХИВ");
            item10.setOnAction((event)->{

            });
            contextMenu.getItems().addAll(item4, item5, item6, item7, item8, item10);
        }

        MenuItem item9 = new MenuItem("Вставить");
        item9.setOnAction((event)->{
            MainController.fileBuf.copyUser(user, connector);
            if (MainController.fileBuf.isDelete()){
                MainController.fileBuf = null;
            }
        });
        if (MainController.fileBuf == null) {
            item9.setVisible(false);
        }

        contextMenu.getItems().add(item9);

        double x = MouseInfo.getPointerInfo().getLocation().getX();
        double y = MouseInfo.getPointerInfo().getLocation().getY();

        listCurrentDirUser.setContextMenu(contextMenu);
        contextMenu.show (listCurrentDirUser, x,y);
    }
}
