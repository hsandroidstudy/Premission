package dgsw.hs.kr.premission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://naver.com");
    }

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
        if(requestCode != REQ_SEND_SMS) return;
        if(permissions[0].equals(Manifest.permission.SEND_SMS) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            sendSMS();
        else
            Toast.makeText(this, "문자 전송 권한이 없습니다.", Toast.LENGTH_SHORT).show();
    }

    private void sendSMS(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("010-4524-5468", null, "Hello!", null, null);
    }
}
