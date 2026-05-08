package com.gem.framework.components

import android.content.Context
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.gem.framework.*
import java.io.File
import java.io.FileInputStream

class LoadTestComponent(
    private val context: Context = globContext
) : Component("LoadTest") {
    var mtime = 0f
    
    override fun update(){
        if (time - mtime > 15f) {
            val mobj = load()
            if (mobj != null){
                mobj.onLoad()
                gameObject.add(mobj)
                mtime += 1000000f
            }
        }
    }

    private val kryo = Kryo().apply {
        isRegistrationRequired = false
        references = true
    }

    private fun getFile(name: String): File {
        return File(context.filesDir, name)
    }

    fun load(filename: String = "scene.bin"): GameObject? {
        val file = getFile(filename)

        if (!file.exists()) {
            println("Save file not found: ${file.absolutePath}")
            return null
        }

        val obj = FileInputStream(file).use { fis ->
            Input(fis).use { input ->
                kryo.readClassAndObject(input)
            }
        }

        return obj as? GameObject
    }
}
