package com.example.motionlog.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.motionlog.R
import com.example.motionlog.adapter.ActivitySessionAdapter
import com.example.motionlog.model.ActivitySession

/**
 * Pantalla de registro manual de actividades.
 * Permite:
 * - Introducir nombre y duración
 * - Validar datos
 * - Mostrar sesiones en un RecyclerView
 */
class ActivityLogActivity : AppCompatActivity() {

    // UI
    private lateinit var etActivityName: EditText
    private lateinit var etDuration: EditText
    private lateinit var btnSaveActivity: Button
    private lateinit var rvActivities: RecyclerView

    // LISTA DE SESIONES REGISTRADAS
    private val sessions = mutableListOf<ActivitySession>()

    // ADAPTER
    private lateinit var adapter: ActivitySessionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity_log)

        // Enlace con la UI
        etActivityName = findViewById(R.id.etActivityName)
        etDuration = findViewById(R.id.etDuration)
        btnSaveActivity = findViewById(R.id.btnSaveActivity)
        rvActivities = findViewById(R.id.rvActivities)

        // Configuración del RecyclerView
        adapter = ActivitySessionAdapter(sessions)
        rvActivities.layoutManager = LinearLayoutManager(this)
        rvActivities.adapter = adapter

        // Acción del botón Guardar
        btnSaveActivity.setOnClickListener {

            val name = etActivityName.text.toString().trim()
            val durationText = etDuration.text.toString().trim()
            val duration = durationText.toIntOrNull()

            // Validación de datos
            if (name.isEmpty() || duration == null) {
                etActivityName.error = if (name.isEmpty()) "Required" else null
                etDuration.error = if (duration == null) "Invalid number" else null
                return@setOnClickListener
            }

            // Crear la sesión
            val session = ActivitySession(
                name = name,
                durationMinutes = duration,
                timestamp = System.currentTimeMillis(),
                iconRes = getIconForActivity(name)
            )

            // Añadir al inicio de la lista
            sessions.add(0, session)
            adapter.notifyItemInserted(0)

            // Scroll automático para ver el nuevo item
            rvActivities.scrollToPosition(0)

            // Limpiar campos
            etActivityName.text.clear()
            etDuration.text.clear()
        }
    }


    // Devuelve un icono en función del nombre de la actividad.
    private fun getIconForActivity(name: String): Int {

        val text = name.trim().lowercase()

        return when (text) {
            "correr", "running", "run" -> R.drawable.ic_run
            "andar", "caminar", "walk", "cinta", "cinta de andar" -> R.drawable.ic_walk
            "bicicleta", "bici", "ciclismo", "spinning",
            "eliptica", "elíptica" -> R.drawable.ic_bike
            "gimnasio", "gym", "weightlifting", "lift",
            "musculacion", "musculación" -> R.drawable.ic_gym
            "yoga" -> R.drawable.ic_yoga
            "senderismo", "hiking", "trekking" -> R.drawable.ic_hiking
            "tenis", "padel", "pádel", "badminton", "squash" -> R.drawable.ic_racket
            else -> R.drawable.ic_activity_default
        }
    }
}