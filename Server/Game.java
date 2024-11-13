package Server;

public class Game {
    private final int MAX = 8;
 
    private Pedina[][] board = new Pedina[MAX][MAX];

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
                    board[i][j] = new Pedina(j, i,"black");
                }
                else if(sampleBoard[i][j] == 2){
                    board[i][j] = new Pedina(j, i,"white");
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
}