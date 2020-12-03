package com.softbankrobotics.dx.dynamicconversationmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.aldebaran.qi.sdk.`object`.conversation.QiChatbot
import com.aldebaran.qi.sdk.`object`.conversation.Topic

class DynamicConversationMenuFragment(
    private val menuItemList: ArrayList<MenuItemData>,
    private val topic: Topic? = null,
    private val qiChatbot: QiChatbot? = null,
    private val layoutID: Int = R.layout.default_menu_item_layout,
    private val numberOfItemsPerLine: Int = menuItemList.count()
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView =
            inflater.inflate(R.layout.dynamic_conversation_menu_fragment, container, false)

        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerview)

        val gridLayoutManager = SpanningGridLayoutManager(context, numberOfItemsPerLine)
        recyclerView.layoutManager = gridLayoutManager

        val menuItemAdapter = MenuItemAdapter(context, menuItemList, topic, qiChatbot, layoutID)
        recyclerView.adapter = menuItemAdapter

        return rootView
    }
}