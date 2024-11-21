package Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import Server.Posizione;

class PedinaGrafica extends JComponent {
    private Color color;
    private Posizione posizione;
    private Boolean isDama = false;
    private ArrayList<Posizione> allPossibleMoves = new ArrayList<>();

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

    // Va ridisegnata cosí si capisce
    public void setIsDama() {
        this.isDama = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
    }

    public void setPawnPossibleMoves(ArrayList<Posizione> allPossibleMoves) {
        this.allPossibleMoves = allPossibleMoves;
    }

    public ArrayList<Posizione> getPawnPossibleMoves() {
        return this.allPossibleMoves;
    }


    @Override
    public String toString() {
        return this.posizione.toString();
    }
}