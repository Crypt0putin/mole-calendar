package com.molecalendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MoleAdapter(
    private var moles: List<Mole>,
    private val onMoleClick: (Mole) -> Unit
) : RecyclerView.Adapter<MoleAdapter.MoleViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class MoleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moleImage: ImageView = itemView.findViewById(R.id.moleImage)
        val moleLocation: TextView = itemView.findViewById(R.id.moleLocation)
        val moleDescription: TextView = itemView.findViewById(R.id.moleDescription)
        val moleSize: TextView = itemView.findViewById(R.id.moleSize)
        val nextCheckDate: TextView = itemView.findViewById(R.id.nextCheckDate)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMoleClick(moles[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mole, parent, false)
        return MoleViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoleViewHolder, position: Int) {
        val mole = moles[position]

        holder.moleLocation.text = mole.location
        holder.moleDescription.text = mole.description
        holder.moleSize.text = "Size: ${mole.size} mm"
        holder.nextCheckDate.text = "Next check: ${dateFormat.format(Date(mole.nextCheckDate))}"

        // Load image if available
        if (mole.photoPath != null && File(mole.photoPath).exists()) {
            Glide.with(holder.itemView.context)
                .load(mole.photoPath)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_camera)
                .into(holder.moleImage)
        } else {
            holder.moleImage.setImageResource(android.R.drawable.ic_menu_camera)
        }
    }

    override fun getItemCount() = moles.size

    fun updateMoles(newMoles: List<Mole>) {
        moles = newMoles
        notifyDataSetChanged()
    }
}
