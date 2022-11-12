package com.udacity.locationreminder.locationreminders.geofence


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.locationreminder.locationreminders.geofence.GeofenceTransitionsJobIntentService.Companion.enqueueWork



class GeofenceBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "GeofenceBroadcastReceiver"
    @SuppressLint("LongLogTag")
    override fun onReceive(context: Context, intent: Intent) {
        val geofenceEvent = GeofencingEvent.fromIntent(intent)
        if (geofenceEvent?.hasError()!!) {
            Log.d(TAG, "Error on receive !")
            return
        }

        when (geofenceEvent.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                enqueueWork(context, intent)
            }
        }

    }
}