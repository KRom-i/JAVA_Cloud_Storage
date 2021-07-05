package Server;

import Client.*;
import Files.*;
import Logger.Log;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private static final String ROOT = "ServerRootDir";

    // Временная заглушка т.к отсутствует БД и авторизация пользователя
    private Client client = new Client("USER", ROOT + File.separator + "1");;
    boolean auth = true;

    public ClientHandler (Socket socket) throws IOException {
        this.socket = socket;
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

    }

    //    Обработка команд от клиента
    @Override
    public void run () {

            Log.info("Client connected: " + socket.getInetAddress());

            while (true) {

                String[] command = getMessage().split(":");

                if (auth) {

                    if ("/download".equals(command[0])) {
                        sendFile(command[1]);
                    } else if ("/upload".equals(command[0])) {
                        getFile();
                    } else if ("/cd".equals(command[0])) {
                        changingCurrentDirectory(command[1]);
                    } else if ("/delete".equals(command[0])) {
                        delete(command[1]);
                    } else if ("/mkdir".equals(command[0])) {
                        createDirectory(command[1]);
                    } else if ("/touch".equals(command[0])) {
                        createFile(command[1]);
                    } else if ("/exit".equals(command[0])) {
                        break;
                    }

                    outListFiles();
                }

            }

            close();
    }


    // Отправка сообщения в цикле клиенту в формате UTF-8
    private void sendMessage(List<String> stringList) {
        for (String msg: stringList
        ) {
            sendMessage(msg);
        }
    }

    // Отправка сообщения клиенту в формате UTF-8
    private void sendMessage (String massage) {
        String log = "Sent MSG to client: " + massage;
        try {
            out.writeUTF(massage);
            Log.info(log);
        } catch (IOException e) {
            Log.error(log, e);
        }
    }

    //    Метод возвращает сообщения от клиента в формате UTF-8
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

    //  Отправка клиенту текущей директории и списка файлов данной директории
    private void outListFiles() {
        sendMessage(client.currentDirInfo());
        sendMessage(getFilesList());
    }

    //    Метод возвращает список файлов текущей директории
    private List<String> getFilesList () {

        File[] files = new File(client.getCurrentPath().toString()).listFiles();
        List<String> stringList = new ArrayList<>();

        int numberFile = files.length;

        if (numberFile > 0){
            stringList.add("ls:"+ numberFile);
            for (File f: files
            ) {
                stringList.add(new FileInfo(f).toString());
            }
        } else {
            stringList.add("ls:"+ numberFile);
        }

        return stringList;
    }

    //    Создание нового файла
    private void createFile(String fileName) {

        Path newFile = Paths.get(client.getCurrentPath() +
                               File.separator + fileName);

        try {
            Files.createFile(newFile);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    //  Создание новой директории
    private void createDirectory (String path) {

        Path newDir = Paths.get(client.getCurrentPath() + File.separator + path);
        try {
            Files.createDirectories(newDir);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //    Изменение текущей директории
    private void changingCurrentDirectory(String command){

        if ("~".equals(command)){
            client.setCurrentDir("");
        } else  if ("..".equals(command)){
            if (client.getCurrentDir().length() > 0){
                client.stepBack();
            }
        } else {
            client.setCurrentDir(client.getCurrentDir() + "/" + command);
        }

    }

    //   Удаление файла или директории
    private void delete(String command) {
        Path pathDel = Paths.get(client.getCurrentPath() +  File.separator + command);
        try {
            Files.delete(pathDel);
        } catch (Exception io) {
            io.printStackTrace();
        }
    }


    //    Передача файла с сервера на клиента
    private void sendFile(String command) {

        try {


        File file = new File(client.getCurrentPath() +  File.separator + command);

        if (file.exists()) {

            long fileLength = file.length();
            FileInputStream fis = new FileInputStream(file);

            out.writeUTF("//download");
            out.writeUTF(file.getName());
            out.writeLong(fileLength);


            int read = 0;
            byte[] buffer = new byte[8 * 1024];

            while ((read = fis.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            out.flush();

            String status = in.readUTF();
            System.out.println(status);

        }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    //    Загрузка файла с клиента на сервер
    private void getFile(){

        try {

            File file = new File(client.getCurrentPath() + File.separator + in.readUTF());

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
            sendMessage("Error while loading file");
            e.printStackTrace();
        }
    }

    //    Закрытие потоков и канала
    private void close () {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
