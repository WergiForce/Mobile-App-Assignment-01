package org.wit.geosite.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.wit.geosite.databinding.CardGeositeBinding
import org.wit.geosite.models.GeositeModel

interface GeositeListener {
    fun onGeositeClick(geosite: GeositeModel)
}

class GeositeAdapter constructor(private var geosites: List<GeositeModel>,
                                   private val listener: GeositeListener) :
    RecyclerView.Adapter<GeositeAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardGeositeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val geosite = geosites[holder.adapterPosition]
        holder.bind(geosite, listener)
    }

    override fun getItemCount(): Int = geosites.size

    class MainHolder(private val binding : CardGeositeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(geosite: GeositeModel, listener: GeositeListener) {
            binding.geositeTitle.text = geosite.title
            binding.description.text = geosite.description
            Picasso.get().load(geosite.image).resize(200,200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onGeositeClick(geosite) }
        }
    }
}