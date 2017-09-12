package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatWindow extends JFrame {
    public static String userName;

    private JTextArea chatBoxText;
    private JScrollPane chatBox;
    private JTextField messageBox;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;



    public ChatWindow(){
        JPanel head = new JPanel();
        add(head, BorderLayout.NORTH);
        head.add(new JLabel(userName) {{

            setForeground(Color.BLUE);
        }});



        JPanel body = new JPanel();
        body.setLayout(new BorderLayout());
        add(body, BorderLayout.CENTER);
        chatBoxText = new JTextArea();
        chatBoxText.setEditable(false);
        chatBoxText.setLineWrap(true);
        chatBoxText.setBackground(Color.WHITE);
        chatBoxText.setPreferredSize(new Dimension(400, 300));

        chatBox = new JScrollPane(chatBoxText);
        chatBox.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        body.add(chatBox);



        JPanel bottom = new JPanel();
        add(bottom, BorderLayout.SOUTH);
        messageBox = new JTextField();
        messageBox.setPreferredSize(new Dimension(300, 30));
        bottom.add(messageBox);
        messageBox.addActionListener(e ->sendMessage());

        JButton buttonSend =new JButton("Send!"){{
            addActionListener(e ->sendMessage());
        }};

        bottom.add(buttonSend);

        pack();

        boolean serverAvailable = true;

        try{
            socket = new Socket("localhost", 8099);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }catch(IOException e){
            //e.printStackTrace();
            System.out.println("Нет соединения с сервером");
            chatBoxText.append("Нет соединения с сервером\n");
            buttonSend.setEnabled(false);
            serverAvailable=false;
        }

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run(){
                try{
                    while(true){
                        String msg = in.readUTF();
                        chatBoxText.append(msg + "\n");
                    }
                }catch(IOException e){
                    System.out.println("Соединение с сервером потеряно");
                    chatBoxText.append("Соединение с сервером потеряно\n");
                    buttonSend.setEnabled(false);
                }
            }
        });

        if (serverAvailable) thread1.start();

    }

    public void sendMessage(){

        String msg = messageBox.getText()+"\n";
        messageBox.setText("");

        try{
            out.writeUTF(msg);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
