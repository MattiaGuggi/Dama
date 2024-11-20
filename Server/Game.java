package Server;

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
            {2,0,2,0,2,0,2,0},
            {0,2,0,2,0,2,0,2},
    
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            
            {1,0,1,0,1,0,1,0},
            {0,1,0,1,0,1,0,1},
            {1,0,1,0,1,0,1,0},
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

    public Boolean checkWin() {
        // Devono esserci solo pedine di un colore
        String color = "";
        
        for (int i=0 ; i<MAX ; i++) {
            for (int j=0 ; j<MAX ; j++) {
                // Se su quella cella c'é una pedina
                if (board[i][j] != null) {
                    // Se il colore non e' gia' stato impostato
                    if (color.equals("")) {
                        color = board[i][j].getColor(); // Ora controllo che siano tutte di questo colore
                    }
                    // N esima pedina incontrata, é diversa dalla prima incontrata?
                    else if (!color.equals(board[i][j].getColor())) {
                        return false; // L'altro giocatore puó ancora muovere
                    }
                }
            }
        }

        // Non ho incontrato problemi (nessuna pedina di colore diverso)
        return true;
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