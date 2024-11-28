package Server;

import java.util.ArrayList;

/**
 * Rappresenta un nodo di un albero.
 * Uso l'albero per rappresentare i percorsi che una pedina puo fare.
 */

public class Node{
    public int x;
    public int y;

    public Node ul;
    public Node ur;
    public Node dr;
    public Node dl;

    public Pedina pieceEaten;
    public Node(int x,int y,Pedina pieceEaten){
        this.x = x;
        this.y = y;
        this.ul = null;
        this.ur = null;
        this.dl = null;
        this.dr = null;
        
        this.pieceEaten = pieceEaten;
    }

    static public String convertTreeToString2(Node node) {
        if (node == null) {
            return "";
        }
        // Creazione della rappresentazione della stringa
        StringBuilder sb = new StringBuilder();
        sb.append("(" + node.x + ":" + node.y + ":" + node.pieceEaten + ")"); // Formato: (x:y:pieceEaten)

        // Aggiunta dei figli, se esistono
        if (node.dl != null || node.dr != null || node.ul != null || node.ur != null) {
            sb.append(" (");
            if (node.dl != null) {
                sb.append("dl: ").append(convertTreeToString2(node.dl)).append(" ");
            }
            if (node.dr != null) {
                sb.append("dr: ").append(convertTreeToString2(node.dr)).append(" ");
            }
            if (node.ul != null) {
                sb.append("ul: ").append(convertTreeToString2(node.ul)).append(" ");
            }
            if (node.ur != null) {
                sb.append("ur: ").append(convertTreeToString2(node.ur)).append(" ");
            }
            sb.append(")");
        }

        return sb.toString();
    }
    
 

    //Ritorna una stringa che rappresenta un albero
    public static String convertTreeToString(Node root){
        //Come prima cosa rappresento l'albero come vettore

        ArrayList<Node> albero = new ArrayList<>();
        Node.convertTreeToArray(root, albero, 0);

        //Ora devo rappresentare il vettore come stringa (Uso quella di default degli alberi)
        return albero + "";
    }

    //Converte una stringa in albero
    public static Node convertStringToTree(String stringa){
        //Come prima cosa devo ricavare il vettore dalla stringa

        ArrayList<Node> root = Node.convertStringToArray(stringa, false, 8);

        //Ora che ho il vettore devo convertirlo in albero

        return Node.convertArrayToTree(root, 0);
    }

    private static void convertTreeToArray(Node nodo, ArrayList<Node> albero, int indice) {
        // Assicura che il vettore abbia almeno la lunghezza necessaria
        while (albero.size() <= indice) {
            albero.add(null); // Riempie eventuali posizioni vuote
        }

        // Inserisce il nodo corrente nell'indice specificato
        albero.set(indice, nodo);

        if (nodo == null)
            return; // Se il nodo è nullo, non proseguire

        // Inserisce ricorsivamente i figli nei loro indici calcolati
        convertTreeToArray(nodo.dl, albero, 4 * indice + 1); // down-left
        convertTreeToArray(nodo.dr, albero, 4 * indice + 2); // down-right
        convertTreeToArray(nodo.ul, albero, 4 * indice + 3); // up-left
        convertTreeToArray(nodo.ur, albero, 4 * indice + 4); // up-right
    }



    
    // Converti un vettore in albero
    private static Node convertArrayToTree(ArrayList<Node> albero, int indice) {
        // Se l'indice è fuori dai limiti o il nodo all'indice è null, restituisci null
        if (indice >= albero.size() || albero.get(indice) == null) {
            return null;
        }

        // Prendi il nodo corrente dall'ArrayList
        Node nodo = albero.get(indice);

        // Ricorsivamente costruisci i figli
        nodo.dl = convertArrayToTree(albero, 4 * indice + 1); // down-left
        nodo.dr = convertArrayToTree(albero, 4 * indice + 2); // down-right
        nodo.ul = convertArrayToTree(albero, 4 * indice + 3); // up-left
        nodo.ur = convertArrayToTree(albero, 4 * indice + 4); // up-right

        return nodo;
    }


    






    //Converte un arrayList in stringa
    public static ArrayList<Node> convertStringToArray(String stringa,Boolean reverse,int max){
        // Tolgo la quadra iniziale e finale
        stringa = stringa.substring(1, stringa.length() - 1);
        String[] elements = stringa.split(", ");

        //System.out.println("Stringa bella:"+stringa);
        ArrayList<Node> path = new ArrayList<>();

        for (String s : elements) {
            if(!s.equals("null")){
                String[] singleEl = s.split(";");
                Integer x = Integer.parseInt(singleEl[0]);
                Integer y = Integer.parseInt(singleEl[1]);
                if(reverse){
                    x = max-1-x;
                    y = max-1-y;
                }
                // Ora analizzo il pieceEaten (colorx-y)
                // Se presente
                Pedina ped;
                //System.out.println("sdadasdsadasdasd: "+s);
                if(singleEl[2].equals("null")){
                    ped = null;
                }
                else{
                    String color = singleEl[2].charAt(0) == 'b' ? "black" : "white";
                    Integer pieceX = (int) singleEl[2].charAt(1) - 48;
                    Integer pieceY = (int) singleEl[2].charAt(3) - 48;
                    if(reverse){
                        pieceX = max - 1 - pieceX;
                        pieceY = max - 1 - pieceY;
                    }
                    ped = new Pedina(pieceX, pieceY, color);
                }
                path.add(new Node(x, y, ped));
            }
            else
                path.add(null);
        }
        
        return path;
    }


    @Override
    public String toString(){
        return this.x+";"+this.y+";"+this.pieceEaten;
    }
}