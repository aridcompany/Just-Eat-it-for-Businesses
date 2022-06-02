package com.ari_d.justeat_itforbusinesses.ui.Main.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.data.entities.Sales
import com.ari_d.justeat_itforbusinesses.databinding.FragmentHomeBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var scoreList = ArrayList<Sales>()
    private lateinit var lineChart: LineChart

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lineChart = binding.chartView
        initLineChart()
        setDataToLineChart()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initLineChart() {
        val xAxis: XAxis = lineChart.xAxis
        val leftAxis: YAxis = lineChart.axisLeft
        val rightAxis: YAxis = lineChart.axisRight
        xAxis.setDrawGridLines(false)
        leftAxis.setDrawGridLines(false)
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawAxisLine(false)
        rightAxis.textColor = requireContext().getColor(R.color.white)

        lineChart.animateX(1000, Easing.EaseInSine)
        lineChart.isDragEnabled = true
        lineChart.description.isEnabled = false
        lineChart.setScaleEnabled(true)
        lineChart.setDrawGridBackground(false)
        lineChart.isHighlightPerDragEnabled = true
        lineChart.setTouchEnabled(true)
        lineChart.dragDecelerationFrictionCoef = 0.9f


        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = MyAxisFormatter()
        xAxis.axisLineColor = requireContext().getColor(R.color.black)
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +90f

        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.isGranularityEnabled = true
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 170f
        leftAxis.yOffset = -9f
        leftAxis.textColor = requireContext().getColor(R.color.secondaryColor)

    }


    inner class MyAxisFormatter : IndexAxisValueFormatter() {

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index < scoreList.size) {
                scoreList[index].name
            } else {
                ""
            }
        }
    }

    private fun setDataToLineChart() {
        //now draw bar chart with dynamic data
        val entries: ArrayList<Entry> = ArrayList()

        scoreList = getScoreList()

        for (i in scoreList.indices) {
            val score = scoreList[i]
            entries.add(Entry(i.toFloat(), score.score.toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "")

        val data = LineData(lineDataSet)
        lineChart.data = data

        lineChart.invalidate()
        lineChart.data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
    }

    private fun getScoreList(): ArrayList<Sales> {
        scoreList.add(0, Sales("Sunday", 37))
        scoreList.add(1, Sales("Monday", 56))
        scoreList.add(2, Sales( "Tuesday", 75))
        scoreList.add(3, Sales("Wednesday", 85))
        scoreList.add(4, Sales("Thursday", 45))
        scoreList.add(5, Sales("Friday", 63))
        scoreList.add(6, Sales("Saturday", 94))

        return scoreList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}