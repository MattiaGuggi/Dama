package Client;

import javax.swing.*;
import java.awt.*;

class PedinaGrafica extends JComponent {
    private Color color;

    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
    }
}
