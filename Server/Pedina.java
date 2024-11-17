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

        // Se é libera puoi muovere
        // Puó andare solo in avanti quindi controllo solo sulle righe successive
        // Parte dal basso, dove le righe partono da 7 a salire (non da 0)
        // Per provare, si puó muovere solo pedine in basso
        if (this.color.equals("white")) {
            if (x > 0 && y < MAX-1 && board[x-1][y+1] == null) {
                allPossibleMoves.add(new Posizione(x-1, y+1));
            }
            if (x > 0 && y > 0 && board[x-1][y-1] == null) {
                allPossibleMoves.add(new Posizione(x-1, y-1));
            }
        }
        else if (this.color.equals("black")) {
            if (x < MAX-1 && y < MAX-1 && board[x+1][y+1] == null) {
                allPossibleMoves.add(new Posizione(x-1, y+1));
            }
            if (x < MAX-1 && y > 0 && board[x+1][y-1] == null) {
                allPossibleMoves.add(new Posizione(x-1, y-1));
            }
        }

        return allPossibleMoves;
    }
}