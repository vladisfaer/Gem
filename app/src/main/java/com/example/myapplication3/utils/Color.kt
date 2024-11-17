package com.gem.framework.utils

import kotlin.math.*

data class Color(var r: Float = 1f, var g: Float = 1f, var b: Float = 1f, var a: Float = 1f) {
    companion object {
        val Red = Color(1f, 0f, 0f)
        val Green = Color(0f, 1f, 0f)
        val Blue = Color(0f, 0f, 1f)
        val White = Color(1f, 1f, 1f)
        val Black = Color(0f, 0f, 0f)
        val Transparent = Color(0f, 0f, 0f, 0f)
    }
}