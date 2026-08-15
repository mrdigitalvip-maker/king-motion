package com.kingmotion.engine.renderer

import android.opengl.GLES20

class GlShaderProgram(vertexSource: String, fragmentSource: String) : AutoCloseable {
    val id: Int = GLES20.glCreateProgram().also { program ->
        val vertex = compile(GLES20.GL_VERTEX_SHADER, vertexSource)
        val fragment = compile(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        GLES20.glAttachShader(program, vertex)
        GLES20.glAttachShader(program, fragment)
        GLES20.glLinkProgram(program)
        val status = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
        GLES20.glDeleteShader(vertex)
        GLES20.glDeleteShader(fragment)
        check(status[0] == GLES20.GL_TRUE) { "Shader link failed: ${GLES20.glGetProgramInfoLog(program)}" }
    }

    fun setFloat(name: String, value: Float) {
        GLES20.glUniform1f(GLES20.glGetUniformLocation(id, name), value)
    }

    override fun close() = GLES20.glDeleteProgram(id)

    private fun compile(type: Int, source: String): Int = GLES20.glCreateShader(type).also { shader ->
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        val status = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
        check(status[0] == GLES20.GL_TRUE) { "Shader compile failed: ${GLES20.glGetShaderInfoLog(shader)}" }
    }
}
