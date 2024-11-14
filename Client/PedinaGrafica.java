package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Server.Posizione;

class PedinaGrafica extends JComponent {
    private Color color;
    private Posizione posizione;

    public PedinaGrafica(Posizione posizione) {
        this.posizione = posizione;

        // Aggiunge un listener per il click sulla pedina
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPossibleMoves();
            }
        });
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Posizione getPosition() {
        return this.posizione;
    }

    // Per ora mostra solo la posizione della pedina e non ritorna un'array Pedina[][]
    // Deve mostrare sul campo grafico dove puó muovere
    public void showPossibleMoves () {
        System.out.println("Posizione della pedina cliccata: " + this.posizione);
    }

    // Devo ritornare tutte le posizioni in cui puó andare la pedina cliccata
    public ArrayList<Posizione> getPossibleMoves() {
        ArrayList<Posizione> possibleMoves = new ArrayList<>();

        return possibleMoves;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillOval(10, 10, getWidth() - 20, getHeight() - 20);
    }
}
