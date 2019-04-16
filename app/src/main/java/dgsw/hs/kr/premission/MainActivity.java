package dgsw.hs.kr.premission;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_SEND_SMS = 1;
    ToggleButton toggleButton;
    private BluetoothAdapter bAdapter;
    private TextView textView;
    private LocationManager manager;

    private void initLocationManager(){
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try{
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, listener);
        } catch (SecurityException e){
            Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String strLocation = "LAT: " + location.getLatitude();
            strLocation += "LNG: " + location.getLongitude();
            textView.setText(strLocation);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener((v, b) -> {
            if(b){
                bAdapter.enable();
            } else {
                bAdapter.disable();
            }
        });

        textView = findViewById(R.id.textView);
        int p = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(p == PackageManager.PERMISSION_GRANTED){
            initLocationManager();
        } else {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }

//        updateBTButton(bAdapter.isEnabled());

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://naver.com");
    }

    @Override
    protected void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Receiver", "onReceive");
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch(state){
                case BluetoothAdapter.STATE_ON:
                    Log.d("Receiver", "Bluetooth ON");
                    toggleButton.setChecked(true);
                    break;
                case BluetoothAdapter.STATE_OFF:
                    Log.d("Receiver", "Bluetooth OFF");
                    toggleButton.setChecked(false);
                    break;
            }
        }
    };

    public void onSendSMS(View v){
        if(Build.VERSION.SDK_INT >= 23){
            int permission =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            if(permission != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {Manifest.permission.SEND_SMS}, REQ_SEND_SMS);
                return;
            }
        }
        sendSMS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQ_SEND_SMS){
            if(permissions[0].equals(Manifest.permission.SEND_SMS) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                sendSMS();
            else
                Toast.makeText(this, "문자 전송 권한이 없습니다.", Toast.LENGTH_SHORT).show();
        } else if(requestCode == 1001) {
            if(permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initLocationManager();
        }

    }

    private void sendSMS(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("010-4524-5468", null, "Hello!", null, null);
    }
}
