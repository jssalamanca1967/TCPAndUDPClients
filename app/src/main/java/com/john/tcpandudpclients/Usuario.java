package com.john.tcpandudpclients;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;

import java.io.*;
import java.net.*;

import java.sql.Date;

/**
 * Created by John on 20/09/2015.
 */
public class Usuario  implements Runnable{

    public boolean tcp;
    public EnvioDatos envioDatos;
    public boolean enviarInterfaz;
    public String IP;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public boolean corriendo;
    public int id;

    public Usuario(int id, boolean tcp, String ip, EnvioDatos envioDatos, boolean enviarAInterfaz, LocationManager locationManager) {

        this.id = id;
        this.envioDatos = envioDatos;
        this.tcp = tcp;
        this.enviarInterfaz = enviarAInterfaz;
        IP = "157.253.212.7";//ip;
        this.locationManager = locationManager;
        corriendo = true;

        System.out.println("Creado un usuario");

    }

    public void desactivarGPS(){

        corriendo = false;

        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = envioDatos.getApplicationContext().checkCallingOrSelfPermission(permission);

        if (res == PackageManager.PERMISSION_GRANTED) {

    //        locationManager.removeUpdates(locationListener);

        }

    }


    public void activarGPS() {

        // Acquire a reference to the system Location Manager
        System.out.println("Iniciando GPS");

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                enviarInfo(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        String locationProvider = LocationManager.GPS_PROVIDER;

        // Or, use GPS location data:
        // String locationProvider = LocationManager.GPS_PROVIDER;

        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = envioDatos.getApplicationContext().checkCallingOrSelfPermission(permission);

        if (res == PackageManager.PERMISSION_GRANTED) {
        //    locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

        } else {

        }


    }

    public void enviarInfo(Location location) {

        // Protocolo: latitud::longitud::altitud::velocidad::fecha

        String texto = "";

        String latitud = "" + location.getLatitude();
        String longitud = "" + location.getLongitude();
        String altitud = "" + location.getAltitude();
        String velocidad = "" + location.getSpeed() + "m/s";
        String fecha = new Date(System.currentTimeMillis()).toString();

        texto = latitud + "::" + longitud + "::" + altitud + "::" + velocidad + "::" + fecha;

        if (enviarInterfaz)
            registrarEnEnvioDatos(texto);

        if (tcp) {
            try {
                enviarTCP(texto);
            } catch (Exception e) {
                envioDatos.mostrarMensaje("Error", e.getMessage());
                e.printStackTrace();
            }
        } else {
            try {
                enviarUDP(texto);
            } catch (Exception e) {
                envioDatos.mostrarMensaje("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void enviarTCP(String texto) throws Exception {

        int PUERTO = 6789;

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        String sentence;
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket(IP, PUERTO);

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        outToServer.writeBytes("Hello" + '\n');

        sentence = texto;

        System.out.println("Enviando: " + sentence);

        outToServer.writeBytes(sentence + '\n');

        modifiedSentence = inFromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);

        if (modifiedSentence.equals("ERROR")) {
            envioDatos.mostrarMensaje("Error TCP", "Error al enviar el mensaje: " + texto);
            System.out.println("Please try again");
        } else if (modifiedSentence.equals("OK")) {
            System.out.println("Envío aceptado");
        } else {
            System.out.println("Que carajos mandé? " + sentence);
        }


        clientSocket.close();
    }

    public void registrarEnEnvioDatos(String texto) {
        envioDatos.agregarReporte(texto);
    }

    public void enviarUDP(String texto) throws Exception {

        int PUERTO = 9876;

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(IP);
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

        // Recibe y convierte el mensaje a enviar

        String sentence = "Hello";
        sendData = sentence.getBytes();

        // Envía el mensaje
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PUERTO);
        clientSocket.send(sendPacket);

        // Recibe y convierte el mensaje a enviar

        byte[] a = new byte[10];

        for(int i = 0; i < a.length; i++)
            a[i] = 0;

        sentence = texto;
        sendData = a;
        sendData = sentence.getBytes();

        // Envía el mensaje
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PUERTO);
        clientSocket.send(sendPacket);

        // Recibe la respuesta
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);  // Espera a que llegue el paquete

        String modifiedSentence = new String(receivePacket.getData());
    //    System.out.println("FROM SERVER:" + modifiedSentence);

        if (modifiedSentence.contains("ERROR")) {
            envioDatos.mostrarMensaje("Error UDP", "Hubo un error enviando UDP: " + texto);
        //    System.out.println("Please try again");
        } else if (modifiedSentence.contains("OK")) {
        //    System.out.println("Envío aceptado");
        } else {
        //    System.out.println("Que carajos mandé? " + sentence);
        }


        clientSocket.close();

    }

    public synchronized void dormir() throws Exception{
        Thread.sleep(10);
    }

    public void run() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        if(Looper.myLooper() == null)
            Looper.prepare();

    //    activarGPS();

    //    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, Looper.myLooper());

        int i = 0;

        while(i < 10){

            try{
                dormir();

                envioDatos.solicitarLocalizacion(this);
//                String texto = "4.980973950::7.907456745::2309::3m/s::" + (new Date(System.currentTimeMillis()).toString());
//
//                registrarEnEnvioDatos(texto);
//
//                if(tcp){
//
//                    enviarTCP(texto);
//                }
//                else{
//                    enviarUDP(texto);
//                }
            }catch(Exception e){
                e.printStackTrace();
            }



        //    String permission = "android.permission.ACCESS_FINE_LOCATION";
        //    int res = envioDatos.getApplicationContext().checkCallingOrSelfPermission(permission);

        //    if (res == PackageManager.PERMISSION_GRANTED) {
        //        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, Looper.myLooper());
        //    }

            i++;
        }

        System.out.println("Acabe");
    }
}
