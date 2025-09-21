package com.gem.framework.components

import com.gem.framework.utils.Audio

class AudioPlayerComponent(private val audio: Audio) : Component() {

    // Воспроизведение с опцией зацикливания
    fun play(loop: Boolean = false) {
        audio.mediaPlayer.apply {
            isLooping = loop
            if (!isPlaying) start()
        }
    }

    // Пауза воспроизведения
    fun pause() {
        if (audio.mediaPlayer.isPlaying) {
            audio.mediaPlayer.pause()
        }
    }

    // Остановка воспроизведения
    fun stop() {
        audio.mediaPlayer.apply {
            if (isPlaying) {
                stop()
                prepare() // Подготовка для следующего воспроизведения
            }
        }
    }

    // Освобождение ресурса при удалении компонента
    override fun onRemove() {
        audio.release()
    }

    override fun copy(): AudioPlayerComponent {
        return AudioPlayerComponent(audio)
    }
}