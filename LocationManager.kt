package com.uttaranand.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat

import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import java.util.concurrent.TimeUnit


class LocationManager(
    val context: Context,
    val onRequestLocationProvidersCallback: (pendingIntent: PendingIntent) -> Unit,
    val onRequestLocationPermissions: () -> Unit,
    val onLocationResultUpdate: (Location?) -> Unit,
) {



    private var fusedLocationProviderClient : FusedLocationProviderClient? = null
    private var locationManager : LocationManager? = null
    private var locationRequest : LocationRequest? = null

    var currentLocationn : Location? = null


    private fun checkLocationPermissions() : Boolean{
        var isAnyLocationGranted = (ActivityCompat.checkSelfPermission(context, LocationConstants.LOCATION_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(context,LocationConstants.LOCATION_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED)
        return isAnyLocationGranted
    }

    @SuppressLint("MissingPermission")
    fun initLocationSDK(){

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,LocationConstants.locationRequestInterval).build()

        locationRequest?.let {
          //  fusedLocationProviderClient?.requestLocationUpdates(it,locationCallback, Looper.getMainLooper())
        }

        fetchLocation()
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation(){

        if(!checkLocationPermissions()){
            onRequestLocationPermissions()
        }else{
            setCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun setCurrentLocation(){

        if(checkLocationProviders()){
            Log.d("fvkbnkjfnvf","getCurrentLocation IF")
            fusedLocationProviderClient?.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,null)?.addOnSuccessListener {nLocation ->

//                setAndStoreNewLocation(nLocation)

            }?.addOnFailureListener {
                null
            }
        }else{
            Log.d("fvkbnkjfnvf","getCurrentLocation ELSE")
            requestLocationProviders()

        }
    }

    fun checkLocationProviders(): Boolean {

        val isGpsProvider = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkProvider = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        Log.d("flbmkfmvf",isGpsProvider.toString())
        Log.d("flbmkfmvf",isNetworkProvider.toString())

        return isGpsProvider == true || isNetworkProvider == true

    }



    private fun requestLocationProviders(){

        Log.d("fvkbnkjfnvf","providers")

        val locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!).build()

        val settingClient = LocationServices.getSettingsClient(context)
        val locationSettingTask = settingClient.checkLocationSettings(locationSettingsRequest)

        locationSettingTask
            .addOnSuccessListener {
                setCurrentLocation()
                Log.d("fvkbnkjfnvf","addOnSuccessListener")
            }
            .addOnFailureListener { exception ->
                if(exception is ResolvableApiException){
                    try{
                        Log.d("fvkbnkjfnvf","addOnFailureListener")

                        val resolution = exception.resolution

                        onRequestLocationProvidersCallback(resolution)

//                        r.launch(IntentSenderRequest.Builder(exception.resolution).build())
                    }catch (e:Exception){
                        Log.d("fvkbnkjfnvf","addOnFailureListener EXCEPTION")

                    }
                }
            }
    }

    fun onRequestProviders(){
        Log.d("flvnfjkvnfv",checkLocationProviders().toString() + "    locationProviders")
        setCurrentLocation()
    }

    fun onLocationRequestPermissions(){
        Log.d("flvnfjkvnfv",checkLocationProviders().toString() + "    onLocationRequestPermissions ")

        setCurrentLocation()

    }



    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult : LocationResult) {
            super.onLocationResult(locationResult)

            setAndStoreNewLocation(locationResult.lastLocation)

        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }
    }

    fun calculateDistance(newLocation : Location?, oldLocation : Location?): Float? {

        val distance = oldLocation?.let { newLocation?.distanceTo(it) }
        return distance?.div(1000)
    }


    /**Set and Store New Location Only If distance between new and old-locally-stored location is specified KM   */
    fun setAndStoreNewLocation(newLoc : Location?){
      /*  //Fetched old location stored locally.
        val oldLocationStr = appPreferences.getString(LocationConstants.LOCATION)
        val oldLocationModel = oldLocationStr.fromJson<LocationModel>()

        val newLocation = newLoc

        //converting locationModel to android.Location object.
        val oldLocation = Location("")
        oldLocation.latitude = oldLocationModel?.latitude ?: 0.0
        oldLocation.longitude = oldLocationModel?.longitude ?: 0.0

        //Calculating distance between old and new current location.
        val distanceKM = calculateDistance(newLocation,oldLocation ) ?: 0f

        *//*If distance between old and new current location is  >= 10 km then set old  *//*
        if(distanceKM >= LocationConstants.LOCATION_DISTANCE_KM) {
            saveLocation(newLocation)
        }

        currentLocationn = newLocation
        onLocationResultUpdate(currentLocationn)

*/
    }

}

object LocationConstants{
    val LOCATION_PERMISSIONS = listOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
    var locationRequestInterval = TimeUnit.SECONDS.toMillis(10)

    const val LOCATION_PROVIDER_REQUEST_CODE = 52
    const val LOCATION = "location"

    const val LOCATION_DISTANCE_KM = 5
}