package Client;

import javax.swing.*;
import java.awt.*;
import Server.Posizione;

class PedinaGrafica extends JComponent {
    private Color color;
    private Posizione posizione;

    public PedinaGrafica(Posizione posizione) {
        this.posizione = posizione;
    }

    public void setOpacity(float opacity) {
        int alpha = Math.round(opacity * 255);
        this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        repaint();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor () {
        return this.color;
    }

    public void setPosition(Posizione posizione) {
        this.posizione = posizione;
    }

    public Posizione getPosition() {
        return this.posizione;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
    }

    @Override
    public String toString() {
        return this.posizione.toString();
    }
}