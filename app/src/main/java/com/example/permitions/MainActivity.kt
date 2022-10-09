package com.example.permitions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.permitions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    private val feature1PermissionRequestLauncher = registerForActivityResult(
        RequestPermission(),
        ::onGotPermissionsResultForFeature1
    )

    private val feature2PermissionRequestLauncher = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotPermissionsResultForFeature2
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.requestCameraPermissionButton.setOnClickListener {
            feature1PermissionRequestLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.requestRecordAudioAndLocationPermissionsButton.setOnClickListener {
            feature2PermissionRequestLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onGotPermissionsResultForFeature1(granted: Boolean) {
        if (granted) {
            onCameraPermissionGranted()
        } else {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                askUserForOpeningAppSettings()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onGotPermissionsResultForFeature2(grantedResults: Map<String, Boolean>) {
        if (grantedResults.entries.all { it.value }) {
            Toast.makeText(this, "Location and record audio permissions granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askUserForOpeningAppSettings() {
        val appSettingsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        if (packageManager.resolveActivity(appSettingsIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            Toast.makeText(this, "Permissions are denied forever", Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Permission denied")
                .setMessage("You have denied permissions forever. " +
                        "You can change your decision in app settings.\n\n" +
                        "Would you like to open app settings?")
                .setPositiveButton("Open") { _,_ ->
                    startActivity(appSettingsIntent)
                }
                .create()
                .show()
        }
    }

    private fun onCameraPermissionGranted() {
        Toast.makeText(this, "Camera permission is granted", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val RQ_PERMISSIONS_FOR_FEATURE_1_CODE = 1
        const val RQ_PERMISSIONS_FOR_FEATURE_2_CODE = 2
    }
}