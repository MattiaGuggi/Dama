package Client;

import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ClientHandler c = new ClientHandler(frame);
        c.start();
    }
}
