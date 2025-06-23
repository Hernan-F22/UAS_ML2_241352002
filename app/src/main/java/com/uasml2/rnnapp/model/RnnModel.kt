package com.uasml2.rnnapp.model

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class RnnModel(private val context: Context) {

    private var interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile("amazon_stock_price.tflite"))
    }

    private fun loadModelFile(filename: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun process(inputBuffer: TensorBuffer): TensorBuffer {
        // Output shape misalnya: [1, 1] jika regresi
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 1), inputBuffer.dataType)
        interpreter.run(inputBuffer.buffer, outputBuffer.buffer.rewind())
        return outputBuffer
    }

    fun close() {
        interpreter.close()
    }

    companion object {
        fun newInstance(context: Context): RnnModel {
            return RnnModel(context)
        }
    }
}