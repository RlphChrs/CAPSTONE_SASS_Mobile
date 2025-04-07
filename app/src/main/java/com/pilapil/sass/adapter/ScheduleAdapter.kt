package com.pilapil.sass.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pilapil.sass.databinding.ScheduleItemBinding

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private val slots = mutableListOf<String>()

    fun submitList(newSlots: List<String>) {
        slots.clear()
        slots.addAll(newSlots)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ScheduleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val time = slots[position]
        holder.bind(time)
    }

    override fun getItemCount(): Int = slots.size

    inner class ScheduleViewHolder(private val binding: ScheduleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(time: String) {
            binding.txtTimeStart.text = time
            binding.txtTimeEnd.text = "${time.dropLast(2)}${time.takeLast(2)} + 1hr"
            binding.txtMeetingTitle.text = "Available Slot"
            binding.txtMeetingTime.text = time
        }
    }
}
