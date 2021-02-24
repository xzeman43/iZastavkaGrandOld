package cz.zemankrystof.izastavka;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        else {
            LeakCanary.install(getApplication());
        }*/

        setContentView(R.layout.activity_splash);

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);

        Fabric.with(this, new Crashlytics());

        String[] permissions = {/*Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,*/
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (!checkIfAlreadyhavePermission(permissions)) {
            requestForSpecificPermission(permissions);
        } else {
           Intent mainActivity = new Intent(this, MainActivity.class);
           startActivity(mainActivity);
           Splash.this.finish();
        }
    }

        private boolean checkIfAlreadyhavePermission(String[] permissions) {
            Log.d("Checking","Permissions!");
            int result = PackageManager.PERMISSION_DENIED;
            for (String permission : permissions) {
                result = ContextCompat.checkSelfPermission(this, permission);
                if (result == PackageManager.PERMISSION_DENIED){
                    break;
                }
                Log.d("Permission", "" + result);
            }
            Log.d("Permission", "" + result);
            return result == PackageManager.PERMISSION_GRANTED;
        }

        private void requestForSpecificPermission(String[] permissions) {
            ActivityCompat.requestPermissions(this, permissions, 101);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case 101:
                    boolean allPermissionsGranted = false;
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        } else {
                            Toast.makeText(Splash.this, "Bez oprávnění aplikace nebude fungovat!", Toast.LENGTH_LONG).show();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            requestForSpecificPermission(permissions);
                            return;
                        }
                    }
                    Log.d("Permissions", "starting main activity");
                    Intent mainActivity = new Intent(this, MainActivity.class);
                    startActivity(mainActivity);
                    Splash.this.finish();
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
}
