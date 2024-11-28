package Server;


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
        return this.color + "-" + this.posizione.getString(reverse);
    }

    // Devo ritornare tutte le posizioni in cui puó andare la pedina cliccata
    public Node getPossibleMoves(Pedina[][] board) {
        //First i have to understand where I can go, top or bottom
        int direction = (this.color.equals("white")) ? 1 : 0;
        //direction = 0 means that the piece must go DOWN
        //direction = 1 means that the piece must go UP
        
        Node tree = new Node(this.posizione.getX(),this.posizione.getY(),null);

        
        //But it can eat multiple pieces
        this.checkForEats(board,direction,tree,"",0);

        //If eating is possible, you are forced to eat
        if(tree.ul == null && tree.ur == null && tree.dl == null && tree.dr == null)
            //The piece can move just once, so I do not need a recursive function
            this.checkForMove(board,direction,tree);
        
        return tree;
    }    
    
    private void checkForMove(Pedina[][] board,int direction,Node tree){
         //Check right
         if(this.posizione.getX() < this.MAX - 1){
            //Down
            if((direction == 0 || this.isDama) && this.posizione.getY() < this.MAX - 1 && board[this.posizione.getY() + 1][this.posizione.getX() + 1] == null )
                tree.dr = new Node(this.posizione.getX() + 1,this.posizione.getY() + 1,null);
            //Up
            if((direction == 1 || this.isDama) && this.posizione.getY() > 0 && board[this.posizione.getY() - 1][this.posizione.getX() + 1] == null)
                tree.ur = new Node(this.posizione.getX() + 1,this.posizione.getY() - 1,null);
        }
        //Check left
        if(this.posizione.getX() > 0){
            //Down
            if((direction == 0 || this.isDama) && this.posizione.getY() < this.MAX - 1 && board[this.posizione.getY() + 1][this.posizione.getX() - 1] == null)
                tree.dl = new Node(this.posizione.getX() - 1,this.posizione.getY() + 1,null);
            //Up
            if((direction == 1 || this.isDama) && this.posizione.getY() > 0 &&  board[this.posizione.getY() - 1][this.posizione.getX() - 1] == null )
                tree.ul = new Node(this.posizione.getX() - 1,this.posizione.getY() - 1,null);
        }
    }


    private void checkForEats(Pedina[][] board,int orientation,Node node,String lastDirection,int depth){
        //Max depth = 3
        if(depth == 3)
            return;

        //If I am not a boss, I can't eat boss pieces
        //Try to go down
        if((orientation == 0 || this.isDama) && node.y < this.MAX - 2){
            //What I have to check:
            //-I need a piece which color is different from mine
            //-I need a empty square to go

            //Left:
            
            if(node.x > 1 && board[node.y+1][node.x-1] != null && !(board[node.y+1][node.x-1].isDama && !this.isDama ) && board[node.y+1][node.x-1].color != this.color && board[node.y+2][node.x-2] == null){
                if(lastDirection != "ur" ){
                    node.dl = new Node(node.x-2,node.y+2,board[node.y+1][node.x-1]);
                    //Check if there are more possible moves
                    this.checkForEats(board,orientation,node.dl,"dl",depth+1);
                }
            }
            //Right:

            if(node.x < MAX-2 && board[node.y+1][node.x+1] != null && !(board[node.y+1][node.x+1].isDama && !this.isDama ) && board[node.y+1][node.x+1].color != this.color && board[node.y+2][node.x+2] == null){

                if(lastDirection != "ul" ){
                    node.dr = new Node(node.x+2,node.y+2,board[node.y+1][node.x+1]);

                    this.checkForEats(board,orientation,node.dr,"dr",depth+1);
                }
            }
            
        }
        //Try to go up
        if((orientation == 1 || this.isDama) && node.y > 1){
            //What i have to check:
            //-I need a piece which color is different from mine
            //-I need a empty square to go
            //Left:
            if(node.x > 1 && board[node.y-1][node.x-1] != null && !(board[node.y-1][node.x-1].isDama && !this.isDama ) && board[node.y-1][node.x-1].color != this.color && board[node.y-2][node.x-2] == null){
                if(lastDirection != "dr"  ){
                    node.ul = new Node(node.x-2,node.y-2,board[node.y-1][node.x-1]);
                    //Check if there are more possible moves
                    this.checkForEats(board,orientation,node.ul,"ul",depth+1);
                }
            }

            //Right:
            if(node.x < MAX-2 &&  board[node.y-1][node.x+1] != null && !(board[node.y-1][node.x+1].isDama && !this.isDama ) && board[node.y-1][node.x+1].color != this.color && board[node.y-2][node.x+2] == null){
                if(lastDirection != "dl" ){
                    node.ur = new Node(node.x+2,node.y-2,board[node.y-1][node.x+1]);
                    //Check if there are more possible moves
                    this.checkForEats(board,orientation,node.ur,"ur",depth+1);
                }
            }

        }
    }


    @Override
    public String toString() {
        return color.charAt(0) + "" + this.posizione;
    }
}