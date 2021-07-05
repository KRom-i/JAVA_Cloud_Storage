import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;

import java.awt.*;
import java.io.File;

public class MenuUserFiles {

    private ContextMenu contextMenu;

//    Меню выбора команд пользовательской части

    public MenuUserFiles (Connector connector, File file, ListView listView) {

        contextMenu = new ContextMenu();

        MenuItem item1 = new MenuItem("Загрузить");
        item1.setOnAction((event)->{
            connector.sendFile(file);
        });

        contextMenu.getItems().addAll(item1);

        double x = MouseInfo.getPointerInfo().getLocation().getX();
        double y = MouseInfo.getPointerInfo().getLocation().getY();

        listView.setContextMenu(contextMenu);
        contextMenu.show (listView, x,y);
    }
}
