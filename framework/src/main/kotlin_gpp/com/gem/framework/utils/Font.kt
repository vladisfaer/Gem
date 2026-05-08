package com.gem.framework.utils

import android.graphics.Typeface
import com.gem.framework.*

class Font(private val fontName: String) {

    @DontSave var typeface: Typeface? = null
        private set

    init {
        typeface = Typeface.createFromAsset(globContext.assets, fontName)
            ?: throw RuntimeException("Не удалось загрузить шрифт: $fontName")
    }
}