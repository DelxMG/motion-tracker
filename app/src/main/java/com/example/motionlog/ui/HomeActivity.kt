package com.example.motionlog.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.motionlog.R

/**
 * Pantalla principal de la aplicación.
 * Desde aquí el usuario puede:
 * - Registrar actividades manualmente
 * - Iniciar una sesión en tiempo real con sensores
 */
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Referencias a los botones del layout
        val btnOpenActivityLog: Button = findViewById(R.id.btnOpenActivityLog)
        val btnStartLiveSession: Button = findViewById(R.id.btnStartLiveSession)

        // Navega a la pantalla de registro manual de actividades
        btnOpenActivityLog.setOnClickListener {
            val intent = Intent(this, ActivityLogActivity::class.java)
            startActivity(intent)
        }

        // Navega a la pantalla de sesión en tiempo real
        btnStartLiveSession.setOnClickListener {
            val intent = Intent(this, LiveSessionActivity::class.java)
            startActivity(intent)
        }
    }
}
