package Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import Server.Posizione;
import Server.Pedina;

class PedinaGrafica extends JComponent {
    private int MAX = 8;
    private Color color;
    private Posizione posizione;
    private Pedina[][] board;

    public PedinaGrafica(Posizione posizione, Pedina[][] board) {
        this.posizione = posizione;
        this.board = board;
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
    
    // Per ora mostra solo la posizione della pedina
    // Deve mostrare sul campo grafico dove puó muovere
    public void showPossibleMoves() {
        ArrayList<Posizione> possibleMoves = getPossibleMoves();
        System.out.println("Posizione della pedina cliccata: " + this.posizione);

        for (Posizione pos : possibleMoves) {
            System.out.println("Posizione possibile: " + pos);
        }
    }

    // Devo ritornare tutte le posizioni in cui puó andare la pedina cliccata
    public ArrayList<Posizione> getPossibleMoves() {
        int x = posizione.getX();
        int y = posizione.getY();
        ArrayList<Posizione> allPossibleMoves = new ArrayList<>();

        // Se é libera puoi muovere
        // Puó andare solo in avanti quindi controllo solo sulle righe successive
        // RITORNA SOLO POSIZIONI A SX
        if (x < MAX-1 && y < MAX-1 && this.board[x+1][y+1] == null) {
            allPossibleMoves.add(new Posizione(x+1, y+1));
        }
        else if (x < MAX-1 && y > 0 && this.board[x+1][y-1] == null) {
            allPossibleMoves.add(new Posizione(x+1, y-1));
        }

        return allPossibleMoves;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
    }
}
