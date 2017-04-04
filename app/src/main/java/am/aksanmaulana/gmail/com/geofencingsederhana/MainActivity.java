package am.aksanmaulana.gmail.com.geofencingsederhana;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private static final int MY_PERMISSIONS_REQUEST = 99;//int bebas, maks 1 byte
    GoogleApiClient mGoogleApiClient ;
    // Location mLastLocation;
    TextView mLatText;
    TextView mLongText;
    LocationRequest mLocationRequest;

    TextView tvInfo;
    String strKeterangan;

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("shadow","Program Start!");

        mLatText    = (TextView) findViewById(R.id.tvLat);
        mLongText   = (TextView) findViewById(R.id.tvLong);
        tvInfo      = (TextView) findViewById(R.id.tvInfo);

        buildGoogleApiClient();
        createLocationRequest();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //muncul dialog & user memberikan reson (allow/deny), method ini akan dipanggil
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                ambilLokasi();
            } else {
                //permssion tidak diberikan, tampilkan pesan
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Tidak mendapat ijin, tidak dapat mengambil lokasi");
                ad.show();
            }
            return;
        }
    }

    private void ambilLokasi() {
   /* mulai Android 6 (API 23), pemberian persmission
    dilakukan secara dinamik (tdk diawal)
    untuk jenis2 persmisson tertentu, termasuk lokasi
    */

        // cek apakah sudah diijinkan oleh user, jika belum tampilkan dialog
        if (ActivityCompat.checkSelfPermission (this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            return;
        }
        //set agar setiap update lokasi maka UI bisa diupdate
        //setiap ada update maka onLocationChanged akan dipanggil
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        ambilLokasi();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        // pengecekan kalau si user berada di area GIK
        if((location.getLatitude() >= -6.860363 && location.getLatitude() <= -6.860195) &&
                (location.getLongitude() >= 107.589644 && location.getLongitude() <= 107.590127)) {

            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setMessage("Banyak pelanggan!");
            ad.show();

            strKeterangan = "disini banyak pelanggan!";
            Log.i("shadow","Masuk");
        }else{
            strKeterangan = "disini sedikit pelanggan!";
            Log.i("shadow","Tidak");
        }
        mLatText.setText("Latitude:"+String.valueOf(location.getLatitude()));
        mLongText.setText("Longitude:" + String.valueOf(location.getLongitude()));
        tvInfo.setText("Keterangan: "+strKeterangan);
    }
}
