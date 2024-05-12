package com.example.hardware

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //ubicacion
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION))
        //QR
        var codigoQrLeido = findViewById<TextView>(R.id.textView)
        // Register the launcher and result handler
        val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Lectura cancelada", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG)
                    .show()
                codigoQrLeido.text = result.contents.toString()
            }
        }

        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Lector de códigos QR")
        options.setCameraId(0) // Use a specific camera of the device

        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        barcodeLauncher.launch(options)

        val btnLeerQR = findViewById<Button>(R.id.button)
        btnLeerQR.setOnClickListener {
            barcodeLauncher.launch(options)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val btnLeerUbi = findViewById<Button>(R.id.button2)
        val ubicacion = findViewById<TextView>(R.id.textView)
        btnLeerUbi.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
                locationPermissionRequest.launch(arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION))
            }
            else{
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        if(location != null){
                            ubicacion.text = location.latitude.toString()+" "+ location.longitude.toString()
                        }
                    }
            }

        }
    }
}