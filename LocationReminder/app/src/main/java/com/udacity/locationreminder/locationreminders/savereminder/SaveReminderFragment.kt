package com.udacity.locationreminder.locationreminders.savereminder

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.udacity.locationreminder.R
import com.udacity.locationreminder.base.BaseFragment
import com.udacity.locationreminder.base.NavigationCommand
import com.udacity.locationreminder.databinding.FragmentSaveReminderBinding
import com.udacity.locationreminder.locationreminders.geofence.GeofenceHelper
import com.udacity.locationreminder.locationreminders.reminderslist.ReminderDataItem
import com.udacity.locationreminder.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SaveReminderFragment : BaseFragment() {
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    private lateinit var geofencingClient: GeofencingClient
    private val GEOFENCE_RADIUS = 500f
    private lateinit var reminderData: ReminderDataItem
    private lateinit var geofenceHelper: GeofenceHelper

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        geofencingClient = LocationServices.getGeofencingClient(requireContext())
        geofenceHelper = GeofenceHelper(context)

        binding.selectLocation.setOnClickListener {
            _viewModel.navigationCommand.value =
                    NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value
            val geofenceId = UUID.randomUUID().toString()

            if (latitude != null && longitude != null && !TextUtils.isEmpty(title))
                addGeofence(LatLng(latitude, longitude), GEOFENCE_RADIUS, geofenceId)

            _viewModel.validateAndSaveReminder(ReminderDataItem(title,description,location, latitude,longitude))

            _viewModel.navigateToReminderList.observe(viewLifecycleOwner) {
                if (it) {
                    view.findNavController()
                        .navigate(R.id.action_saveReminderFragment_to_reminderListFragment)
                    _viewModel.navigateToReminderList()
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewModel.onClear()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun addGeofence(
            latLng: LatLng,
            radius: Float,
            geofenceId: String) {
        val geofence: Geofence = geofenceHelper.getGeofence(
                geofenceId,
                latLng,
                radius,
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest: GeofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent: PendingIntent? = geofenceHelper.getGeofencePendingIntent()
        geofencingClient.addGeofences(geofencingRequest, pendingIntent!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Geofence Added")
                }
            .addOnFailureListener { e ->
                    val errorMessage: String = geofenceHelper.getErrorString(e)
                    Toast.makeText(
                        context,
                        "Please give background location permission",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(TAG, "fail in creating geofence: $errorMessage")
                }
    }

}
private const val TAG = "SaveReminderFragment"