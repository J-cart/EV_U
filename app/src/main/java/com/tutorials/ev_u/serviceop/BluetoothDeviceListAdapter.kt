package com.tutorials.ev_u.serviceop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tutorials.bluetooth_one.serviceop.BlueDevice
import com.tutorials.ev_u.R
import com.tutorials.ev_u.databinding.DeviceAdapterViewholderBinding


class BluetoothDeviceListAdapter: ListAdapter<BlueDevice, BluetoothDeviceListAdapter.ViewHolder>(
    diffObject
) {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private  val binding = DeviceAdapterViewholderBinding.bind(view)
        fun bind(item: BlueDevice){
            binding.apply {
                deviceNameTv.text = "Name: "+item.device.name
                deviceAddressTv.text = "Address: "+item.device.address
                root.setOnClickListener {
                    adapterClickListener?.let { onClick -> onClick(item) }
                }
            }
        }
    }


    companion object {
        val diffObject = object : DiffUtil.ItemCallback<BlueDevice>() {
            override fun areItemsTheSame(oldItem: BlueDevice, newItem: BlueDevice): Boolean {
                return oldItem.device.address == newItem.device.address
            }
            override fun areContentsTheSame(oldItem: BlueDevice, newItem: BlueDevice): Boolean {
                return oldItem.device.address == newItem.device.address && oldItem.device.name == newItem.device.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.device_adapter_viewholder,parent,false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

    }

    private var adapterClickListener:((BlueDevice)->Unit)? = null

    fun adapterClick(onClickListener:(BlueDevice)->Unit){
        adapterClickListener = onClickListener
    }

}