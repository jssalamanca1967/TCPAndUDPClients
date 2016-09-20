package com.john.tcpandudpclients;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by John on 20/09/2015.
 */
public class ManejadorUsuarios {

    public ArrayList<Usuario> usuariosActivos;
    public int numUsarios;
    public LocationManager locationManager;
    public EnvioDatos envioDatos;
    public boolean tcp;
    public String ip;

    public ManejadorUsuarios(String ip, boolean tcp, int numUsuarios, LocationManager locationManager, EnvioDatos envioDatos){

        usuariosActivos = new ArrayList<>();

        this.envioDatos = envioDatos;
        this.numUsarios = numUsuarios;
        this.locationManager = locationManager;
        this.tcp = tcp;
        this.ip = ip;
    }

    public void pruebaGPS(View view){

        // Acquire a reference to the system Location Manager

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        String locationProvider = LocationManager.GPS_PROVIDER;
        // Or, use GPS location data:
        // String locationProvider = LocationManager.GPS_PROVIDER;

        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = envioDatos.getApplicationContext().checkCallingOrSelfPermission(permission);

        if(res == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

        }
        else{

        }

    }

    public void makeUseOfNewLocation(Location location){

        String velocidad = "" + location.getSpeed() + "m/s";
        String latitud = "" + location.getLatitude();
        String longitud = "" + location.getLongitude();
        String altitud = "" + location.getAltitude();

        envioDatos.mostrarMensaje("Holaaaaa", velocidad + "\n" + latitud + "\n" + longitud + "\n" + altitud);

    }


    public void iniciarUsuarios(){


        for(int i = 0; i < numUsarios; i++){
            Usuario nuevo = new Usuario(i,tcp,ip,envioDatos,numUsarios==1,locationManager);
            usuariosActivos.add(nuevo);

            Thread a = new Thread(nuevo);
            a.start();
        //    nuevo.run();
        }
    }

    public void detenerUsuarios(){
        for(int i = 0; i < numUsarios; i++){
            usuariosActivos.get(i).desactivarGPS();
        }
    }
}
