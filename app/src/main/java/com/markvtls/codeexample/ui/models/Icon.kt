package com.wallpaperscraft.keby.app.ui.models

import android.graphics.drawable.Drawable
import com.wallpaperscraft.keby.domain.models.IconAlignment
import com.wallpaperscraft.keby.domain.models.IconState

data class Icon(
    val drawable: Drawable,
    val state: IconState,
    val alignment: IconAlignment
)