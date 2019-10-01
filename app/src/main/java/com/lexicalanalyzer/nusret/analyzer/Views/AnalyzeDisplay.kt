package com.lexicalanalyzer.nusret.analyzer.Views

import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.common.collect.Ordering
import com.google.common.collect.TreeMultimap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lexicalanalyzer.nusret.analyzer.R
import com.lexicalanalyzer.nusret.analyzer.Utils.Word
import java.lang.reflect.Type
import java.util.*

class AnalyzeDisplay : AppCompatActivity() {

    internal lateinit var matchedTexts: TextView
    internal lateinit var chart: PieChart

    //To take json words for newGSL
    internal lateinit var matchedlist: String

    internal var son = Gson()
    internal lateinit var type: Type

    //Number of words and unique words in EditText , and which dictionary user choose
    internal var numberofwords: Int = 0
    internal var Uniquewordsnumber: Int = 0
    internal var whichdic: Int = 0


    //The words it isnt in dictionary, it's change depend on which language choosen.
    internal lateinit var offlist: String
    internal lateinit var offlistwords: ArrayList<Word>
    internal lateinit var offliststrings: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze_display)

        matchedTexts = findViewById(R.id.textView2)
        matchedTexts.movementMethod = ScrollingMovementMethod() // To make it scrollable

        chart = findViewById(R.id.chart)


        //Offlist words defined in switch because offlist words could come as String or Word

        numberofwords = intent.getIntExtra("NumberofWords", 0)
        Uniquewordsnumber = intent.getIntExtra("Uniquewordsnumber", 0)
        offlist = intent.getStringExtra("OffSet")
        matchedlist = intent.getStringExtra("MatchedList")

        whichdic = intent.getIntExtra("DictionarySelect", 0)
        when (whichdic) {
            1 -> {
                type = object : TypeToken<ArrayList<Word>>() {

                }.type
                offlistwords = son.fromJson(offlist, type)
                getChart("New GSL")
            }
            2 -> {
                type = object : TypeToken<ArrayList<Word>>() {

                }.type
                offlistwords = son.fromJson(offlist, type)
                getChart("Academic Vocabulary List")
            }
            3 -> {
                type = object : TypeToken<ArrayList<Word>>() {

                }.type
                offlistwords = son.fromJson(offlist, type)
                getChart("NGSL")
            }
            4 -> {
                type = object : TypeToken<ArrayList<Word>>() {

                }.type
                offlistwords = son.fromJson(offlist, type)
                getChart("Academic Word List")
            }
            5 -> {
                type = object : TypeToken<ArrayList<String>>() {

                }.type
                offliststrings = son.fromJson(offlist, type)
                AcademicCollocation()
            }
            6 -> {
                type = object : TypeToken<ArrayList<String>>() {

                }.type
                offliststrings = son.fromJson(offlist, type)
                DiscorseConnectors()
            }
            7 -> {
                type = object : TypeToken<ArrayList<Word>>() {

                }.type
                offlistwords = son.fromJson(offlist, type)
                getChart("Phrasal Verbs")
            }
        }
    }

    private fun getChart(dicname: String) {
        val list = son.fromJson<ArrayList<Word>>(matchedlist, type)
        matchedTexts.text = ""

        val size = list.size
        val text = "   You used total $numberofwords words and $Uniquewordsnumber unique words. \n   You have $size  $dicname"
        matchedTexts.text = text

        val values = ArrayList<PieEntry>()
        values.add(PieEntry(list.size.toFloat(), dicname))
        values.add(PieEntry(offlistwords.size.toFloat(), "Offlist"))


        val matchedwords = ArrayList<ArrayList<Word>>()
        matchedwords.add(list)
        matchedwords.add(offlistwords)

        val wordsmulti = TreeMultimap.create(Ordering.natural<String>(), Ordering.natural<String>())

        for (w in list) {
            wordsmulti.put(w.type, w.word)
        }


        val dataSet = PieDataSet(values, dicname)
        dataSet.setAutomaticallyDisableSliceSpacing(true)
        dataSet.selectionShift = 10f
        dataSet.setColors(*ColorTemplate.JOYFUL_COLORS)

        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.BLACK)

        val description = Description()
        description.text = "Number of $dicname"
        description.textSize = 10f

        chart.description = description
        chart.description.isEnabled = true
        chart.setUsePercentValues(true)
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)
        chart.transparentCircleRadius = 31f
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic)
        chart.centerText = "   You use $size $dicname Total used unique words $Uniquewordsnumber"
        chart.setCenterTextSize(15f)
        chart.setDrawEntryLabels(false)
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTextSize(10f)
        chart.isRotationEnabled = false
        chart.legend.textSize = 5f

        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                matchedTexts.scrollTo(0, 0)
                //Value of selected chart,How many words from this chart
                val pos1 = e.y.toInt()

                //Position of selected chart
                val pos = h.x.toInt()

                val typeofwords: String
                matchedTexts.text = ""
                typeofwords = values[pos].label
                Toast.makeText(this@AnalyzeDisplay, typeofwords, Toast.LENGTH_SHORT).show()
                val text = "   Words from $typeofwords"
                matchedTexts.text = text
                val set = wordsmulti.keySet()

                if (typeofwords == dicname) {
                    for (type in set) {
                        if (type != "undefined") {
                            matchedTexts.append("   " + type + " " + wordsmulti.get(type).toString())
                            matchedTexts.append("\n")
                        } else {
                            val value = wordsmulti.get(type).toString()
                            val values = value.split("[^A-Za-z]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            for (`val` in values) {
                                matchedTexts.append("   $`val`\n")
                            }
                        }

                    }
                } else {
                    for (a in offlistwords) {
                        matchedTexts.append("\n" + "   " + a.word)
                    }
                }
            }

            override fun onNothingSelected() {

            }
        })
        chart.data = data
    }

    private fun DiscorseConnectors() {


        val words = son.fromJson<ArrayList<String>>(matchedlist, type)
        matchedTexts.text = ""

        val size = words.size
        val text = "   You use total $numberofwords words and $Uniquewordsnumber unique words. \n   You have $size unique words in Discourse Connectors"
        matchedTexts.text = text


        val values = ArrayList<PieEntry>()
        values.add(PieEntry(words.size.toFloat(), "Discourse Connectors"))
        values.add(PieEntry(offliststrings.size.toFloat(), "Offlist"))

        val dataSet = PieDataSet(values, "Discourse Connectors")
        dataSet.setAutomaticallyDisableSliceSpacing(true)
        dataSet.selectionShift = 10f
        dataSet.setColors(*ColorTemplate.JOYFUL_COLORS)


        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.BLACK)

        val description = Description()
        description.text = "Number of Discourse Connectors"
        description.textSize = 10f

        chart.description = description
        chart.description.isEnabled = true
        chart.setUsePercentValues(true)
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)
        chart.transparentCircleRadius = 31f
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic)
        chart.centerText = "   You use $size Discourse Connectors  Total used unique words $Uniquewordsnumber"
        chart.setCenterTextSize(15f)
        chart.setDrawEntryLabels(false)
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTextSize(10f)
        chart.isRotationEnabled = false
        chart.legend.textSize = 5f

        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                matchedTexts.scrollTo(0, 0)
                //Value of selected chart,How many words from this chart
                val pos1 = e.y.toInt()

                //Position of selected chart
                val pos = h.x.toInt()

                val typeofwords: String
                matchedTexts.text = ""
                typeofwords = values[pos].label
                Toast.makeText(this@AnalyzeDisplay, "$typeofwords $pos1", Toast.LENGTH_SHORT).show()
                val text = "   Words from $typeofwords\n"
                matchedTexts.text = text

                if (typeofwords == "Discourse Connectors") {
                    for (a in words) {
                        matchedTexts.append(a)
                        matchedTexts.append("\n")
                    }
                } else {
                    for (a in offliststrings) {
                        matchedTexts.append(a)
                        matchedTexts.append("\n")
                    }
                }
            }

            override fun onNothingSelected() {

            }
        })
        chart.data = data

    }

    private fun AcademicCollocation() {

        val words = son.fromJson<ArrayList<String>>(matchedlist, type)
        matchedTexts.text = ""

        val size = words.size
        val text = "   You use total $numberofwords words and $Uniquewordsnumber unique words. \n   You have $size unique words in Academic Collocation Library"
        matchedTexts.text = text


        val values = ArrayList<PieEntry>()
        values.add(PieEntry(words.size.toFloat(), "Academic Collocations"))
        values.add(PieEntry(offliststrings.size.toFloat(), "Offlist"))

        val dataSet = PieDataSet(values, "Academic Collocations")
        dataSet.setAutomaticallyDisableSliceSpacing(true)
        dataSet.selectionShift = 10f
        dataSet.setColors(*ColorTemplate.JOYFUL_COLORS)

        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.BLACK)

        val description = Description()
        description.text = "Number of Academic Collocations"
        description.textSize = 10f

        chart.description = description
        chart.description.isEnabled = true
        chart.setUsePercentValues(true)
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)
        chart.transparentCircleRadius = 31f
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic)
        chart.centerText = "   You use $size Academic Collocations  Total used unique words $Uniquewordsnumber"
        chart.setCenterTextSize(15f)
        chart.setDrawEntryLabels(false)
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTextSize(10f)
        chart.isRotationEnabled = false
        chart.legend.textSize = 5f

        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                matchedTexts.scrollTo(0, 0)
                //Value of selected chart,How many words from this chart
                val pos1 = e.y.toInt()

                //Position of selected chart
                val pos = h.x.toInt()

                val typeofwords: String
                matchedTexts.text = ""
                typeofwords = values[pos].label
                Toast.makeText(this@AnalyzeDisplay, "$typeofwords $pos1", Toast.LENGTH_SHORT).show()
                val text = "   Words from $typeofwords\n"
                matchedTexts.text = text

                if (typeofwords == "Academic Collocations") {
                    for (a in words) {
                        matchedTexts.append(a)
                        matchedTexts.append("\n")
                    }
                } else {
                    for (a in offliststrings) {
                        matchedTexts.append(a)
                        matchedTexts.append("\n")
                    }
                }
            }

            override fun onNothingSelected() {

            }
        })
        chart.data = data
    }
}
