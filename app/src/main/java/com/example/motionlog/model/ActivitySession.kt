package com.example.motionlog.model

// Modelo de datos que representa una sesión de actividad física.
data class ActivitySession(
    val name: String,
    val durationMinutes: Int,
    val timestamp: Long,
    val iconRes: Int
)