package com.example.nusret.ifoundthisoninternet;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;

public class AnalyzeDisplay extends AppCompatActivity {

    //ArraList for NewGSL matched words
    ArrayList<String> matchedWords1 = new ArrayList<>();
    ArrayList<String> matchedWords2 = new ArrayList<>();
    ArrayList<String> matchedWords3 = new ArrayList<>();

    TextView matchedTexts;
    PieChart chart;

    //To take json words for newGSL
    String wordListAsString;
    String first500, second500, last, supplemental;

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

        //From json to ArrayList

        Gson son = new Gson();
        Type type = new TypeToken<List<Word>>() {
        }.getType();

        Type type1  = new TypeToken<List<String>>()
        {}.getType();

        whichdic = getIntent().getIntExtra("DictionarySelect", 0);
        switch (whichdic) {
            case (1): {
                offlist = getIntent().getStringExtra("OffSet");
                offlistwords = son.fromJson(offlist, type);
                NewGSL();
                break;
            }
            case (2): {
                offlist = getIntent().getStringExtra("OffSet");
                offlistwords = son.fromJson(offlist, type);
                AcademicWordList();
                break;
            }
            case (3): {
                offlist = getIntent().getStringExtra("OffSet");
                offlistwords = son.fromJson(offlist, type);
                NGSL();
                break;
            }
            case (4): {
                offlist = getIntent().getStringExtra("OffSet");
                offlistwords = son.fromJson(offlist, type);
                AWL();
                break;
            }
            case (5): {
                offlist = getIntent().getStringExtra("OffSet");
                offliststrings = son.fromJson(offlist, type1);
                AcademicCollocation();
                break;
            }
            case(6):
            {
                offlist = getIntent().getStringExtra("OffSet");
                offliststrings = son.fromJson(offlist, type1);
                DiscorseConnectors();
                break;
            }
            case(7):
            {
                offlist = getIntent().getStringExtra("OffSet");
                offlistwords = son.fromJson(offlist, type);
                PhrasalVerbs();
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent toMain = new Intent(this, MainActivity.class);
        startActivity(toMain);
    }

    private void PhrasalVerbs()
    {
        numberofwords = getIntent().getIntExtra("NumberofWords", 0);
        Uniquewordsnumber = getIntent().getIntExtra("Uniquewordsnumber", 0);
        wordListAsString = getIntent().getStringExtra("matchedlist");
        Gson son = new Gson();
        Type type = new TypeToken<ArrayList<Word>>() {
        }.getType();

        final ArrayList<Word> words = son.fromJson(wordListAsString, type);

        matchedTexts.setText("");

        int size = words.size();
        String text = "   You use total " + numberofwords + " words and " + Uniquewordsnumber + " unique words. " + "\n   You have " + size + " Phrasal Verbs";
        matchedTexts.setText(text);

        final ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(words.size(), "Phrasal Verbs"));
        values.add(new PieEntry(offlistwords.size(), "Offlist"));


        final ArrayList<ArrayList<Word>> matchedwords = new ArrayList<>();
        matchedwords.add(words);
        matchedwords.add(offlistwords);

        final TreeMultimap<String, String> wordsmulti = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());

        for (Word w : words) {
            wordsmulti.put(w.getType(), w.getWord());
        }

        PieDataSet dataSet = new PieDataSet(values, "Phrasal Verbs");
        dataSet.setAutomaticallyDisableSliceSpacing(true);
        dataSet.setSelectionShift(10f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        Description description = new Description();
        description.setText("Number of Phrasal Verbs");
        description.setTextSize(10f);

        chart.setDescription(description);
        chart.getDescription().setEnabled(true);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleRadius(31f);
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic);
        chart.setCenterText("   You use " + size + " Phrasal Verbs " + " Total used unique words " + Uniquewordsnumber);
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
                NavigableSet<String> set = wordsmulti.keySet();

                if(typeofwords.equals("Phrasal Verbs"))
                {
                    for (String type : set) {
                        matchedTexts.append("   " + type + " " + wordsmulti.get(type).toString());
                        matchedTexts.append("\n");

                    }
                }else
                { for(Word a :offlistwords)
                {
                    matchedTexts.append(a.word);
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
    private void DiscorseConnectors()
    {
        numberofwords = getIntent().getIntExtra("NumberofWords", 0);
        Uniquewordsnumber = getIntent().getIntExtra("Uniquewordsnumber", 0);
        wordListAsString = getIntent().getStringExtra("matchedlist");
        Gson son = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        final ArrayList<String> words = son.fromJson(wordListAsString, type);
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

                if(typeofwords.equals("Discourse Connectors"))
                {
                    for(String a :words)
                    {
                        matchedTexts.append(a);
                        matchedTexts.append("\n");
                    }
                }else
                {
                    for(String a :offliststrings)
                    {
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
        numberofwords = getIntent().getIntExtra("NumberofWords", 0);
        Uniquewordsnumber = getIntent().getIntExtra("Uniquewordsnumber", 0);
        wordListAsString = getIntent().getStringExtra("matchedlist");
        Gson son = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        final ArrayList<String> words = son.fromJson(wordListAsString, type);
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

               if(typeofwords.equals("Academic Collocations"))
               {
                   for(String a :words)
                   {
                       matchedTexts.append(a);
                       matchedTexts.append("\n");
                   }
               }else
               {
                   for(String a :offliststrings)
                   {
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

    private void NGSL() {
        numberofwords = getIntent().getIntExtra("NumberofWords", 0);
        Uniquewordsnumber = getIntent().getIntExtra("Uniquewordsnumber", 0);

        first500 = getIntent().getStringExtra("First1000");
        second500 = getIntent().getStringExtra("Second1000");
        last = getIntent().getStringExtra("Third1000");
        supplemental = getIntent().getStringExtra("Supplemental");

        Gson son = new Gson();
        Type type = new TypeToken<ArrayList<Word>>() {
        }.getType();

        ArrayList<Word> first1000, second1000, third1000, supplementale;

        first1000 = son.fromJson(first500, type);
        second1000 = son.fromJson(second500, type);
        third1000 = son.fromJson(last, type);
        supplementale = son.fromJson(supplemental, type);

        matchedTexts.setText("");
        int size = first1000.size() + second1000.size() + third1000.size() + supplementale.size();
        String text = "   You use total " + numberofwords + " words and " + Uniquewordsnumber + " unique words. " + "\n   You have " + size + " unique words in NGSL Library";
        matchedTexts.setText(text);
        final TreeMultimap<String, String> first1000multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> second1000multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> third1000multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> supplementalmulti = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> offsetmulti = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        for (Word w : offlistwords) {
            offsetmulti.put(w.getType(), w.getWord());
        }
        for (Word w : supplementale) {
            supplementalmulti.put(w.getType(), w.getWord());
        }
        for (Word w : first1000) {
            first1000multi.put(w.getType(), w.getWord());
        }
        for (Word w : second1000) {
            second1000multi.put(w.getType(), w.getWord());
        }
        for (Word w : third1000) {
            third1000multi.put(w.getType(), w.getWord());
        }
        final ArrayList<TreeMultimap<String, String>> matchedwords = new ArrayList<>();
        matchedwords.add(first1000multi);
        matchedwords.add(second1000multi);
        matchedwords.add(third1000multi);
        matchedwords.add(supplementalmulti);
        matchedwords.add(offsetmulti);

        final ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(first1000.size(), "First 1000"));
        values.add(new PieEntry(second1000.size(), "Second 1000"));
        values.add(new PieEntry(third1000.size(), "Third 1000"));
        values.add(new PieEntry(supplementale.size(), "Supplemenetal Words"));
        values.add(new PieEntry(offlistwords.size(), "OffList"));

        PieDataSet dataSet = new PieDataSet(values, "NGSL Words");
        dataSet.setAutomaticallyDisableSliceSpacing(true);
        dataSet.setSelectionShift(10f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);


        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        Description description = new Description();
        description.setText("Number of NGSL Words");
        description.setTextSize(10f);

        chart.setDescription(description);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleRadius(31f);
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic);
        chart.setCenterText("   You use " + size + " NGSL Words " + " Total used unique words " + Uniquewordsnumber);
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
                NavigableSet<String> set = matchedwords.get(pos).keySet();

                for (String type : set) {
                    if (type.equals("undefined")) {
                        String value = matchedwords.get(pos).get(type).toString();
                        String[] values = value.split("[^A-Za-z]+");
                        for (String val : values) {
                            matchedTexts.append("   " + val + "\n");
                        }
                    } else {
                        matchedTexts.append("   " + type + " " + matchedwords.get(pos).get(type).toString());
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

    private void AcademicWordList() {
        numberofwords = getIntent().getIntExtra("NumberofWords", 0);
        Uniquewordsnumber = getIntent().getIntExtra("Uniquewordsnumber", 0);

        wordListAsString = getIntent().getStringExtra("words_as_string");
        Gson son = new Gson();
        Type type = new TypeToken<ArrayList<Word>>() {
        }.getType();
        ArrayList<Word> wordsList = son.fromJson(wordListAsString, type);


        matchedTexts.setText("");
        String text = "  You use total " + numberofwords + " words and " + Uniquewordsnumber + " unique words. " + "\n   You have " + wordsList.size() + " unique words in Academic Words Library.";
        matchedTexts.setText(text);

        //Multimap for Academic Word List words and it's root words
        final TreeMultimap<String, String> multimap = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        for (Word w : wordsList) {
            multimap.put(w.getType(), w.getWord());
        }

        //And this is for offlist words
        TreeMultimap<String, String> offlistmap = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        for (Word w : offlistwords) {
            offlistmap.put(w.getType(), w.getWord());
        }
        //To access all types like offlist,academic words, for example, piechart number 1 is same for matchedword.get(1)
        final ArrayList<TreeMultimap<String, String>> matchedwords = new ArrayList<>();
        matchedwords.add(multimap);
        matchedwords.add(offlistmap);

        //It's for make piechart values and labels
        final ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(wordsList.size(), "Academic Words"));
        values.add(new PieEntry(offlistwords.size(), "Offlist Words"));

        PieDataSet dataSet = new PieDataSet(values, "Academic Words");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        Description description = new Description();
        description.setText("Number of Academic Words");
        description.setTextSize(10f);

        chart.setDescription(description);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(true);
        chart.setRotationEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setTransparentCircleRadius(31f);
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic);
        chart.setCenterText("You use " + wordsList.size() + " Academic Words " + " Total used words " + Uniquewordsnumber);
        chart.setCenterTextSize(15f);
        chart.getLegend().setTextSize(5f);
        chart.setDrawEntryLabels(true);

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
                NavigableSet<String> set = matchedwords.get(pos).keySet();

                for (String type : set) {
                    if (type.equals("undefined")) {
                        String value = matchedwords.get(pos).get(type).toString();
                        String[] values = value.split("[^A-Za-z]+");
                        for (String val : values) {
                            matchedTexts.append("   " + val + "\n");
                        }
                    } else {
                        matchedTexts.append("   " + type + " " + matchedwords.get(pos).get(type).toString());
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

    private void NewGSL() {

        first500 = getIntent().getStringExtra("First500");
        second500 = getIntent().getStringExtra("Second500");
        last = getIntent().getStringExtra("Last");
        numberofwords = getIntent().getIntExtra("NumberofWords", 0);
        Uniquewordsnumber = getIntent().getIntExtra("Uniquewordsnumber", 0);

        Gson son = new Gson();
        Type type = new TypeToken<List<Word>>() {
        }.getType();

        matchedWords1 = son.fromJson(first500, type);
        matchedWords2 = son.fromJson(second500, type);
        matchedWords3 = son.fromJson(last, type);

        final ArrayList<ArrayList> matchedwords = new ArrayList<>();
        matchedwords.add(matchedWords1);
        matchedwords.add(matchedWords2);
        matchedwords.add(matchedWords3);
        matchedwords.add(offlistwords);

        int size = matchedWords1.size() + matchedWords2.size() + matchedWords3.size();
        String text = "   You use total " + numberofwords + " words and " + Uniquewordsnumber + " unique words. " + "\n   You have " + size + " unique words in NewGsl Library";
        matchedTexts.setText(text);

        final ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(matchedWords1.size(), "First 500"));
        values.add(new PieEntry(matchedWords2.size(), "Second 500"));
        values.add(new PieEntry(matchedWords3.size(), "Remainings"));
        values.add(new PieEntry(offlistwords.size(), "Offlist"));

        PieDataSet dataSet = new PieDataSet(values, "NewGSL Words");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueTextSize(5f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);


        Description description = new Description();
        description.setText("Number of Words depends on NewGsl Library");
        description.setTextSize(10f);
        description.setTextAlign(Paint.Align.RIGHT);

        chart.setDescription(description);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(true);
        chart.setRotationEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleRadius(31f);
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic);
        chart.setCenterText("   You use " + size + " words from New GSL " + " Total used words " + Uniquewordsnumber);
        chart.setCenterTextSize(15f);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setDrawEntryLabels(false);
        chart.getLegend().setTextSize(5f);

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                matchedTexts.scrollTo(0, 0);
                int pos1 = (int) e.getY();
                int pos = (int) h.getX();

                String typeofwords;
                matchedTexts.setText(" ");
                typeofwords = values.get(pos).getLabel();
                Toast.makeText(AnalyzeDisplay.this, typeofwords + " " + pos1, Toast.LENGTH_SHORT).show();
                Collections.sort(matchedwords.get(pos), new Comparator<Word>() {
                    @Override
                    public int compare(Word lhs, Word rhs) {
                        return lhs.getWord().compareToIgnoreCase(rhs.getWord());
                    }
                });
                String text = "   Words from " + typeofwords + "\n";
                matchedTexts.setText(text);
                for (int size = 0; size < matchedwords.get(pos).size(); size++) {
                    matchedTexts.append("   " + matchedwords.get(pos).get(size).toString().toLowerCase());
                    matchedTexts.append("\n");
                }
            }
            @Override
            public void onNothingSelected() {

            }
        });
        chart.setData(data);
    }

    private void AWL() {
        String awl1 = getIntent().getStringExtra("Awl1");
        String awl2 = getIntent().getStringExtra("Awl2");
        String awl3 = getIntent().getStringExtra("Awl3");
        String awl4 = getIntent().getStringExtra("Awl4");
        String awl5 = getIntent().getStringExtra("Awl5");
        String awl6 = getIntent().getStringExtra("Awl6");
        String awl7 = getIntent().getStringExtra("Awl7");
        String awl8 = getIntent().getStringExtra("Awl8");
        String awl9 = getIntent().getStringExtra("Awl9");
        String awl10 = getIntent().getStringExtra("Awl10");
        numberofwords = getIntent().getIntExtra("NumberofWords", 0);
        Uniquewordsnumber = getIntent().getIntExtra("Uniquewordsnumber", 0);
        Gson son = new Gson();
        Type type = new TypeToken<List<Word>>() {
        }.getType();
        ArrayList<Word> awlword1, awlword2, awlword3, awlword4, awlword5, awlword6, awlword7, awlword8, awlword9, awlword10;
        awlword1 = son.fromJson(awl1, type);
        awlword2 = son.fromJson(awl2, type);
        awlword3 = son.fromJson(awl3, type);
        awlword4 = son.fromJson(awl4, type);
        awlword5 = son.fromJson(awl5, type);
        awlword6 = son.fromJson(awl6, type);
        awlword7 = son.fromJson(awl7, type);
        awlword8 = son.fromJson(awl8, type);
        awlword9 = son.fromJson(awl9, type);
        awlword10 = son.fromJson(awl10, type);
        matchedTexts.setText("");
        int size = awlword1.size() + awlword2.size() + awlword3.size() + awlword4.size() + awlword5.size() + awlword6.size() + awlword7.size() + awlword8.size() + awlword9.size() + awlword10.size();
        String text = "   You use total " + numberofwords + " words and " + Uniquewordsnumber + " unique words. " + "\n   You have " + size + " unique words in AWL Library";
        matchedTexts.setText(text);

        final TreeMultimap<String, String> awl1multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> awl2multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> awl3multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> awl4multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> awl5multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> awl6multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> awl7multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> awl8multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> awl9multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> awl10multi = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        final TreeMultimap<String, String> offsetmulti = TreeMultimap.create(Ordering.<String>natural(), Ordering.<String>natural());
        for (Word w : awlword1) {
            awl1multi.put(w.getType(), w.getWord());
        }
        for (Word w : awlword2) {
            awl2multi.put(w.getType(), w.getWord());
        }
        for (Word w : awlword3) {
            awl3multi.put(w.getType(), w.getWord());
        }
        for (Word w : awlword4) {
            awl4multi.put(w.getType(), w.getWord());
        }
        for (Word w : awlword5) {
            awl5multi.put(w.getType(), w.getWord());
        }
        for (Word w : awlword6) {
            awl6multi.put(w.getType(), w.getWord());
        }
        for (Word w : awlword7) {
            awl7multi.put(w.getType(), w.getWord());
        }
        for (Word w : awlword8) {
            awl8multi.put(w.getType(), w.getWord());
        }
        for (Word w : awlword9) {
            awl9multi.put(w.getType(), w.getWord());
        }
        for (Word w : awlword10) {
            awl10multi.put(w.getType(), w.getWord());
        }
        for (Word w : offlistwords) {
            offsetmulti.put(w.getType(), w.getWord());
        }

        final ArrayList<TreeMultimap<String, String>> matchedwords = new ArrayList<>();
        matchedwords.add(awl1multi);
        matchedwords.add(awl2multi);
        matchedwords.add(awl3multi);
        matchedwords.add(awl4multi);
        matchedwords.add(awl5multi);
        matchedwords.add(awl6multi);
        matchedwords.add(awl7multi);
        matchedwords.add(awl8multi);
        matchedwords.add(awl9multi);
        matchedwords.add(awl10multi);
        matchedwords.add(offsetmulti);

        final ArrayList<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(awlword1.size(), "AWL Sublist 1 "));
        values.add(new PieEntry(awlword2.size(), "AWL Sublist 2"));
        values.add(new PieEntry(awlword3.size(), "AWL Sublist 3"));
        values.add(new PieEntry(awlword4.size(), "AWL Sublist 4"));
        values.add(new PieEntry(awlword5.size(), "AWL Sublist 5"));
        values.add(new PieEntry(awlword6.size(), "AWL Sublist 6"));
        values.add(new PieEntry(awlword7.size(), "AWL Sublist 7"));
        values.add(new PieEntry(awlword8.size(), "AWL Sublist 8"));
        values.add(new PieEntry(awlword9.size(), "AWL Sublist 9"));
        values.add(new PieEntry(awlword10.size(), "AWL Sublist 10"));
        values.add(new PieEntry(offlistwords.size(), "OffList"));

        ArrayList<Integer> an = new ArrayList<>();

        for(int a :ColorTemplate.JOYFUL_COLORS)
        {
            an.add(a);
        }
        an.add(Color.BLUE);
        an.add(Color.RED);
        an.add(Color.YELLOW);

        PieDataSet dataSet = new PieDataSet(values, "Academic Word List");
        dataSet.setAutomaticallyDisableSliceSpacing(true);
        dataSet.setSelectionShift(10f);
        dataSet.setColors(an);


        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        Description description = new Description();
        description.setText("Number of AWL Words");
        description.setTextSize(10f);

        chart.setDescription(description);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleRadius(31f);
        chart.animateX(1000, Easing.EasingOption.EaseInOutCubic);
        chart.setCenterText("   You use " + size + " AWL Words " + " Total used unique words " + Uniquewordsnumber);
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
                NavigableSet<String> set = matchedwords.get(pos).keySet();

                for (String type : set) {
                    if (type.equals("undefined")) {
                        String value = matchedwords.get(pos).get(type).toString();
                        String[] values = value.split("[^A-Za-z]+");
                        for (String val : values) {
                            matchedTexts.append("   " + val + "\n");
                        }
                    } else {
                        matchedTexts.append("   " + type + " " + matchedwords.get(pos).get(type).toString());
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
