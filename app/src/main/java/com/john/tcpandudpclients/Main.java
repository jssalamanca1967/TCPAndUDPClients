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
import android.widget.FrameLayout;
import android.widget.RadioButton;

public class Main extends AppCompatActivity {

    public final static String TCP = "TCP";
    public final static String IP = "IP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void iniciar(View view){

        EditText txtIP =  (EditText)findViewById(R.id.txtIP);
        String ip = txtIP.getText().toString();

        String[] campos = ip.split(".");
//        if(campos.length != 4){

  //          mostrarMensaje("Error", "Por favor introduzca una IP válida " + campos.length);

//        }
  //      else{

            RadioButton radioTCP = (RadioButton) findViewById(R.id.radioTCP);

            boolean tcp = radioTCP.isChecked();

            aEnviarDatos(tcp, ip);

//        }
    }

    public void pruebaGPS(View view){

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);

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

        mostrarMensaje("Holaaaaa", velocidad + "\n" + latitud + "\n" + longitud + "\n" + altitud);

    }

    public void aEnviarDatos(boolean tcp, String ip){

        Intent intent = new Intent(this, EnvioDatos.class);

        intent.putExtra(IP, ip);
        intent.putExtra(TCP , tcp);

        startActivity(intent);
    }
}
