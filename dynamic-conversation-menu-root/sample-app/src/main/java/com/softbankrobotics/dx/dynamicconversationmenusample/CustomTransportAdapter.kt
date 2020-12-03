package com.softbankrobotics.dx.dynamicconversationmenusample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomTransportAdapter(context: Context, users: ArrayList<Transport>) :
    ArrayAdapter<Transport>(
        context,
        0,
        users
    ) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Check if an existing view is being reused, otherwise inflate the view
        var rowView = convertView
        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.item_transport, parent, false)
        }

        // Get the data item for this position
        val bus = getItem(position)

        // Populate the data into the template view using the data object
        val nameTextView = rowView!!.findViewById<TextView>(R.id.name)
        val priceTextView = rowView.findViewById<TextView>(R.id.price)
        val destinationTextView = rowView.findViewById<TextView>(R.id.destination)
        val nextTextView = rowView.findViewById<TextView>(R.id.next)
        nameTextView.text = bus!!.name
        priceTextView.text = bus.price
        destinationTextView.text = bus.destination
        nextTextView.text = bus.next

        // Return the completed view to render on screen
        return rowView
    }
}
