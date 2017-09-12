package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Main {
    private static Thread adminListener;
    private static Thread clientListener;


    public static void main( String[] args ) {

        ServerSocket serverSocket=null;
        Socket socket = null;

//        Thread adminListener;
//        Thread clientListener;

        final boolean exitFlag [] = new boolean[1];
        exitFlag[0] =false;

        try {
            serverSocket = new ServerSocket(8099);
            System.out.println("Сервер запущен ");
            socket = serverSocket.accept(); //режим ожидания, возвращает объект типа сокет, блокирует выполнение кода
            System.out.println("Клиент подключился");

            final DataInputStream in = new DataInputStream(socket.getInputStream());
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            //DataInputStream scanner = new DataInputStream(System.in);
            Scanner scanner = new Scanner(System.in);
            //final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));



            adminListener = new Thread(new Runnable() {
                public void run() {
                    //System.out.println("Scanner thread ready ");


                    while (!adminListener.isInterrupted()) {
                        try {
                            String aMsg = "";
                            aMsg = scanner.nextLine();

                            if (aMsg.equalsIgnoreCase("end")) {
                                throw new IOException();
                            }
                            System.out.println("Server: " + aMsg);
                            out.writeUTF("Server: " + aMsg);
                            out.flush();
                        } catch (IOException e1) {
                            System.out.println("Сервер отключился");
                            adminListener.interrupt();
                            scanner.close();


                        }
                    }
                }
            });


            clientListener = new Thread(new Runnable() {
                public void run() {
                    while (!clientListener.isInterrupted()) {
                        String msg = null;
                        try {
                            msg = in.readUTF();

                            if (msg != null) {
                                System.out.println("Client: " + msg);
                                if (msg.equalsIgnoreCase("end\n")) throw new IOException();
                                out.writeUTF("Client: " + msg); //записывается в буфер
                                out.flush(); //достать сообщение из буфера
                            }
                        } catch (IOException e) {
                            System.out.println("Пользователь отключился");
                            clientListener.interrupt();
                            exitFlag[0]=true;
                        }


                    }
                }
            });
            clientListener.setDaemon(true);
            adminListener.setDaemon(true);

            clientListener.start();
            adminListener.start();

            //System.out.println("main thread");
            while(!clientListener.isInterrupted() &&  !adminListener.isInterrupted());
            //System.out.println("end of main thread");


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось запустить сервер");
//        } catch (ThreadDeath e) {
//
//            System.out.println("Завершение работы...");

        }finally{
            System.out.println("Сервер выключен");
            try{

                socket.close();
                serverSocket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }



}

