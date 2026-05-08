package com.gem.framework.components

import android.content.Context
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import com.gem.framework.GameObject
import com.gem.framework.globContext
import com.gem.framework.time
import com.gem.framework.rootObject
import java.io.File
import java.io.FileOutputStream

class SaveTestComponent(
    @DontSave private val context: Context = globContext
) : Component("SaveTest") {
    //timer for test
    var mtime = 0f
    var stage: Int = 0
    
    @DontSave val myObj = GameObject("LOADED OBJECT").apply{
        add(RectangleComponent())
    }
    
    override fun onPostInit(){
        mtime = time
    }
    
    override fun update(){
        if(time - mtime > 3f) {
            mtime += 1000000f
            gameObject.onSave()
            save(gameObject)
        }
    }

    @DontSave private val kryo = Kryo().apply {
        isRegistrationRequired = false // можно true для максимальной скорости
        references = true // важно для графов (родитель/дети)
    }

    private fun getFile(name: String): File {
        return File(context.filesDir, name)
    }

    fun save(root: GameObject, filename: String = "scene.bin") {
        val file = getFile(filename)

        FileOutputStream(file).use { fos ->
            Output(fos).use { output ->
                kryo.writeClassAndObject(output, root)
            }
        }

        println("Saved (Kryo) to: ${file.absolutePath}")
    }
}
