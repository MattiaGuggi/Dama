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
        // Devo capire se andare in alto o in basso
        int direction = (this.color.equals("white")) ? 1 : 0;
        // direction = 0 la pedina deve andare GIU
        // direction = 1 la pedina deve andare SU
        
        Node tree = new Node(this.posizione.getX(),this.posizione.getY(),null);
        
        //Puó mangiare piú pedine
        this.checkForEats(board,direction,tree,"",0);

        // Le mangiate hanno prioritá sulle mosse normali
        if(tree.ul == null && tree.ur == null && tree.dl == null && tree.dr == null)
            // La pedina puó muovere solo una volta, quindi non serve ricorsione :)
            this.checkForMove(board,direction,tree);
        
        return tree;
    }    
    
    private void checkForMove(Pedina[][] board,int direction,Node tree){
         //Destra
         if(this.posizione.getX() < this.MAX - 1){
            //Giu
            if((direction == 0 || this.isDama) && this.posizione.getY() < this.MAX - 1 && board[this.posizione.getY() + 1][this.posizione.getX() + 1] == null )
                tree.dr = new Node(this.posizione.getX() + 1,this.posizione.getY() + 1,null);
            //Su
            if((direction == 1 || this.isDama) && this.posizione.getY() > 0 && board[this.posizione.getY() - 1][this.posizione.getX() + 1] == null)
                tree.ur = new Node(this.posizione.getX() + 1,this.posizione.getY() - 1,null);
        }
        //Sinistra
        if(this.posizione.getX() > 0){
            //Giu
            if((direction == 0 || this.isDama) && this.posizione.getY() < this.MAX - 1 && board[this.posizione.getY() + 1][this.posizione.getX() - 1] == null)
                tree.dl = new Node(this.posizione.getX() - 1,this.posizione.getY() + 1,null);
            //Su
            if((direction == 1 || this.isDama) && this.posizione.getY() > 0 &&  board[this.posizione.getY() - 1][this.posizione.getX() - 1] == null )
                tree.ul = new Node(this.posizione.getX() - 1,this.posizione.getY() - 1,null);
        }
    }

    private void checkForEats(Pedina[][] board,int orientation,Node node,String lastDirection,int depth){
        // Si possono mangiare massimo 3 pedine
        if(depth == 3)
            return;

        //Se non sono dama, non posso mangiare una dama
        //Prova ad andare in alto
        if((orientation == 0 || this.isDama) && node.y < this.MAX - 2){
            //Da controllare:
            //Colore diverso dal mio + casella libera

            //Sinistra:
            if(node.x > 1 && board[node.y+1][node.x-1] != null && !(board[node.y+1][node.x-1].isDama && !this.isDama ) && board[node.y+1][node.x-1].color != this.color && board[node.y+2][node.x-2] == null){
                if(lastDirection != "ur" ){
                    node.dl = new Node(node.x-2,node.y+2,board[node.y+1][node.x-1]);
                    //Controlla se ci sono piú mosse possibili
                    this.checkForEats(board,orientation,node.dl,"dl",depth+1);
                }
            }
            //Destra:
            if(node.x < MAX-2 && board[node.y+1][node.x+1] != null && !(board[node.y+1][node.x+1].isDama && !this.isDama ) && board[node.y+1][node.x+1].color != this.color && board[node.y+2][node.x+2] == null){
                if(lastDirection != "ul" ){
                    node.dr = new Node(node.x+2,node.y+2,board[node.y+1][node.x+1]);

                    this.checkForEats(board,orientation,node.dr,"dr",depth+1);
                }
            }
        }
        //Prova ad andare in alto
        if((orientation == 1 || this.isDama) && node.y > 1){
            //Da controllare:
            //Colore diverso dal mio + casella libera
            
            //Sinistra:
            if(node.x > 1 && board[node.y-1][node.x-1] != null && !(board[node.y-1][node.x-1].isDama && !this.isDama ) && board[node.y-1][node.x-1].color != this.color && board[node.y-2][node.x-2] == null){
                if(lastDirection != "dr"  ){
                    node.ul = new Node(node.x-2,node.y-2,board[node.y-1][node.x-1]);
                    //Controlla se ci sono piú mosse possibili
                    this.checkForEats(board,orientation,node.ul,"ul",depth+1);
                }
            }

            //Destra
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