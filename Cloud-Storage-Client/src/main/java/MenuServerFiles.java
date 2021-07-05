import javafx.scene.control.*;
import javafx.scene.control.MenuItem;

import java.awt.*;

public class MenuServerFiles {

    private ContextMenu contextMenu;

//    Меню выбора команд серверной части

    public MenuServerFiles (Connector connector, String fileName, ListView listView) {

        contextMenu = new ContextMenu();

        MenuItem item1 = new MenuItem("Открыть");
        item1.setOnAction((event)->{
            connector.sendMsg("/cd:" + fileName);
        });

        MenuItem item2 = new MenuItem("Создать папку");
        item2.setOnAction((event)->{
            connector.sendMsg("/mkdir:" + fileName + "newTestDir");
        });

        MenuItem item3 = new MenuItem("Создать файл");
        item3.setOnAction((event)->{
            connector.sendMsg("/touch:" + fileName + "newTestFile.txt");
        });

        MenuItem item4 = new MenuItem("Удалить файл");
        item4.setOnAction((event)->{
            connector.sendMsg("/rm:" + fileName);
        });

        MenuItem item5 = new MenuItem("Скачать");
        item5.setOnAction((event)->{
            connector.sendMsg("/download:" + fileName);
        });

        contextMenu.getItems().addAll(item1, item2, item3, item4, item5);

        double x = MouseInfo.getPointerInfo().getLocation().getX();
        double y = MouseInfo.getPointerInfo().getLocation().getY();

        listView.setContextMenu(contextMenu);
        contextMenu.show (listView, x,y);
    }
}
