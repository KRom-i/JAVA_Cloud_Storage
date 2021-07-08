package Server;

import Client.*;
import Files.*;
import Logger.Log;
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
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

    private Client client;

    public ClientHandler (Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());

    }

    //    Обработка команд от клиента
    @Override
    public void run () {

        client = new Client("USER", ROOT, socket.getInetAddress());

        Log.info("Client connected: " + client.toString());

            try {

            while (true) {

                String[] command = getMessage().split(":");

                if (client.isAuth()) {

                    if ("/download".equals(command[0])) {

                        if (command[1].startsWith("/rm!")){
                            sendFile(command[1].split("!")[1]);
                            delete(command[1].split("!")[1]);
                        } else {
                            sendFile(command[1]);
                        }

                    } else if ("/upload".equals(command[0])) {
                        getFile();

                    } else if ("/cd".equals(command[0])) {
                        changingCurrentDirectory(command[1]);

                    } else if ("//cd".equals(command[0])) {
                        setCurrentDirectory(command[1]);

                    } else if ("/rm".equals(command[0])) {
                        delete(command[1]);

                    } else if ("/mkdir".equals(command[0])) {
                        createDirectory(command[1]);

                    } else if ("/touch".equals(command[0])) {
                        createFile(command[1]);

                    } else if ("/rename".equals(command[0])) {
                            renameFile(command[1], command[2]);

                    } else if ("/copy".equals(command[0])){
                        copy(command[1]);

                    } else if ("/exit".equals(command[0])) {
                        sendMessage("/exit");
                        break;
                    }

                    outListFiles();
                }

            }

            } catch (Exception e){
                e.printStackTrace();
            } finally {
                close();
            }

    }



    // Отправка сообщения в цикле клиенту в формате UTF-8
    private void sendMessage(List<String> stringList) throws IOException {
        for (String msg: stringList
        ) {
            sendMessage(msg);
        }
    }

    // Отправка сообщения клиенту в формате UTF-8
    private void sendMessage (String massage) throws IOException {
        out.writeUTF(massage);
        Log.info("OUT MSG: " + massage);
    }

    //    Метод возвращает сообщения от клиента в формате UTF-8
    private String getMessage() throws IOException {
        String massage = in.readUTF();
        Log.info("IN MSG: " + massage);
        return massage;
    }

    //  Отправка клиенту текущей директории и списка файлов данной директории
    private void outListFiles() throws IOException {
        sendMessage(client.currentDirInfo());
        sendMessage(getFilesList());
    }

    //    Метод возвращает список файлов текущей директории
    private List<String> getFilesList () {

        File[] files = new File(client.getCurrentPath().toString()).listFiles();
        List<String> stringList = new ArrayList<>();

        int numberFile = files.length;

        if (numberFile > 0){
            stringList.add("/ls:"+ numberFile);
            for (File f: files
            ) {
                stringList.add(
                        new FileInfo(f, (client.getCurrentDir() +"/"+ f.getName())).toString()
                );
            }
        } else {
            stringList.add("/ls:"+ numberFile);
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
            File file = new File(client.getCurrentPath() + File.separator + command);
            if (file.isDirectory()){
                client.setCurrentDir(client.getCurrentDir() + "/" + command);
            } else {
                System.out.println("method open file");
            }

        }

    }

    //    Установить текущую директорию
    public void setCurrentDirectory (String dir) {

            if (!dir.startsWith("/")) {
                dir = "/" + dir;
            }
            File file = new File(client.getRootClient() + dir);
            if (file.isDirectory()) {
                client.setCurrentDir(dir);
            } else {
                System.out.println("method open file: " + file.getAbsolutePath());
            }


    }

    //   Удаление файла или директории
    private void delete(String command) {
        Path pathDel = Paths.get(client.getRootClient() +  File.separator + command);
        try {
            Files.delete(pathDel);
        } catch (Exception io) {
            io.printStackTrace();
        }
    }

    //   Переименовать файл или директорию
    private void renameFile (String nameSrcFile, String nameDestFile) {

        File srcFile = new File(client.getCurrentPath() +  File.separator + nameSrcFile);
        File destFile = new File(client.getCurrentPath() +  File.separator + nameDestFile);
        srcFile.renameTo(destFile);
    }

    //    Копирование файлов / директории
    private void copy(String src) {

        Path path1 = Paths.get(client.getRootClient() + File.separator + src);

        Path path2 = Paths.get(client.getCurrentPath() + File.separator + path1.getFileName());

        try {
            Files.copy(path1, path2);
        } catch (IOException e){

        }

    }

    //    Передача файла с сервера на клиента
    private void sendFile(String command) {

        File file = new File(client.getRootClient()  + command);

        try (FileInputStream fis = new FileInputStream(file)){

        if (file.exists()) {

            long fileLength = file.length();


            out.writeUTF("/download");
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

        } catch (Exception e){
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

            String status = "";

            if (size == file.length()){
                status = ("File downloaded");
            } else {
                status = ("Error while loading file");
            }

//            sendMessage(status);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //    Закрытие потоков и канала
    private void close () {

        Log.info("Client disconnected: " + client.toString());

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
