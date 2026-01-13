package com.example.motionlog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.motionlog.R
import com.example.motionlog.model.ActivitySession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter del RecyclerView que muestra las sesiones de actividad.
 * Se encarga de:
 * - Crear las filas (ViewHolder)
 * - Vincular los datos con la UI
 */
class ActivitySessionAdapter(
    private val sessions: MutableList<ActivitySession>
) : RecyclerView.Adapter<ActivitySessionAdapter.ActivitySessionViewHolder>() {

    // Formateador de fecha reutilizable
    private val dateFormatter =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())


    // ViewHolder que mantiene referencias a las vistas de una fila
    class ActivitySessionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val ivIcon: ImageView = itemView.findViewById(R.id.ivActivityIcon)
        val tvName: TextView = itemView.findViewById(R.id.tvActivityName)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
    }


    //Se llama cuando RecyclerView necesita crear una nueva fila

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitySessionViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity_session, parent, false)

        return ActivitySessionViewHolder(view)
    }


    //Se llama para rellenar una fila con los datos correspondientes

    override fun onBindViewHolder(holder: ActivitySessionViewHolder, position: Int) {

        val session = sessions[position]


        holder.ivIcon.setImageResource(session.iconRes)
        holder.tvName.text = session.name
        holder.tvDuration.text = "${session.durationMinutes} min"

        // Fecha y hora formateadas a partir del timestamp
        holder.tvDateTime.text =
            dateFormatter.format(Date(session.timestamp))
    }

    //Devuelve el número de elementos en la lista
    override fun getItemCount(): Int = sessions.size


    //Añade una nueva sesión al inicio de la lista y notifica al RecyclerView
    fun addSession(session: ActivitySession) {
        sessions.add(0, session)
        notifyItemInserted(0)
    }
}