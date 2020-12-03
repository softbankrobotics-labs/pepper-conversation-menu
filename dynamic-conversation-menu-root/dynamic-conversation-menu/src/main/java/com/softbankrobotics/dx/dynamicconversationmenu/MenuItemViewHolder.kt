package com.softbankrobotics.dx.dynamicconversationmenu

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

internal class MenuItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val image: ImageView = view.findViewById(R.id.image)
    val title: TextView = view.findViewById(R.id.title)
    val cardLayout: CardView = view.findViewById(R.id.cardLayout)
}
