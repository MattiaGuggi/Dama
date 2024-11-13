package Client;

import java.net.*;
import java.rmi.server.ExportException;
import java.util.Scanner;
import java.io.*;

public class ClientHandler extends Thread{
    private Boolean partitaFinita = false;
    public void run(){
        try{
            InetAddress serverAddress = InetAddress.getByName("localhost");
            //Connessione al server

            Socket socket = new Socket(serverAddress,50000);
            System.out.println("Sono connesso al server: "+serverAddress);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            Campo campo = null;

            while(!partitaFinita){
                String result = in.readLine();

                if(result != null){
                    //Devo decodificare il messaggio che mi viene inviato dal server
                    String[] decode = result.split("#");

                    if(result.equals("createGame")){
                        System.out.println("La partita sta per iniziare!");
                        Thread.sleep(2000);
                        
                        campo = new Campo(decode[1]);
                    }
                    System.out.println(result);
                }
            }
            

            socket.close();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
