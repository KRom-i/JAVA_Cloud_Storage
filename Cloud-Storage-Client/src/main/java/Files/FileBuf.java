package Files;

import Connector.Connector;
import User.User;
import java.io.File;


public class FileBuf {

    private String path;
    private boolean server;
    private boolean delete;

/*   Класс необходим для копирования / перемещения файлом между:
      - директориями пользователя
      - директориями сервера
      - директориями пользователя / сервера
 */

    public FileBuf (String path, boolean server, boolean delete) {
        this.path = path;
        this.server = server;
        this.delete = delete;
    }


    public void copyUser(User user, Connector connector){

        if (!server){
            user.copyFile(path);
            if (delete){
                user.delete(path);
            }

        } else {

            if (!delete){
                connector.sendMsg("/download:" + path);
            } else {
                connector.sendMsg("/download:/rm!" + path);
            }
        }

    }

    public void copyServer(Connector connector, User user){

        if (!server){
            connector.sendFile(new File(path));
            if (delete){
                user.delete(path);
            }
        } else {
            connector.sendMsg("/copy:" + path);
            if (delete){
                connector.sendMsg("/rm:" + path);
            }
        }
    }

    public boolean isDelete () {
        return delete;
    }
}
