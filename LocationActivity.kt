    private val locationManager : LocationManager by lazy {

        LocationManager(context = requireContext(),
            onRequestLocationProvidersCallback = { pendingIntent->
                requestLocationProviderLauncher.launch(IntentSenderRequest.Builder(pendingIntent).build())
            },
            onRequestLocationPermissions = {
                requestLocationPermissions.launch(LocationConstants.LOCATION_PERMISSIONS.toTypedArray())
            }
        ) { currentLocation ->

        }

    }

    fun initLocationSdk(){
        locationManager.initLocationSDK()
    }

    private var requestLocationProviderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){

        //If after accept or reject provider from location Provider dialogue then check for locationProvider and call function.
        if(locationManager.checkLocationProviders()) {
            locationManager.onRequestProviders()
        }
    }

    private var requestLocationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){map ->
        map.keys.forEach { Log.d("fk39493vbkfnvf",it.toString() ) }
        map.keys.forEach { Log.d("fk39493vbkfnvf",map.get(it).toString()) }

        //If Both or anyone permission is allowed from COARSE AND FINE LOCATION PERMISSIONS
        if(map[LocationConstants.LOCATION_PERMISSIONS[0]] == true || map[LocationConstants.LOCATION_PERMISSIONS[1]] == true ){
            locationManager.onLocationRequestPermissions()
        }

    }

