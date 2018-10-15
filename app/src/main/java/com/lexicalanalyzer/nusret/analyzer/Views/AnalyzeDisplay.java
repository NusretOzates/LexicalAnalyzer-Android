package com.lexicalanalyzer.nusret.analyzer.Views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lexicalanalyzer.nusret.analyzer.R;
import com.lexicalanalyzer.nusret.analyzer.Utils.Word;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.NavigableSet;

public class AnalyzeDisplay extends AppCompatActivity {

    TextView matchedTexts;
    PieChart chart;

    //To take json words for newGSL
    String matchedlist;

    Gson son = new Gson();
    Type type;

    //Number of words and unique words in EditText , and which dictionary user choose
    int numberofwords;
    int Uniquewordsnumber;
    int whichdic;


    //The words it isnt in dictionary, it's change depend on which language choosen.
    String offlist;
    ArrayList<Word> offlistwords;
    ArrayList<String> offliststrings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_display);

        matchedTexts = findViewById(R.id.textView2);
        matchedTexts.setMovementMethod(new ScrollingMovementMethod()); // To make it scrollable

        chart = findViewById(R.id.chart);


        //Offlist words defined in switch because offlist words could come as String or Word

        numberofwords = getIntent().getIntExtra("NumberofWords", 0);
        Uniquewordsnumber = getIntent().getIntExtra("Uniquewordsnumber", 0);
        offlist = getIntent().getStringExtra("OffSet");
        matchedlist = getIntent().getStringExtra("MatchedList");

        whichdic = getIntent().getIntExtra("DictionarySelect", 0);
        switch (whichdic) {
            case (1): {
                type = new TypeToken<ArrayList<Word>>() {
                }.getType();
                offlistwords = son.fromJson(offlist, type);
                getChart("New GSL");
                break;
            }
            case (2): {
                type = new TypeToken<ArrayList<Word>>() {
                }.getType();
                offlistwords = son.fromJson(offlist, type);
                getChart("Academic Vocabulary List");
                break;
            }
            case (3): {
                type = new TypeToken<ArrayList<Word>>() {
                }.getType();
                offlistwords = son.fromJson(offlist, type);
                getChart("NGSL");
                break;
            }
            case (4): {
                type = new TypeToken<ArrayList<Word>>() {
                }.getType();
                offlistwords = son.fromJson(offlist, type);
                getChart("Academic Word List");
                break;
            }
            case (5): {
                type = new TypeToken<ArrayList<String>>() {
                }.getType();
                offliststrings = son.fromJson(offlist, type);
                AcademicCollocation();
                break;
            }
            case (6): {
                type = new TypeToken<ArrayList<String>>() {
                }.getType();
                offliststrings = son.fromJson(offlist, type);
                DiscorseConnectors();
                break;
            }
            case (7): {
                type = new TypeToken<ArrayList<Word>>() {
                }.getType();
                offlistwords = son.fromJson(offlist, type);
                getChart("Phrasal Verbs");
            }

        }
    }

    private void getChart(final String dicname) {
        ArrayList<Word> list = son.fromJson(matchedlist, type);
        matchedTexts.setText("");

        int size = list.size();
        String text = "   You used total " + numberofwords + " words and " + Uniquewordsnumber + " unique words. " + "\n   You have " + size + "  " + dicname;
        matchedTexts.setText(text);

        final ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(list.size(), dicname));
        values.add(new PieEntry(offlistwords.size(), "Offlist"));


        final ArrayList<ArrayList<Word>> matchedwords = new ArrayList<>();
        matchedwords.add(list);
        matchedwords.add(offlistwords);

        final TreeMultimap<String, String> wordsmulti = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());

        for (Word w : list) {
            wordsmulti.put(w.getType(), w.getWord());
        }


        PieDataSet dataSet = new PieDataSet(values, dicname);
        dataSet.setAutomaticallyDisableSliceSpacing(true);
        dataSet.setSelectionShift(10f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        Description description = new Description();
        description.setText("Number of " + dicname);
        description.setTextSize(10f);

        chart.setDescription(description);
        chart.getDescription().setEnabled(true);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleRadius(31f);
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic);
        chart.setCenterText("   You use " + size + " " + dicname + " Total used unique words " + Uniquewordsnumber);
        chart.setCenterTextSize(15f);
        chart.setDrawEntryLabels(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(10f);
        chart.setRotationEnabled(false);
        chart.getLegend().setTextSize(5f);

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                matchedTexts.scrollTo(0, 0);
                //Value of selected chart,How many words from this chart
                int pos1 = (int) e.getY();

                //Position of selected chart
                int pos = (int) h.getX();

                String typeofwords;
                matchedTexts.setText("");
                typeofwords = values.get(pos).getLabel();
                Toast.makeText(AnalyzeDisplay.this, typeofwords, Toast.LENGTH_SHORT).show();
                String text = "   Words from " + typeofwords + "\n";
                matchedTexts.setText(text);
                NavigableSet<String> set = wordsmulti.keySet();

                if (typeofwords.equals(dicname)) {
                    for (String type : set) {
                        if (!type.equals("undefined")) {
                            matchedTexts.append("   " + type + " " + wordsmulti.get(type).toString());
                            matchedTexts.append("\n");
                        } else {
                            String value = wordsmulti.get(type).toString();
                            String[] values = value.split("[^A-Za-z]+");
                            for (String val : values) {
                                matchedTexts.append("   " + val + "\n");
                            }
                        }

                    }
                } else {
                    for (Word a : offlistwords) {
                        matchedTexts.append(a.getWord());
                        matchedTexts.append("\n");
                    }
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        chart.setData(data);
    }

    private void DiscorseConnectors() {


        final ArrayList<String> words = son.fromJson(matchedlist, type);
        matchedTexts.setText("");

        int size = words.size();
        String text = "   You use total " + numberofwords + " words and " + Uniquewordsnumber + " unique words. " + "\n   You have " + size + " unique words in Discourse Connectors";
        matchedTexts.setText(text);


        final ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(words.size(), "Discourse Connectors"));
        values.add(new PieEntry(offliststrings.size(), "Offlist"));

        PieDataSet dataSet = new PieDataSet(values, "Discourse Connectors");
        dataSet.setAutomaticallyDisableSliceSpacing(true);
        dataSet.setSelectionShift(10f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        Description description = new Description();
        description.setText("Number of Discourse Connectors");
        description.setTextSize(10f);

        chart.setDescription(description);
        chart.getDescription().setEnabled(true);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleRadius(31f);
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic);
        chart.setCenterText("   You use " + size + " Discourse Connectors " + " Total used unique words " + Uniquewordsnumber);
        chart.setCenterTextSize(15f);
        chart.setDrawEntryLabels(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(10f);
        chart.setRotationEnabled(false);
        chart.getLegend().setTextSize(5f);

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                matchedTexts.scrollTo(0, 0);
                //Value of selected chart,How many words from this chart
                int pos1 = (int) e.getY();

                //Position of selected chart
                int pos = (int) h.getX();

                String typeofwords;
                matchedTexts.setText("");
                typeofwords = values.get(pos).getLabel();
                Toast.makeText(AnalyzeDisplay.this, typeofwords + " " + pos1, Toast.LENGTH_SHORT).show();
                String text = "   Words from " + typeofwords + "\n";
                matchedTexts.setText(text);

                if (typeofwords.equals("Discourse Connectors")) {
                    for (String a : words) {
                        matchedTexts.append(a);
                        matchedTexts.append("\n");
                    }
                } else {
                    for (String a : offliststrings) {
                        matchedTexts.append(a);
                        matchedTexts.append("\n");
                    }
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        chart.setData(data);

    }

    private void AcademicCollocation() {

        final ArrayList<String> words = son.fromJson(matchedlist, type);
        matchedTexts.setText("");

        int size = words.size();
        String text = "   You use total " + numberofwords + " words and " + Uniquewordsnumber + " unique words. " + "\n   You have " + size + " unique words in Academic Collocation Library";
        matchedTexts.setText(text);


        final ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(words.size(), "Academic Collocations"));
        values.add(new PieEntry(offliststrings.size(), "Offlist"));

        PieDataSet dataSet = new PieDataSet(values, "Academic Collocations");
        dataSet.setAutomaticallyDisableSliceSpacing(true);
        dataSet.setSelectionShift(10f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        Description description = new Description();
        description.setText("Number of Academic Collocations");
        description.setTextSize(10f);

        chart.setDescription(description);
        chart.getDescription().setEnabled(true);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleRadius(31f);
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic);
        chart.setCenterText("   You use " + size + " Academic Collocations " + " Total used unique words " + Uniquewordsnumber);
        chart.setCenterTextSize(15f);
        chart.setDrawEntryLabels(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(10f);
        chart.setRotationEnabled(false);
        chart.getLegend().setTextSize(5f);

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                matchedTexts.scrollTo(0, 0);
                //Value of selected chart,How many words from this chart
                int pos1 = (int) e.getY();

                //Position of selected chart
                int pos = (int) h.getX();

                String typeofwords;
                matchedTexts.setText("");
                typeofwords = values.get(pos).getLabel();
                Toast.makeText(AnalyzeDisplay.this, typeofwords + " " + pos1, Toast.LENGTH_SHORT).show();
                String text = "   Words from " + typeofwords + "\n";
                matchedTexts.setText(text);

                if (typeofwords.equals("Academic Collocations")) {
                    for (String a : words) {
                        matchedTexts.append(a);
                        matchedTexts.append("\n");
                    }
                } else {
                    for (String a : offliststrings) {
                        matchedTexts.append(a);
                        matchedTexts.append("\n");
                    }
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        chart.setData(data);
    }
}
