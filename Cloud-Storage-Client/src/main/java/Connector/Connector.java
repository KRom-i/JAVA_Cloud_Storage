package Connector;

import Cloud.Cloud;
import Files.FileInfo;
import Logger.Log;
import User.User;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Connector {


    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private User user;
    private Cloud cloud;

    public Connector (Cloud cloud, User user) {
        try {
            this.cloud = cloud;
            this.user = user;
            this.socket = new Socket("localhost", 8000);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //    Обработка команд от сервера
    public void start () {

        ExecutorService connect = Executors.newFixedThreadPool(1);

        connect.submit(() -> {

            try {

                sendMsg("/auth-nickname-pass");

                while (true) {

                    if (socket == null) break;

                    String msg = getMessage();

                    if (msg.startsWith("/cd:")) {

                        cloud.setCurrentDir(msg);

                    } else if (msg.startsWith("/ls:")) {

                        int numberFile = Integer.valueOf(msg.split(":")[1]);
                        FileInfo[] files = new FileInfo[numberFile];
                        for (int i = 0; i < files.length; i++) {
                            files[i] = new FileInfo(getMessage());
                        }

                        cloud.updateList(files);

                    } else if ("/download".equals(msg)) {
                        getFile();

                    } else if ("/exit".equals(msg)) {
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();

            }

        });

        connect.shutdown();
    }

    //    Метод возвращает сообщения от сервера в формате UTF-8
    private String getMessage () throws IOException {
        String massage = in.readUTF();
        Log.info("IN MSG: " + massage);
        return massage;
    }

    // Отправка сообщения серверу в формате UTF-8
    public void sendMsg (String msg) {
        String log = "Sent MSG to server: " + msg;
        try {
            out.writeUTF(msg);
            Log.info(log);
        } catch (IOException e) {
            Log.error(log, e);
        }
    }

    //    Передача файла с клиента на сервет
    public void sendFile (File file) {

        try {

            if (!file.exists()) {
                throw new FileNotFoundException();
            }

            long fileLength = file.length();
            FileInputStream fis = new FileInputStream(file);

            out.writeUTF("/upload");
            out.writeUTF(file.getName());
            out.writeLong(fileLength);

            int read = 0;
            byte[] buffer = new byte[8 * 1024];

            while ((read = fis.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            out.flush();

            fis.close();

//            System.out.println(getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Загрузка файла с сервера на клиента
    private void getFile () {

        try {

            File file = new File(user.getCurPath() + File.separator + in.readUTF());

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);

            long size = in.readLong();

            byte[] buffer = new byte[8 * 1024];

            for (int i = 0; i < (size + (buffer.length - 1)) / (buffer.length); i++) {
                int read = in.read(buffer);
                fos.write(buffer, 0, read);
            }

            fos.close();

            String status;

            if (size == file.length()) {
                status = ("File downloaded");
            } else {
                status = ("Error while loading file");
            }

            out.writeUTF(status);
            System.out.println(status);

            user.updateListFiles();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Закрытие потоков и канала
    private void close () {

        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
