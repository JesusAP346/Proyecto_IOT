package com.example.proyecto_iot.taxista.qr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.proyecto_iot.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.CaptureActivity;

public class CustomCaptureActivity extends CaptureActivity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private boolean flashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_capture); // ← usa el layout correcto

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        Button btnFlash = findViewById(R.id.btnFlash);


        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        btnFlash.setOnClickListener(v -> {
            flashOn = !flashOn;
            if (flashOn) {
                barcodeScannerView.setTorchOn();
                btnFlash.setText("Apagar Linterna ");
            } else {
                barcodeScannerView.setTorchOff();
                btnFlash.setText("Encender Linterna ");
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
