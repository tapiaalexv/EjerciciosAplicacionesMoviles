package com.tapiaalexis.myappgooglemapsapi

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tapiaalexis.myappgooglemapsapi.databinding.ActivityMapsBinding
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapa: GoogleMap
    private val EPNLatLong = LatLng(-0.21, -78.49)
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap
        with(mapa.uiSettings) {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMyLocationButtonEnabled = true
            isIndoorLevelPickerEnabled = true
            isMapToolbarEnabled = true
            isZoomGesturesEnabled = true
            isScrollGesturesEnabled = true
            isTiltGesturesEnabled = true
            isRotateGesturesEnabled = true
        }
        mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(EPNLatLong, 15.0f));
        mapa.addMarker(
            MarkerOptions()
                .position(EPNLatLong)
                .title("EPN")
                .snippet("Escuela Politécnica Nacional")
                .icon(
                    BitmapDescriptorFactory
                        .fromResource(android.R.drawable.star_big_on)
                )
                .anchor(0.5f, 0.5f)
        )
        mapa.setOnMapClickListener { puntoPulsado ->
            mapa.addMarker(
                MarkerOptions().position(puntoPulsado)
                    .icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                    )
            )
            try {
                val geoCoder = Geocoder(baseContext, Locale.getDefault())
                val addresses: List<Address> = geoCoder.getFromLocation(
                    puntoPulsado.latitude, puntoPulsado.longitude, 1
                )
                if (addresses.size > 0) {
                    val address = addresses[0].getAddressLine(0) + ", " + addresses[0].getLocality()
                    Toast.makeText(baseContext, address, Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
            } finally {
                // optional finally block
            }
        }
    }

    fun moveCamera(view: View?) {
        mapa.moveCamera(CameraUpdateFactory.newLatLng(EPNLatLong))
    }

    fun animateCamera(view: View?) {
        if (!isLocationPermissionGranted()) return
        moveToMyLocation()
    }

    @SuppressLint("MissingPermission")
    fun moveToMyLocation() {
        mapa.isMyLocationEnabled = true
        val service = getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = service.getBestProvider(Criteria(), false)
        val location = service.getLastKnownLocation(provider!!)
        var latitudLongitud = LatLng(37.3993, -122.079)//Silicon Valley
        if (location != null) {
            latitudLongitud = LatLng(location.latitude, location.longitude)
        }
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(latitudLongitud, 10f))
    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            // Precise location access granted.
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    // Only approximate location access granted.
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                moveToMyLocation()
            }
            else -> {
                // No location access granted.
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            false
        } else {
            true
        }
    }

    fun tipoMapa(view: View?) {
        var likelyPlaceNames = arrayOf<String>(
            "MAP_TYPE_NONE",
            "MAP_TYPE_NORMAL",
            "MAP_TYPE_SATELLITE",
            "MAP_TYPE_TERRAIN",
            "MAP_TYPE_HYBRID"
        )
        val listener = DialogInterface.OnClickListener { dialog, which ->
            // The "which" argument contains the position of the selected item.
            mapa.setMapType(which)
        }
        // Display the dialog.
        AlertDialog.Builder(this)
            .setTitle("Escoja un tipo de mapa:")
            .setItems(likelyPlaceNames, listener)
            .show()
    }

}


