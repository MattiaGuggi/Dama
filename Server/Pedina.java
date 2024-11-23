package Server;

import java.util.ArrayList;

public class Pedina {
    private Posizione posizione;
    private Boolean isDama = false;
    private String color;
    private int MAX = 8;

    public Pedina(int x, int y, String color){
        this.posizione = new Posizione(x, y);
        this.color = color;
    }

    public Boolean getIsDama(){
        return this.isDama;
    }

    public String getColor() {
        return this.color;
    }

    public Posizione getPosizione() {
        return this.posizione;
    }

    public void setIsDama() {
        this.isDama = true;
    }

    public void setPosition(Posizione posizione) {
        this.posizione = posizione;
    }

    public String getString(Boolean reverse) {
        return this.color + "," + this.posizione.getString(reverse);
    }

    // Devo ritornare tutte le posizioni in cui puó andare la pedina cliccata
    public ArrayList<Posizione> getPossibleMoves(Pedina[][] board) {
        int x = posizione.getX();
        int y = posizione.getY();
        ArrayList<Posizione> allPossibleMoves = new ArrayList<>();

        // null indica casella libera
        // Se non é null deve esserci una Pedina
        // Per mangiare deve ovviamente essere di colore diverso
        // Se é dama puó muoversi ovunque
        // !!BISOGNA CONTROLLARE CHE NON ESCA DALLA BOARD ANCHE LA MANGIATA!!
        if (this.color.equals("white") || this.isDama) {
            if (y > 0 && x < MAX-1 && board[y-1][x+1] == null) {
                allPossibleMoves.add(new Posizione(x+1, y-1));
            }
            else if (y > 0 && x < MAX-1 && !board[y-1][x+1].getColor().equals(this.color)) {
                allPossibleMoves.add(new Posizione(x+2, y-2));
            }
            if (y > 0 && x > 0 && board[y-1][x-1] == null) {
                allPossibleMoves.add(new Posizione(x-1, y-1));
            }
            else if (y > 0 && x > 0 && !board[y-1][x-1].getColor().equals(this.color)) {
                allPossibleMoves.add(new Posizione(x-2, y-2));
            }
        }
        else if (this.color.equals("black") || this.isDama) {
            if (y < MAX-1 && x < MAX-1 && board[y+1][x+1] == null) {
                allPossibleMoves.add(new Posizione(x+1, y+1));
            }
            else if (y < MAX-1 && x < MAX-1 && !board[y+1][x+1].getColor().equals(this.color)) {
                allPossibleMoves.add(new Posizione(x+2, y+2));
            }
            if (y < MAX-1 && x > 0 && board[y+1][x-1] == null) {
                allPossibleMoves.add(new Posizione(x-1, y+1));
            }
            else if (y < MAX-1 && x > 0 && !board[y+1][x-1].getColor().equals(this.color)) {
                allPossibleMoves.add(new Posizione(x-2, y+2));
            } 
        }

        // Controlla che le posizioni siano tutte dentro la board
        for (Posizione p : allPossibleMoves) {
            if (p.getX() < 0 || p.getX() > MAX-1 || p.getY() < 0 || p.getY() > MAX-1)
                allPossibleMoves.remove(p);
        }

        // In qualche modo va comunicato che é una mossa per mangiare
        
        return allPossibleMoves;
    }

    @Override
    public String toString() {
        return color.charAt(0) + "" + this.posizione;
    }
}