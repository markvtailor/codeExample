package com.wallpaperscraft.keby.app.ui.models

data class DownloadedTheme(
    val themeId: Int,
    val themeUrl: String,
    val isActive: Boolean,
    val markedForDeletion: Boolean
)
