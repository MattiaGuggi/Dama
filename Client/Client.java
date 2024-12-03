package Client;

import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ImageIcon icon = new ImageIcon(Client.class.getResource("../dama.png"));
        frame.setIconImage(icon.getImage());
        ClientHandler c = new ClientHandler(frame);
        c.start();
    }
}
