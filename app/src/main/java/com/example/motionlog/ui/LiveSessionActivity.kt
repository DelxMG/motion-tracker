package com.example.motionlog.ui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.motionlog.R
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Activity que gestiona una sesión de actividad en tiempo real usando el acelerómetro del dispositivo.
 */

class LiveSessionActivity : AppCompatActivity(), SensorEventListener {

    // UI
    private lateinit var ivMotionIcon: ImageView
    private lateinit var tvMotionStatus: TextView
    private lateinit var chronometer: Chronometer
    private lateinit var btnStartPause: Button
    private lateinit var btnEndSession: Button

    // SENSOR
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // ESTADO
    private var isTracking = false

    // CRONÓMETRO
    private var pauseOffset: Long = 0
    private var isChronometerRunning = false

    // ACELERACIÓN
    private var lastAcceleration = SensorManager.GRAVITY_EARTH
    private var currentAcceleration = SensorManager.GRAVITY_EARTH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_session)

        // Enlace con la UI
        ivMotionIcon = findViewById(R.id.ivMotionIcon)
        tvMotionStatus = findViewById(R.id.tvMotionStatus)
        chronometer = findViewById(R.id.chronometer)
        btnStartPause = findViewById(R.id.btnStartPause)
        btnEndSession = findViewById(R.id.btnEndSession)

        // Obtener el SensorManager y el acelerómetro
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // // Si el dispositivo no tiene acelerómetro, se desactiva la funcionalidad
        if (accelerometer == null) {
            tvMotionStatus.text = "No accelerometer available"
            btnStartPause.isEnabled = false
            Toast.makeText(this, "This device has no accelerometer", Toast.LENGTH_SHORT).show()
        }

        // Formato manual del cronómetro (HH:MM:SS)
        chronometer.setOnChronometerTickListener {
            val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base
            val totalSeconds = elapsedMillis / 1000
            val seconds = totalSeconds % 60
            val minutes = (totalSeconds / 60) % 60
            val hours = totalSeconds / 3600

            chronometer.text =
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

        // Botón Start / Pause
        btnStartPause.setOnClickListener {
            if (!isTracking) startTracking() else pauseTracking()
        }

        // Botón Finish
        btnEndSession.setOnClickListener {
            finishSession()
        }
    }

    // Inicia la sesión
    private fun startTracking() {
        isTracking = true

        // Activamos la escucha del acelerómetro
        accelerometer?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        // Arrancamos o reanudamos el cronómetro
        if (!isChronometerRunning) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            isChronometerRunning = true
        }

        btnStartPause.text = "Pause"
        btnEndSession.visibility = View.VISIBLE
        tvMotionStatus.text = "Tracking started"

        Toast.makeText(this, "Session started", Toast.LENGTH_SHORT).show()
    }

    // Pausa la sesión
    private fun pauseTracking() {
        isTracking = false

        // Desregistramos el sensor para ahorrar batería
        sensorManager.unregisterListener(this)

        // Guardamos el tiempo transcurrido
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
        chronometer.stop()
        isChronometerRunning = false

        btnStartPause.text = "Start"
        tvMotionStatus.text = "Paused"

        Toast.makeText(this, "Session paused", Toast.LENGTH_SHORT).show()
    }

    // Finaliza la sesión

    private fun finishSession() {
        sensorManager.unregisterListener(this)
        isTracking = false
        chronometer.stop()

        // Duración total en minutos
        val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base
        val durationMinutes = (elapsedMillis / 60000).toInt()

        showSummaryDialog(durationMinutes)
    }

    // Callback del acelerómetro que se ejecuta cuando este detecta cambios

    override fun onSensorChanged(event: SensorEvent) {
        if (!isTracking) return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        // Valores del acelerómetro en los ejes X, Y, Z
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Calculo de la aceleración sqrt(x² + y² + z²)
        lastAcceleration = currentAcceleration
        currentAcceleration = sqrt(x * x + y * y + z * z)

        // Intensidad: diferencia aceleración actual y anterior
        val intensity = abs(currentAcceleration - lastAcceleration)

        // Clasificación del movimiento según la intensidad
        when {
            intensity < 0.5f -> {
                tvMotionStatus.text = "No movement"
                ivMotionIcon.setImageResource(R.drawable.ic_standing)
            }
            intensity < 1.5f -> {
                tvMotionStatus.text = "Walking"
                ivMotionIcon.setImageResource(R.drawable.ic_walk)
            }
            intensity < 3f -> {
                tvMotionStatus.text = "Running"
                ivMotionIcon.setImageResource(R.drawable.ic_run)
            }
            else -> {
                tvMotionStatus.text = "Intense movement"
                ivMotionIcon.setImageResource(R.drawable.ic_bike)
            }
        }
    }

    // Muestra un resumen final de la sesión

    private fun showSummaryDialog(durationMinutes: Int) {

        val message = """
            Duration: $durationMinutes min
            Session finished
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Session summary")
            .setMessage(message)
            .setIcon(R.drawable.ic_activity_default)

            //Se ejecuta cuando el usuario pulsa "Close".
            .setPositiveButton("Close") { _, _ ->
                Toast.makeText(this, "Session finished", Toast.LENGTH_SHORT).show()
                finish() // Cerramos la Activity al cerrar el diálogo
            }
            .setCancelable(false) // Impide que el usuario cierre el diálogo tocando fuera
            .show() // Muestra el diálogo
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Si la Activity se pausa, se libera el sensor
    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
    }
}
