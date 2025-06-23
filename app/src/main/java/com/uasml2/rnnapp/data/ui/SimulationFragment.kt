package com.uasml2.rnnapp.data.ui

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.CandleStickChart
import com.uasml2.rnnapp.R
import com.uasml2.rnnapp.model.RnnModel
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class SimulationFragment : Fragment() {
    private lateinit var model: RnnModel
    private lateinit var inputText: EditText
    private lateinit var btnPredict: Button
    private lateinit var tvResult: TextView
    private lateinit var candleChart: CandleStickChart
    private lateinit var btnGenerate: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_simulation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputText = view.findViewById(R.id.etInputText)
        btnPredict = view.findViewById(R.id.btnPredict)
        tvResult = view.findViewById(R.id.tvHasil)
        candleChart = view.findViewById(R.id.candleChart)
        btnGenerate = view.findViewById(R.id.btnGenerate)

        model = RnnModel.newInstance(requireContext())

        btnGenerate.setOnClickListener {
            // Buat 10 angka acak antara 1850–2050, tetap urut naik
            val dummyData = (0..9).map { i ->
                (1850 + i * (5..15).random()).toFloat()
            }

            val dummyText = dummyData.joinToString(", ") { "%.0f".format(it) }
            inputText.setText(dummyText)
            tvResult.text = "Dummy data berhasil diisi."
        }

        btnPredict.setOnClickListener {
            val userInput = inputText.text.toString()
            val inputArray = parseInput(userInput)
            if (inputArray.size != 10) {
                tvResult.text = "Masukkan tepat 10 nilai harga saham!"
                return@setOnClickListener
            }

            // Normalisasi dan prediksi
            val scaledInput = normalize(inputArray)
            val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 10, 1), DataType.FLOAT32)
            inputFeature.loadArray(scaledInput)

            val output = model.process(inputFeature)
            val predictedScaled = output.floatArray
            val predictedValue = inverseNormalize(predictedScaled)[0]

            tvResult.text = "Prediksi harga saham berikutnya: $%.2f".format(predictedValue)

            // Tampilkan chart
            showChart(inputArray, predictedValue)
        }
    }

    private fun showChart(data: FloatArray, prediction: Float) {
        val candleEntries = mutableListOf<CandleEntry>()

        for (i in data.indices) {
            val base = data[i]
            val high = base + (5..15).random()
            val low = base - (5..15).random()
            val open = base
            val close = base + (-10..10).random()
            candleEntries.add(CandleEntry(i.toFloat(), high, low, open, close))
        }

        // Tambahkan prediksi sebagai candle terakhir
        val predIndex = data.size
        candleEntries.add(
            CandleEntry(predIndex.toFloat(), prediction + 10, prediction - 10, prediction, prediction)
        )

        val dataSet = CandleDataSet(candleEntries, "Harga Saham dan Prediksi")
        dataSet.color = Color.rgb(80, 80, 80)
        dataSet.shadowColor = Color.DKGRAY
        dataSet.decreasingColor = Color.RED
        dataSet.decreasingPaintStyle = Paint.Style.FILL
        dataSet.increasingColor = Color.GREEN
        dataSet.increasingPaintStyle = Paint.Style.FILL
        dataSet.neutralColor = Color.BLUE
        dataSet.setDrawValues(false)

        val candleData = CandleData(dataSet)
        candleChart.data = candleData
        candleChart.description.text = "Visualisasi Candle Saham"
        candleChart.invalidate()
    }


    override fun onDestroyView() {
        model.close()
        super.onDestroyView()
    }

    private fun parseInput(text: String): FloatArray {
        return text.trim()
            .split(",", " ", "\n")
            .mapNotNull { it.toFloatOrNull() }
            .toFloatArray()
    }

    private fun normalize(values: FloatArray): FloatArray {
        val min = 1800f
        val max = 2260f
        return values.map { (it - min) / (max - min) }.toFloatArray()
    }

    private fun inverseNormalize(values: FloatArray): FloatArray {
        val min = 1800f
        val max = 2260f
        return values.map { it * (max - min) + min }.toFloatArray()
    }
}
