package com.softbankrobotics.dx.dynamicconversationmenu

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.aldebaran.qi.sdk.`object`.conversation.AutonomousReactionImportance
import com.aldebaran.qi.sdk.`object`.conversation.AutonomousReactionValidity
import com.aldebaran.qi.sdk.`object`.conversation.QiChatbot
import com.aldebaran.qi.sdk.`object`.conversation.Topic

internal class MenuItemAdapter(
    private val context: Context?,
    private val menuItemList: List<MenuItemData>,
    private val topic: Topic? = null,
    private val qiChatbot: QiChatbot? = null,
    private val layoutID: Int = R.layout.default_menu_item_layout
) : RecyclerView.Adapter<MenuItemViewHolder>() {

    private val handler = Handler()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuItemViewHolder {
        val view: View = LayoutInflater.from(context)
            .inflate(layoutID, parent, false)
        return MenuItemViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return menuItemList.size
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val menuItem = menuItemList[position]
        holder.image.setImageResource(menuItem.image)
        holder.title.text = menuItem.name
        if (menuItem.onClick != null) {
            holder.cardLayout.setOnClickListener {
                runSelectedAnimation(holder)
                handler.postDelayed({
                    menuItem.onClick.invoke()
                }, 300)
            }
        } else {
            holder.cardLayout.setOnClickListener {
                runSelectedAnimation(holder)
                topic?.async()?.bookmarks?.andThenConsume {
                    val bookmarkName = menuItemList[position].bookmarkName
                    if (bookmarkName != null) {
                        val bookmark = it[bookmarkName]
                        handler.postDelayed({
                            qiChatbot?.async()?.goToBookmark(
                                bookmark,
                                AutonomousReactionImportance.HIGH,
                                AutonomousReactionValidity.IMMEDIATE
                            )
                        }, 300)
                    }
                }
            }
        }
    }

    private fun runSelectedAnimation(holder: MenuItemViewHolder) {
        val animZoomOut = AnimationUtils.loadAnimation(context, R.anim.zoom_out)
        holder.cardLayout.startAnimation(animZoomOut)
    }
}
