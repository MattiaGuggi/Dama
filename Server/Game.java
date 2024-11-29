package Server;

import java.util.ArrayList;

public class Game {
    private final int MAX = 8;
 
    private Pedina[][] board = new Pedina[MAX][MAX];
    
    private int turn = 1;
    /* 0-> Primo giocatore che si collega al server (Nero)
     * 1-> Secondo giocatore che si collega al server (Bianco)
    */

    public Game(){
        //Come prima cosa, creo il campo
        this.createBoard();
        
    }

    private void createBoard(){
        final int[][] sampleBoard = {
            {0,2,0,2,0,2,0,2},
            {2,0,2,0,0,0,2,0},
            {0,2,0,2,0,2,0,2},
            {0,0,0,0,0,0,0,0},
            {0,0,0,2,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,1,0},
        };
        for(int i = 0;i<MAX;++i){
            for(int j = 0;j<MAX;++j){
                if(sampleBoard[i][j] == 1){
                    board[i][j] = new Pedina(j, i,"white");
                }
                else if(sampleBoard[i][j] == 2){
                    board[i][j] = new Pedina(j, i,"black");
                }
                else{
                    board[i][j] = null;
                }
            }
        }
    }

    public Pedina[][] getBoard(){
        return this.board;
    }

    //Ritorna il campo in versione stringa
    //Reverse indica se devo invertire il campo
    public String stringifyBoard(Boolean reverse){
        
        // Conversione array in stringa di formato colore#x#y
        String boardData = "";
        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                // Se su quella cella c'é una pedina
                if (board[i][j] != null) {
                    boardData += board[i][j].getString(reverse) + ";";
                }
            }
        }

        // Rimuovi l'ultimo separatore
        if (boardData.length() > 0) {
            boardData = boardData.substring(0, boardData.length()-1);
        }

        return boardData;
    }

    //Metodo che controlla se la partita è finita
    //Ci sono 2 casi per cui una partita puo finire
    //1) Finisci i pezzi
    //2) I tuoi pezzi non hanno più mosse disponibili
    public String[] checkFinishGame() {
        //Prima controllo se ci sono ancora pezzi di colore diverso nella board
        String[] result1 = checkWinForPieces();
        //Se hai vinto per questo, evito di andare avanti e ritorno il risultato
        if(result1[0].equals("true")){
            return result1;
        }

        //Ora devo controllare se i pezzi rimasti possono ancora muoversi

        String[] result2 = checkWinForMoves();
        if(result2[0].equals("true")){
            return result2;
        }

        //Se non entro in niente prima, vuol dire che nessuno ha vinto
        return new String[] {"false",""};
    }

    //Controlla se la partita è finita perché l'avversario non hai più pezzi in vita
    public String[] checkWinForPieces(){

        String color = "";

        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                // Se su quella cella c'é una pedina
                if (board[i][j] != null) {
                    // Se il colore non e' gia' stato impostato
                    if (color.equals("")) {
                        color = board[i][j].getColor(); // Ora controllo che siano tutte di questo colore
                    }
                    // N esima pedina incontrata, é diversa dalla prima incontrata?
                    else if (!color.equals(board[i][j].getColor())) {
                        return new String[] {"false",""}; // L'altro giocatore puó ancora muovere
                    }
                }
            }
        }

        //Vuol dire che ho trovato solo pedine dello stesso colore (Quindi questo colore ha vinto)


        return new String[] { "false", color,"Non ci sono più pedine disponibili nel campo!"};
    }


    //Controlla se i pezzi hanno ancora mosse disponili
    public String[] checkWinForMoves(){

        Boolean canWhiteMove = findMovesFromColor("white");

        //Se il bianco non si puo muovere, vuol dire che ha vinto il black
        if(!canWhiteMove){
            return new String[] {"true","black","Non ci sono più mosse disponibili!"};
        }

        Boolean canBlackMove = findMovesFromColor("black");

        if (!canBlackMove) {
            return new String[] { "true", "white", "Non ci sono più mosse disponibili!" };
        }

        //S
        return new String[] { "false", "" };

    }


    public Boolean findMovesFromColor(String color){
        for(int i = 0;i<MAX;++i){
            for(int j = 0;j<MAX;++j){

                if(board[i][j] != null && board[i][j].getColor() == color){
                    //Ora controlliamo le mosse che puo fare questa pedina

                    Node moves = board[i][j].getPossibleMoves(board);

                    //Se trovo anche solo una mossa che puo fare significa che il bianco si puo muovere
                    if(moves.dl != null || moves.dr != null || moves.ul != null || moves.ur != null){
                        return true;
                    }
                }

            }
        }
        return false;
    }


    public void changeTurn() {
        this.turn = (turn + 1) % 2;
    }
    public int getTurn(){
        return this.turn;
    }
    public int getMax(){
        return this.MAX;
    }
}