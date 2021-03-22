package com.originalstocks.projectloki.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.originalstocks.projectloki.R
import com.originalstocks.projectloki.data.db.LocationsModel
import com.originalstocks.projectloki.data.helpers.*
import com.originalstocks.projectloki.databinding.FragmentHomeBinding
import com.originalstocks.projectloki.ui.adapter.LocationsPagedAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback {
    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var parentSheetBehaviour: BottomSheetBehavior<LinearLayout>
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        parentSheetBehaviour = BottomSheetBehavior.from(binding.bottomSheetView)
        parentSheetBehaviour.peekHeight = peekHeight
        parentSheetBehaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        parentSheetBehaviour.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // Fully expanded
                        binding.recentLocationsRecyclerView.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        parentSheetBehaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        binding.recentLocationsRecyclerView.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }
        })

        /** Not working, Google APIs got paid somehow.*/
         /*val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
         autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
         autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
             override fun onPlaceSelected(place: Place) {
                 Log.i(TAG, "SelectedPlace:name = ${place.name} id: ${place.id} latLng: ${place.latLng}")
                 binding.locationEditText.setOnEditorActionListener { _, actionId, _ ->
                     if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                         val locationText = binding.locationEditText.text.toString()
                         if (locationText.isNotEmpty()) {
                             val noteModel = LocationsModel(title = place.name.toString(), locationLatLng = place.latLng.toString())
                             viewModel.saveThisData(requireActivity(), noteModel)
                         } else {
                             showSnackBar(binding.rootView, "Please enter Location...")
                         }
                         true
                     } else false
                 }

             }

             override fun onError(status: com.google.android.gms.common.api.Status) {
                 Log.e(TAG, "An error occurred: $status")
             }
         })*/


        val pagedAdapter = LocationsPagedAdapter(requireActivity())
        binding.recentLocationsRecyclerView.adapter = pagedAdapter

        binding.removeAllDataButton.setOnClickListener {
            viewModel.deleteAllData(requireActivity())
        }
        lifecycleScope.launch {
            viewModel.locationsFlowData.collectLatest { data ->
                pagedAdapter.submitData(data)
            }
        }


        /** Observers */
        viewModel.dbStatusLiveData.observe(viewLifecycleOwner, Observer { status ->
            when (status!!) {
                Status.INIT -> {
                    Log.i(TAG, "onActivityCreated_observe status = INIT")
                }
                Status.SAVING -> {
                    Log.i(TAG, "onActivityCreated_observe status = SAVING")
                }
                Status.DATA_SAVED -> {
                    Log.i(TAG, "onActivityCreated_observe status = DATA_SAVED")
                    showToast(requireContext(), "Location saved...")
                    lifecycleScope.launch {
                        viewModel.locationsFlowData.collectLatest { data ->
                            pagedAdapter.submitData(data)
                        }
                    }
                }
                Status.ERROR -> {
                    Log.i(TAG, "onActivityCreated_observe status = ERROR")
                }
                else -> {
                    Log.i(TAG, "onActivityCreated_observe status = ERROR")
                }
            }
        })

        viewModel.dbStatusDelete.observe(viewLifecycleOwner, Observer { status ->
            when (status!!) {
                Status.INIT -> {
                    Log.i(TAG, "onActivityCreated_observe status = INIT")
                }
                Status.DELETING -> {
                    Log.i(TAG, "onActivityCreated_observe status = DELETING")
                }
                Status.DELETED -> {
                    Log.i(TAG, "onActivityCreated_observe status = DELETED")
                    showToast(requireContext(), "Data cleared !")
                    lifecycleScope.launch {
                        viewModel.locationsFlowData.collectLatest { data ->
                            pagedAdapter.submitData(data)
                        }
                    }
                }
                Status.ERROR -> {
                    Log.i(TAG, "onActivityCreated_observe status = ERROR")
                }
                else -> {
                    Log.i(TAG, "onActivityCreated_observe status = ERROR")
                }
            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.home_map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        var latLng: LatLng? = null
        val mLocationListener = LocationListener { location ->
            val currentLocation = getAddress(location.longitude, location.latitude)

            // setting up data on MAp
            latLng = LatLng(location.latitude, location.longitude)
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            //mMap.addMarker(MarkerOptions().position(latLng).title("Currently you're here in $currentLocation"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.isMyLocationEnabled = true

            Log.i(TAG, "onViewCreated_fetchLocationAccordingToLatLong = $currentLocation")
        }

        binding.locationEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val locationText = binding.locationEditText.text.toString()
                if (locationText.isNotEmpty()) {

                    //viewModel.getQueriedLocation("Lucknow, U.P", "Bhopal, M.P")

                    val noteModel = LocationsModel(title = locationText, locationLatLng = latLng.toString())
                    viewModel.saveThisData(requireActivity(), noteModel)
                } else {
                    showSnackBar(binding.rootView, "Please enter Location...")
                }
                true
            } else false
        }

        checkPermission()
        /*Here we can use LocationManager.GPS_PROVIDER has it gives more Accurate location pointers but it consumes more battery hence using LocationManager.NETWORK_PROVIDER*/
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0F,
            mLocationListener
        )
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.e(TAG, "checkPermission_permission already granted")
                // Fetch the location
                val mLocationListener = LocationListener { location ->
                    val currentLocation = getAddress(location.longitude, location.latitude)
                    Log.i(TAG, "onViewCreated_fetchLocationAccordingToLatLong = $currentLocation")
                }

                /*Here we can use LocationManager.GPS_PROVIDER has it gives more Accurate location pointers but it consumes more battery hence using LocationManager.NETWORK_PROVIDER*/
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0F,
                    mLocationListener
                )

            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) -> {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_PERMISSION_LOCATION
                )
                // We've been denied once before. Explain why we need the permission, then ask again.
                Log.i(TAG, "checkPermission_permission once denied, asking permission again")
            }
            else -> {
                // We've never asked. Just do it.
                Log.i(TAG, "checkPermission_permission asking permission")
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_PERMISSION_LOCATION
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSION_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted now fetch the locations

                    // Fetch the location
                    val mLocationListener = LocationListener { location ->
                        Log.i(
                            TAG,
                            "onLocationChanged_location lat = ${location.latitude} long = ${location.longitude}"
                        )
                        val currentLocation = getAddress(location.longitude, location.latitude)
                        Log.i(
                            TAG,
                            "onViewCreated_fetchLocationAccordingToLatLong = $currentLocation"
                        )
                    }

                    /*Here we can use LocationManager.GPS_PROVIDER has it gives more Accurate location pointers but it consumes more battery hence using LocationManager.NETWORK_PROVIDER*/
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        0F,
                        mLocationListener
                    )
                    Log.i(TAG, "checkPermission_permission onRequestPermissionsResult granted")
                } else {
                    Log.i(TAG, "checkPermission_permission onRequestPermissionsResult denied")
                }
                return
            }
        }
    }

    private fun getAddress(longitude: Double, latitude: Double): String {
        val result = StringBuilder()
        try {
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                result.append(address.locality).append(", ")
                result.append(address.countryName)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result.toString()
    }


}