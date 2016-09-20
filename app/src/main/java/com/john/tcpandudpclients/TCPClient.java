package com.john.tcpandudpclients;

import java.io.*;
import java.net.*;

/**
 * Created by John on 19/09/2015.
 */
public class TCPClient implements Runnable {

    public final static int PUERTO = 6789;

    public String IP;
    public int id;

    public TCPClient(int pId, String pIp){
        IP = pIp;
        id = pId;
    }

    public void empezarEscucharGPS(){

    }

    public void run() {

        try {

            String sentence;
            String modifiedSentence;
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            Socket clientSocket = new Socket(IP, PUERTO);

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            boolean aceptado = false;

            while (!aceptado) {


                sentence = inFromUser.readLine();

                System.out.println("Enviando: " + sentence);

                outToServer.writeBytes(sentence + '\n');

                modifiedSentence = inFromServer.readLine();
                System.out.println("FROM SERVER: " + modifiedSentence);

                if (modifiedSentence.equals("ERROR")) {
                    System.out.println("Please try again");
                } else if (modifiedSentence.equals("OK")) {
                    System.out.println("Envío aceptado");
                    aceptado = true;
                } else {
                    System.out.println("Que carajos mandé? " + sentence);
                }

            }

            clientSocket.close();

        }
        catch(Exception e){

        }

    }

}