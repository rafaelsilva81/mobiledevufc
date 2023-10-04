package com.rafaelsilva81.mobiledevufc.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.rafaelsilva81.mobiledevufc.R
import com.rafaelsilva81.mobiledevufc.databinding.ActivityMapsBinding
import com.rafaelsilva81.mobiledevufc.helpers.DialogHelper
import java.io.IOException
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var addrInput: TextInputEditText
    private lateinit var confirmBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        addrInput = binding.mapDetailedAddressInput
        confirmBtn = binding.mapConfirmAddressButton

        confirmBtn.setOnClickListener {
            val address = addrInput.text.toString()
            if (address.isNotEmpty()) {
                // Retornar para a tela anterior com o endereço para ser salvo no "addressInput"
                intent.putExtra("address", address)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                DialogHelper.showAlert(
                    this,
                    "Erro",
                    "Você precisa selecionar um endereço",
                )
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var loc = getUserLocation()

        if (loc != null) {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        loc.latitude,
                        loc.longitude
                    ), 15f
                )
            )
        }

        // Register the click listener for the map
        mMap.setOnMapClickListener(this)

    }

    private fun getUserLocation(): Location? {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // get location
        val location = if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        } else {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }

        return location;
    }

    override fun onMapClick(latLng: LatLng) {
        // Retrieve the address from latitude and longitude
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0].getAddressLine(0)
                    // Update the input field with the address
                    addrInput.setText(address)
                    // Update the map camera, and add a marker
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30f))
                    mMap.clear()
                    mMap.addMarker(
                        com.google.android.gms.maps.model.MarkerOptions().position(latLng)
                    )
                } else {
                    Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error retrieving address", Toast.LENGTH_SHORT).show()
        }
    }
}