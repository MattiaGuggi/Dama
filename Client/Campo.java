package Client;

import java.awt.*;
import javax.swing.*;

class JFrame1 {
    private final int MAX = 8;
    private JButton[][] buttons = new JButton[MAX][MAX];
    private String[][] drawings;

    public JFrame1(String[][] drawings) {
        this.drawings = drawings;
        JFrame frame = new JFrame("Dama");

        frame.setSize(800, 800);
        frame.setLayout(new GridLayout(MAX, MAX));
        frame.setVisible(true);

        for (int i=0 ; i<MAX ; i++) {
            for (int j=0 ; j<MAX ; j++) {
                buttons[i][j] = new JButton("");
                frame.add(buttons[i][j]);
            }
        }
    }
}

// null = no pedina
class Disegno extends JComponent {
    String[][] board = null;

    public Disegno(String[][] board) {
        this.board = board;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
        
        super.paintComponent(g);
        g.setColor(Color.white);
        g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
        
    }
}

public class Campo {
    private final int MAX = 8;
    private String[][] board;

    public Campo(String initialString) {
        this.board = new String[MAX][MAX];
    }

    public void drawCampo() {
        Disegno disegno = new Disegno(this.board);
    }
}