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
    public ArrayList<Mossa> getPossibleMoves(Pedina[][] board) {
        int x = posizione.getX();
        int y = posizione.getY();
        ArrayList<Mossa> normalMoves = new ArrayList<>();
        ArrayList<Mossa> captureMoves = new ArrayList<>();
    
        // null indicate cella vuota
        // Per mangiare deve essere di colore diverso
        if (this.color.equals("white") || this.isDama) {
            // Destra avanti
            if (y > 0 && x < MAX-1 && board[y-1][x+1] == null) {
                normalMoves.add(new Mossa(new Posizione(x+1, y-1), false));
            }
            else if (y > 1 && x < MAX-2 && board[y-1][x+1] != null 
                    && !board[y-1][x+1].getColor().equals(this.color) 
                    && board[y-2][x+2] == null) {
                captureMoves.add(new Mossa(new Posizione(x+2, y-2), true));
            }

            // Sinistra avanti
            if (y > 0 && x > 0 && board[y-1][x-1] == null) {
                normalMoves.add(new Mossa(new Posizione(x-1, y-1), false));
            }
            else if (y > 1 && x > 1 && board[y-1][x-1] != null 
                    && !board[y-1][x-1].getColor().equals(this.color) 
                    && board[y-2][x-2] == null) {
                captureMoves.add(new Mossa(new Posizione(x-2, y-2), true));
            }
        } 
        if (this.color.equals("black") || this.isDama) {
            // Destra indietro
            if (y < MAX-1 && x < MAX-1 && board[y+1][x+1] == null) {
                normalMoves.add(new Mossa(new Posizione(x+1, y+1), false));
            }
            else if (y < MAX-2 && x < MAX-2 && board[y+1][x+1] != null 
                    && !board[y+1][x+1].getColor().equals(this.color) 
                    && board[y+2][x+2] == null) {
                captureMoves.add(new Mossa(new Posizione(x+2, y+2), true));
            }

            // Sinistra indietro
            if (y < MAX-1 && x > 0 && board[y+1][x-1] == null) {
                normalMoves.add(new Mossa(new Posizione(x-1, y+1), false));
            }
            else if (y < MAX-2 && x > 1 && board[y+1][x-1] != null 
                    && !board[y+1][x-1].getColor().equals(this.color) 
                    && board[y+2][x-2] == null) {
                captureMoves.add(new Mossa(new Posizione(x-2, y+2), true));
            }
        }
        
        // Rimuovi mosse al di fuori della board
        normalMoves.removeIf(move -> {
            Posizione p = move.getTargetPosition();
            return p.getX() < 0 || p.getX() >= MAX || p.getY() < 0 || p.getY() >= MAX;
        });
        captureMoves.removeIf(move -> {
            Posizione p = move.getTargetPosition();
            return p.getX() < 0 || p.getX() >= MAX || p.getY() < 0 || p.getY() >= MAX;
        });

        // Priorita alle mangiate
        if (!captureMoves.isEmpty()) {
            return captureMoves;
        }
    
        return normalMoves;
    }    

    @Override
    public String toString() {
        return color.charAt(0) + "" + this.posizione;
    }
}
