package com.gem.framework.utils

import android.media.MediaPlayer
import com.gem.framework.globContext

class Audio(fileName: String) {

    internal val mediaPlayer: MediaPlayer = MediaPlayer()

    init {
        val assetFileDescriptor = globContext.assets.openFd(fileName)
        mediaPlayer.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )
        mediaPlayer.prepare()
        assetFileDescriptor.close()
    }

    fun release() {
        mediaPlayer.release()
    }
}