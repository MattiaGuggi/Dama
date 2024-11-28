package Client;

import javax.swing.*;
import java.awt.*;

import Server.Node;
import Server.Posizione;

class PedinaGrafica extends JComponent {
    private Color color;
    private Posizione posizione;
    private Boolean isDama = false;
    private Node allPossibleMoves;

    public PedinaGrafica(Posizione posizione,Boolean dama) {
        this.posizione = posizione;
        this.isDama = dama;
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

    // Va ridisegnata cosí si capisce
    public void setIsDama() {
        this.isDama = true;
    }

    public Boolean getIsDama() {
        return this.isDama;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
        if(this.isDama){
            g.setColor(Color.GREEN);
            g.fillOval(20, 20, getWidth() - 40, getHeight() - 40);
        }
    }

    public void setPawnPossibleMoves(Node allPossibleMoves) {
        this.allPossibleMoves = allPossibleMoves;
    }

    public Node getPawnPossibleMoves() {
        return this.allPossibleMoves;
    }


    @Override
    public String toString() {
        return this.posizione.toString();
    }
}