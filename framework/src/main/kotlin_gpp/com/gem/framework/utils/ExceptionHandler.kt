package com.gem.framework.utils

import android.util.Log

object ExceptionHandler {
    var handler: (Throwable, Any?) -> Unit = { e, source ->
        val sourceName = source?.let { " [${it::class.simpleName}]" } ?: ""
        Log.e("GemEngine", "Unhandled engine exception$sourceName", e)
    }

    fun handle(e: Throwable, source: Any? = null) {
        handler(e, source)
    }

    inline fun <T> safe(source: Any? = null, block: () -> T): T? {
        return try {
            block()
        } catch (e: Throwable) {
            handle(e, source)
            null
        }
    }
}
