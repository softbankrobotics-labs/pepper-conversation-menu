package com.softbankrobotics.dx.dynamicconversationmenu

data class MenuItemData(
    val name: String,
    val image: Int,
    val bookmarkName: String? = null,
    val onClick: (() -> Unit)? = null
)