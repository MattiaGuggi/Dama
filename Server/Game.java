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
}