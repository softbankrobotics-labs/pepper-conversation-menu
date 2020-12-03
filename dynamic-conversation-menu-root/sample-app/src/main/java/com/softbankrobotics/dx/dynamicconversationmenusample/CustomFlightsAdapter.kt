package com.softbankrobotics.dx.dynamicconversationmenusample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomFlightsAdapter(context: Context, users: ArrayList<Flight>) : ArrayAdapter<Flight>(
    context,
    0,
    users
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Check if an existing view is being reused, otherwise inflate the view
        var rowView = convertView
        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.item_flight, parent, false)
        }

        // Get the data item for this position
        val flight = getItem(position)

        // Populate the data into the template view using the data object
        val terminalTextView = rowView!!.findViewById<TextView>(R.id.terminal)
        val numberTextView = rowView.findViewById<TextView>(R.id.number)
        val destinationTextView = rowView.findViewById<TextView>(R.id.destination)
        val timeTextView = rowView.findViewById<TextView>(R.id.time)
        val gateTextView = rowView.findViewById<TextView>(R.id.gate)
        val remarkTextView = rowView.findViewById<TextView>(R.id.remark)
        terminalTextView.text = flight!!.terminal
        numberTextView.text = flight.number
        destinationTextView.text = flight.destination
        timeTextView.text = flight.time
        gateTextView.text = flight.gate
        remarkTextView.text = flight.remark

        // Return the completed view to render on screen
        return rowView
    }
}
