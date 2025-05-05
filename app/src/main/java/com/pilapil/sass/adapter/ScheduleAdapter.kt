package com.pilapil.sass.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pilapil.sass.databinding.ScheduleItemBinding

class ScheduleAdapter(
    private val onSlotClick: (String) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private val availableSlots = mutableListOf<String>()
    private val allSlots = listOf(
        "08:00", "09:00", "10:00", "11:00",
        "13:00", "14:00", "15:00", "16:00"
    )

    fun submitList(newAvailableSlots: List<String>) {
        availableSlots.clear()
        availableSlots.addAll(newAvailableSlots)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ScheduleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val slotTime = allSlots[position]
        val isAvailable = availableSlots.contains(slotTime)
        holder.bind(slotTime, isAvailable)
    }

    override fun getItemCount(): Int = allSlots.size

    inner class ScheduleViewHolder(private val binding: ScheduleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(time: String, isAvailable: Boolean) {
            binding.txtTimeStart.text = time
            binding.txtAvailabilityStatus.text = if (isAvailable) "Available" else "Not Available"
            binding.txtAvailabilityStatus.setTextColor(
                binding.root.context.getColor(
                    if (isAvailable) android.R.color.holo_green_dark else android.R.color.holo_red_dark
                )
            )

            binding.root.setOnClickListener {
                if (isAvailable) {
                    onSlotClick.invoke(time)
                }
            }
        }
    }
}
