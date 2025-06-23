package com.uasml2.rnnapp.data.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.uasml2.rnnapp.R

class DatasetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dataset, container, false)
        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayout)
        val csvData = readCsvFromAssets(requireContext(), "Amazon.csv")

        csvData.take(30).forEachIndexed { rowIndex, row ->
            val tableRow = TableRow(requireContext())

            row.forEach { cell ->
                val cellView = TextView(requireContext()).apply {
                    text = cell.replace("\n", " ") // hapus enter dari CSV
                    setPadding(12, 8, 12, 8)
                    setTextColor(Color.BLACK)
                    textSize = 12f
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    width = 200  // bisa disesuaikan
                    setTypeface(Typeface.MONOSPACE)
                    setBackgroundResource(R.drawable.table_cell_border)
                }

                tableRow.addView(cellView)
            }
            tableLayout.addView(tableRow)
        }

        return view
    }

    private fun readCsvFromAssets(context: Context, fileName: String): List<List<String>> {
        val result = mutableListOf<List<String>>()
        val inputStream = context.assets.open(fileName)
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val values = line.split(",") // bisa disesuaikan separator
                result.add(values)
            }
        }
        return result
    }
}
