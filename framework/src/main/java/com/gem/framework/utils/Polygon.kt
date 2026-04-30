package com.gem.framework.utils

import org.jbox2d.common.Vec2
import com.gem.framework.globContext

data class Polygon(var fileName: String) {
    lateinit var vertices: List<Vector2>

    constructor(_vertices: List<Vector2>) : this(""){
        vertices = _vertices
    }

    init {
        if (!::vertices.isInitialized){
            val inputStream = globContext.assets.open(fileName)
            val reader = inputStream.bufferedReader()
            val cords = mutableListOf<Vector2>()
            reader.forEachLine { line ->
                val cord = line.trim().split(' ')
                cords.add(Vector2(cord[0].toFloat(),cord[1].toFloat()))
            }
            vertices = cords.toList()
        }
        require(vertices.size >= 3) { "Многоугольник должен иметь как минимум 3 вершины." }
    }

    fun getVertexArray(): FloatArray {
        return vertices.flatMap { listOf(it.x, it.y, 0f) }.toFloatArray()
    }

    fun getVec2Array(): Array<Vec2> {
        return vertices.map { Vec2(it.x, it.y) }.toTypedArray()
    }
}