import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

public class Connector {


    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private TextField textCurrentDirServer;
    private ListView<FileInfo> listCurrentDirServer;

    public Connector (TextField textCurrentDirServer, ListView<FileInfo> listCurrentDirServer) {

        this.textCurrentDirServer = textCurrentDirServer;
        this.listCurrentDirServer = listCurrentDirServer;

        try {
            socket = new Socket("localhost", 8000);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //    Обработка команд от сервера
    public void start(){

        new Thread(()->{


            sendMsg("/ls");

            while (true) {

                    String msg = getMessage();

                    if (msg.startsWith("/currentDir:")) {

                        String[] s = msg.split(":");
                        if (s.length > 1) {
                            textCurrentDirServer.setText(s[1]);
                        } else {
                            textCurrentDirServer.clear();
                        }

                    } else if (msg.startsWith("ls:")) {

                        int numberFile = Integer.valueOf(msg.split(":")[1]);
                        FileInfo[] files = new FileInfo[numberFile];
                        for (int i = 0; i < files.length; i++) {
                            String file = getMessage();
                            files[i] = new FileInfo(file);
                        }
                        Platform.runLater(() -> {
                            listCurrentDirServer.getItems().clear();
                            listCurrentDirServer.getItems().addAll(files);
                        });

                    } else if (msg.startsWith("/download")) {
                        getFile();
                    }

        }


        }).start();


    }

    //    Метод возвращает сообщения от сервера в формате UTF-8
    private String getMessage(){
        String massage = "";
        try {
            massage = in.readUTF();
            Log.info("IN MSG: " + massage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return massage;
    }

    // Отправка сообщения серверу в формате UTF-8
    public void sendMsg(String msg){
        String log = "Sent MSG to server: " + msg;
        try {
            out.writeUTF(msg);
            Log.info(log);
        } catch (IOException e) {
            Log.error(log, e);
        }
    }

    //    Передача файла с клиента на сервет
    public void sendFile(File file){

        try {

            if (!file.exists()){
                throw new FileNotFoundException();
            }

            long fileLength = file.length();
            FileInputStream fis = new FileInputStream(file);

            out.writeUTF("/upload");
            out.writeUTF(file.getName());
            out.writeLong(fileLength);

            int read = 0;
            byte[] buffer = new byte[8 * 1024];

            while ((read = fis.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }

            out.flush();

            String status = in.readUTF();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //    Загрузка файла с сервера на клиента
    private void getFile () {

        try {

            File file = new File(MainController.userDir + File.separator + in.readUTF());

            if (!file.exists()){
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);

            long size = in.readLong();

            byte[] buffer = new byte[8 * 1024];

            for(int i = 0; i < (size + (buffer.length - 1)) / (buffer.length); i++) {
                int read = in.read(buffer);
                fos.write(buffer, 0, read);
            }

            fos.close();


            String status;

            if (size == file.length()){
                status = ("File downloaded");
            } else {
                status = ("Error while loading file");
            }

            out.writeUTF(status);
            System.out.println(status);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
