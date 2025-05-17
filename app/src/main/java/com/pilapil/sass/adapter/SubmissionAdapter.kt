package com.example.sass.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sass.model.SubmissionFile
import com.pilapil.sass.R

class SubmissionAdapter(
    private val onRemove: (SubmissionFile) -> Unit
) : RecyclerView.Adapter<SubmissionAdapter.ViewHolder>() {

    private val files = mutableListOf<SubmissionFile>()

    fun updateFiles(newFiles: List<SubmissionFile>) {
        files.clear()
        files.addAll(newFiles)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.submission_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        holder.tvFileName.text = file.name
        holder.btnRemove.setOnClickListener { onRemove(file) }
    }

    override fun getItemCount(): Int = files.size
}
