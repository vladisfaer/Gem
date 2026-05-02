package com.gem.framework.components

import com.gem.framework.utils.Audio

class AudioPlayerComponent(val audio: Audio) : Component() {

    fun play(loop: Boolean = false) {
        audio.mediaPlayer.apply {
            isLooping = loop
            if (!isPlaying) start()
        }
    }

    fun pause() {
        if (audio.mediaPlayer.isPlaying) {
            audio.mediaPlayer.pause()
        }
    }

    fun stop() {
        audio.mediaPlayer.apply {
            if (isPlaying) {
                stop()
                prepare()
            }
        }
    }

    override fun onRemove() {
        audio.release()
    }
}
