package com.gem.editor

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.gem.framework.SurfaceView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(SurfaceView(this))
    }
}