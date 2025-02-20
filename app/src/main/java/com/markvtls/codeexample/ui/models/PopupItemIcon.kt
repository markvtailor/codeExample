package com.wallpaperscraft.keby.app.ui.models

import com.wallpaperscraft.keby.domain.models.PopupItem

class PopupItemIcon(
    val icon: KeyIcon,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    val iconWidth: Int,
    val iconHeight: Int
): PopupItem(x, y, width, height)