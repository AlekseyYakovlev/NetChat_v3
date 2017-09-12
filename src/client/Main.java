package client;

import javax.swing.*;

public class Main {
    public static void main( String[] args ) {
        ChatWindow.userName="Aleksey";

        ChatWindow chatWindow = new ChatWindow();
        chatWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        chatWindow.setVisible(true);
    }
}
