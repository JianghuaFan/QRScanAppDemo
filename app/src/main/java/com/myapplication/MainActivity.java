package com.myapplication;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rokid.glass.instruct.InstructLifeManager;
import com.rokid.glass.instruct.entity.EntityKey;
import com.rokid.glass.instruct.entity.IInstructReceiver;
import com.rokid.glass.instruct.entity.InstructEntity;

import org.jetbrains.annotations.Nullable;

public class MainActivity extends AppCompatActivity {
    //    private static final int REQUEST_CODE_SCAN_INFO = 123;
    public int REQUEST_CODE_SCAN_INFO = 123;
    private InstructLifeManager mLifeManager;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        Button scanQRButton = (Button) findViewById(R.id.qrScan_btn);
        // If the button is clicked, the method setOnClickListener would be invoked to start the Quick Scan activity for scanning QR code,
        // If the button is red by people, the method configInstruct would be invoked.

        // Set click listener for scan button
        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the scanQRButton");
                startQRScan();
            }
        });
        configInstruct();
    }

    // If click the button call this method to start the Quick Scan activity for scanning QR code, If use the voice instruct call
    private void startQRScan() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.rokid.glass.scan2",
                "com.rokid.glass.scan2.activity.QrCodeActivity");
        intent.setComponent(comp);
        Log.d("Scan", "Start scanning QR Code");
        startActivityForResult(intent, REQUEST_CODE_SCAN_INFO);
    }

    // Handle the result of the QR scan activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultCode", String.valueOf(resultCode));
        if (requestCode == REQUEST_CODE_SCAN_INFO && resultCode == RESULT_OK) {
            String scannedResult = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d("scannedResult", scannedResult);
            if (scannedResult != null) {
                Log.d("Scan", "QR Code scanning completed");
                openDocument(scannedResult);
            }
        }
    }

    // Method to open a document based on the QR code content
    private void openDocument(String qrCodeContent) {
        ComponentName component = new ComponentName("com.rokid.glass.document2", "com.rokid.glass.document2.activity.MainActivity");
        Intent intent = new Intent();
        intent.setComponent(component);
        Log.d("ScannedResult", qrCodeContent);
        boolean isValidCase = true;
        String fileName = null;
        String folder = null;

        switch (qrCodeContent) {
            case "Video1":
                Log.d("Switch case1", qrCodeContent);
                fileName = "GEA Grasso Screw Compressor Product Animation-wqNTYLIDaxs-720p-1654246055390.mp4";
                folder = "/sdcard/Documents/Videos";
                break;
            case "Video2":
                fileName = "Gas Compressor Package.mp4";
                folder = "/sdcard/Documents/Videos";
                break;
            case "Video3":
                fileName = "Rolls-Royce  How Engines Work-JxkJ-FwFeVI-720p-1654417847380.mp4";
                folder = "/sdcard/Documents/Videos";
                break;
            case "Pdf1":
                fileName = "GEA Screw Compressor Package Brochure.pdf";
                folder = "/sdcard/Documents/Pdf";
                break;
            case "Pdf2":
                fileName = "grasso-screw-compressor-package-pi_tcm26-38504.pdf";
                folder = "/sdcard/Documents/Pdf";
                break;
            default:
                isValidCase = false;
                showToast("Invalid QR Code Content");
        }
        intent.putExtra("file_name", fileName);
        intent.putExtra("file_path_floder", folder);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (isValidCase) {
            startActivity(intent);
        }
    }

    private void showToast(String str) {
        LayoutInflater inflater = getLayoutInflater();
        View toastView = inflater.inflate(R.layout.toast_layout, findViewById(R.id.toast_layout));

        TextView toastText = toastView.findViewById(R.id.toast_text);
        toastText.setText(str);
        toastText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.toast_text_color)); // Set the font color programmatically

        Toast toast = new Toast(MainActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastView);
        toast.show();
    }


    public void configInstruct() {
        mLifeManager = new InstructLifeManager(this, getLifecycle(), mInstructLifeListener);
        mLifeManager.addInstructEntity(
                new InstructEntity()
                        .addEntityKey(R.id.qrScan_btn)
                        .addEntityKey(new EntityKey(EntityKey.Language.en, "start scan"))
                        .setShowTips(true)
                        .setCallback(new IInstructReceiver() {
                            @Override
                            public void onInstructReceive(Activity act, String key, InstructEntity instruct) {
                                Log.d(TAG, "掃描 觸發");
                                startQRScan();
                            }
                        })
        );
    }

    private InstructLifeManager.IInstructLifeListener mInstructLifeListener = new InstructLifeManager.IInstructLifeListener() {
        @Override
        public boolean onInterceptCommand(String command) {
            if ("需要拦截的指令".equals(command)) {
                return true;
            }
            return false;
        }

        @Override
        public void onTipsUiReady() {
            Log.d("AudioAi", "onTipsUiReady Call ");
        }

        @Override
        public void onHelpLayerShow(boolean show) {

        }
    };

}