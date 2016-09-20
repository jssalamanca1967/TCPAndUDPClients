package com.john.tcpandudpclients;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EnvioDatos extends AppCompatActivity {

    public boolean esTcp;
    public int numUsuarios;
    public String ip;
    public ManejadorUsuarios manejador;
    public LocationManager locationManager;
    public LocationListener locationListener;
    public Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envio_datos);

        Intent intent = getIntent();
        esTcp = intent.getBooleanExtra(Main.TCP, true);
        ip = intent.getStringExtra(Main.IP);

        TextView lblTitulo = (TextView) findViewById(R.id.lblTitulo);
        lblTitulo.setText(esTcp ? "TCP" : "UDP");

        pruebaGPS();

    }

    public void pruebaGPS(){

        System.out.println("Empezando GPS");

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
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
            int res = getApplicationContext().checkCallingOrSelfPermission(permission);

            if(res == PackageManager.PERMISSION_GRANTED){

                location = null;
                locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
            }




    }

    public synchronized void makeUseOfNewLocation(Location location){

        String velocidad = "" + location.getSpeed() + "m/s";
        String latitud = "" + location.getLatitude();
        String longitud = "" + location.getLongitude();
        String altitud = "" + location.getAltitude();

        //mostrarMensaje("Holaaaaa", velocidad + "\n" + latitud + "\n" + longitud + "\n" + altitud);

        this.location = location;

        //System.out.println("Actualizacion: " + location.getAltitude());

        notify();

    }

    public synchronized void solicitarLocalizacion(Usuario usuario){



            while(location == null) {
                try {
                    System.out.println("Pidiendo");

                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            usuario.enviarInfo(location);

            location = null;




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_envio_datos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public synchronized void agregarReporte(String reporte){

        if(numUsuarios == 1) {

            TextView lblDatos = (TextView) findViewById(R.id.lblDatos);

            lblDatos.setText(lblDatos.getText() + "\n" + reporte);
        }
    }

    public void iniciarEnvioDatos(View view){

        EditText txtNumUsuarios = (EditText)findViewById(R.id.txtUsuarios);

        String numero = txtNumUsuarios.getText().toString();

        try{
            int num = Integer.parseInt(numero);

            empezarPrueba(num);

        }catch(NumberFormatException e){
            mostrarMensaje("Error", "Por favor introduzca un número válido");
        }


    }

    public void empezarPrueba(int num){
        numUsuarios = num;

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        manejador = new ManejadorUsuarios(ip,esTcp,numUsuarios,locationManager,this);

        manejador.iniciarUsuarios();
    }

    public void detenerEnvioDatos(View view){

        manejador.detenerUsuarios();

    }

    public void mostrarMensaje(String titulo, String mensaje){

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(mensaje)
                .setTitle(titulo)
                .setCancelable(true)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();

    }
}
