package com.fsvdevs.agrosphere.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class ActuatorData(
    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
    var timestamp: Timestamp = Timestamp.now(),

    @get:PropertyName("fan_status") @set:PropertyName("fan_status")
    var fanStatus: Boolean = false,

    @get:PropertyName("fog_status") @set:PropertyName("fog_status")
    var fogStatus: Boolean = false,

    @get:PropertyName("vent_status") @set:PropertyName("vent_status")
    var ventStatus: Boolean = false
)