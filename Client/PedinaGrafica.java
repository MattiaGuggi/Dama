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
    
    // Deve mostrare sul campo grafico dove puó muovere e non sul terminale
    public void showPossibleMoves(Pedina[][] board) {
        ArrayList<Posizione> possibleMoves = this.getPossibleMoves(board);

        for (Posizione pos : possibleMoves) {
            System.out.println("Posizione possibile: " + pos);
        }
    }

    // Devo ritornare tutte le posizioni in cui puó andare la pedina cliccata
    public ArrayList<Posizione> getPossibleMoves(Pedina[][] board) {
        int x = posizione.getX();
        int y = posizione.getY();
        ArrayList<Posizione> allPossibleMoves = new ArrayList<>();

        // Se é libera puoi muovere
        // Puó andare solo in avanti quindi controllo solo sulle righe successive
        // Parte dal basso, dove le righe partono da 7 a salire (non da 0)
        // Per provare, si puó muovere solo pedine in basso
        if (x > 0 && y < MAX-1 && board[x-1][y+1] == null) {
            allPossibleMoves.add(new Posizione(x-1, y+1));
        }
        if (x > 0 && y > 0 && board[x-1][y-1] == null) {
            allPossibleMoves.add(new Posizione(x-1, y-1));
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
