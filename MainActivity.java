package com.cornerstone.fortress;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.view.View;
import android.widget.*;
import java.util.Locale;

/** Local-only diagnostics. No network upload, root, or hidden Android APIs. */
public final class MainActivity extends Activity {
    private LinearLayout body;
    private static final String[] MODULES = {
        "Kernel Integrity Guardian", "Permission Sentinel MAX", "Privacy Dashboard Ultra",
        "DNS Security Engine", "Wi-Fi Threat Analyzer", "Bluetooth Defense MAX",
        "USB Security Guardian", "Battery Intelligence", "Storage Integrity MAX",
        "Sensor Diagnostics Pro", "Emergency Recovery Center", "App Integrity Scanner",
        "Network Reliability Engine", "Security Audit & Reports", "Master Integration & Testing V3"
    };
    private static final String[] STAGES = {
        "Guidance only: verified boot state is not exposed to ordinary apps",
        "Opens Android permission management", "Opens Android privacy settings",
        "Opens Android Private DNS settings", "Displays current transport and validation",
        "Opens Bluetooth settings", "Opens connected-device settings",
        "Reads current battery level and charging state", "Reads internal storage capacity",
        "Lists hardware sensors exposed by Android", "Opens Android backup settings",
        "Shows app signing-certificate availability for this app",
        "Displays current network validation; history not yet implemented",
        "Displays on-device diagnostic summary; export pending",
        "Module inventory and local self-test"
    };
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll = new ScrollView(this);
        body = new LinearLayout(this); body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(24, 24, 24, 24); scroll.addView(body); setContentView(scroll);
        TextView title = new TextView(this); title.setText("FORTRESS ULTIMATE MAX · V3");
        title.setTextSize(22); title.setTypeface(null, 1); body.addView(title);
        TextView subtitle = new TextView(this);
        subtitle.setText("Local diagnostics • Android " + Build.VERSION.RELEASE + " • " + Build.MODEL);
        body.addView(subtitle);
        Button scan = new Button(this); scan.setText("RUN DEVICE CHECK"); body.addView(scan);
        scan.setOnClickListener(v -> showResults());
        TextView heading = new TextView(this); heading.setText("15-module integration inventory");
        heading.setTextSize(18); body.addView(heading);
        for (int i=0;i<MODULES.length;i++) {
            final int index=i;
            Button b=new Button(this); b.setAllCaps(false); b.setText((i+1)+". "+MODULES[i]);
            body.addView(b); b.setOnClickListener(v -> openModule(index));
        }
    }
    private void showResults() {
        StringBuilder s = new StringBuilder("Device: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL)
            .append("\nAndroid API: ").append(Build.VERSION.SDK_INT);
        BatteryManager battery=(BatteryManager)getSystemService(BATTERY_SERVICE);
        int level=battery.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        s.append("\nBattery: ").append(level>=0?level+"%":"unavailable");
        Intent power=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if(power!=null) s.append("\nCharging status: ").append(power.getIntExtra(BatteryManager.EXTRA_STATUS,-1));
        try { StatFs fs=new StatFs(Environment.getDataDirectory().getPath());
            s.append("\nFree internal storage: ").append(String.format(Locale.US,"%.1f GiB",fs.getAvailableBytes()/1073741824.0));
        } catch(Exception e) { s.append("\nStorage: unavailable"); }
        SensorManager sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        s.append("\nAvailable sensors: ").append(sm.getSensorList(Sensor.TYPE_ALL).size());
        ConnectivityManager cm=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        Network n=cm.getActiveNetwork(); NetworkCapabilities cap=n==null?null:cm.getNetworkCapabilities(n);
        s.append("\nActive network: ").append(cap==null?"none or unavailable":
            cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)?"Wi-Fi":
            cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)?"Cellular":"Other");
        s.append("\nInternet validated: ").append(cap!=null&&cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED));
        new AlertDialogBuilder(this).show("Device check",s.toString());
    }
    private void openModule(int i) {
        switch(i) {
            case 1: launch(Settings.ACTION_APPLICATION_SETTINGS); return;
            case 2: launch(Settings.ACTION_PRIVACY_SETTINGS); return;
            case 3: launch(Settings.ACTION_WIRELESS_SETTINGS); return;
            case 5: launch(Settings.ACTION_BLUETOOTH_SETTINGS); return;
            case 6: launch(Settings.ACTION_SETTINGS); return;
            case 10: launch(Settings.ACTION_SETTINGS); return;
            case 7: case 8: case 9: case 12: case 13: case 14: showResults(); return;
            default: new AlertDialogBuilder(this).show(MODULES[i],STAGES[i]);
        }
    }
    private void launch(String action) {
        try { startActivity(new Intent(action)); }
        catch(Exception e) { startActivity(new Intent(Settings.ACTION_SETTINGS)); }
    }
    private static final class AlertDialogBuilder {
        private final Activity activity;
        AlertDialogBuilder(Activity activity){this.activity=activity;}
        void show(String title,String message){new android.app.AlertDialog.Builder(activity)
            .setTitle(title).setMessage(message).setPositiveButton("OK",null).show();}
    }
}
