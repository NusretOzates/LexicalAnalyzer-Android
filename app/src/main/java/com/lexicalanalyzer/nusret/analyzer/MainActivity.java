package com.lexicalanalyzer.nusret.analyzer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private static final int REQUEST_RECORD_PERMISSION = 100;
    int numberofwords;
    int numberofuniqueswords;

    Set<String> uniquewords = new HashSet<>(); // Unique offlist words!
    Set<Word> offlistwordsforphrasalverbs = new HashSet<>();

    //EditText strings for offlist
    Set<String> wordset = new HashSet<>();
    HashSet<Word> wordsetlist = new HashSet<>();
    ArrayList<String> aclwordsList = new ArrayList<>();
    ArrayList<String> discoursewordslist = new ArrayList<>();
    private EditText returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent intent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private Button analyze;
    //Matched Words for Dictionaries
    private Set<Word> matchednewgslwords = new HashSet<>();
    private Set<Word> matchedacademicwordlistwords = new HashSet<>();
    private Set<Word> matchedNgsl = new HashSet<>();
    private Set<Word> matchedawlList = new HashSet<>();
    private Set<Word> matchedPhrasalVerbs = new HashSet<>();
    private Set<String> matchedacademic = new HashSet<>();
    private Set<String> matcheddiscourse = new HashSet<>();
    //Words for Dictionaries
    private BinarySearchTree newGslList = new BinarySearchTree();
    private BinarySearchTree academicwordlist = new BinarySearchTree();
    private BinarySearchTree ngslList = new BinarySearchTree();
    private BinarySearchTree awlList = new BinarySearchTree();
    private Set<Word> phrasalverbslist = new HashSet<>();

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Reading Libraries
        new Thread(new Runnable() {
            public void run() {
                readNewGSLLibrary();
                readAcademicWordsLibrary();
                readNGSLWordLibray();
                read_AWL_Library();
                read_Academic_Collocation_List();
                read_Discourse_Connector_List();
                read_Phrasal_Verbs();
            }
        }
        ).start();
        Spinner secenekler = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.dictionairies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        secenekler.setAdapter(adapter);

        returnedText = findViewById(R.id.textView1);
        progressBar = findViewById(R.id.progressBar1);
        toggleButton = findViewById(R.id.toggleButton1);

        Button refresh = findViewById(R.id.Refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnedText.setText(" ");
            }
        });

        progressBar.setVisibility(View.INVISIBLE);
        //Ses dinleme ve algılayıp yazmak için
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        String language = "tr-TR";
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        //Ses kaydetmeye başlamak içib
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    ActivityCompat.requestPermissions
                            (MainActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });


        //BURADAN AŞAĞIYA SÖZLÜK SEÇİMİ
        analyze = findViewById(R.id.analyzebttn);
        final Intent toAnalyze = new Intent(MainActivity.this, AnalyzeDisplay.class);
        secenekler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("New-GSL")) {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect", 1);
                            readEditText(1);
                            getReadyForNewGSL(toAnalyze);
                        }
                    });

                } else if (parent.getItemAtPosition(position).equals("Academic Vocabulary List")) {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect", 2);
                            readEditText(2);
                            getReadyforAcademicWordListScan(toAnalyze);
                        }
                    });

                } else if (parent.getItemAtPosition(position).equals("General Service List")) {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect", 3);
                            readEditText(0);
                            getReadyForNGSL(toAnalyze);
                        }
                    });

                } else if (parent.getItemAtPosition(position).equals("Academic Word List")) {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect", 4);
                            readEditText(3);
                            getReadyForAWL(toAnalyze);
                        }
                    });

                } else if (parent.getItemAtPosition(position).equals("Academic Collocation List")) {

                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect", 5);
                            readEditTextStringSet(matchedacademic);
                            getReadyForAcademicCollocation(toAnalyze);
                        }
                    });

                } else if (parent.getItemAtPosition(position).equals("Discourse Connectors")) {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect", 6);
                            readEditTextStringSet(matcheddiscourse);
                            getReadyForDiscourseConnectors(toAnalyze);
                        }
                    });
                } else if (parent.getItemAtPosition(position).equals("Phrasal Verbs")) {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect", 7);
                            readEditTextForPhrasalVerbs();
                            getReadyForPhrasalVerbs(toAnalyze);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //To Send found AcademicWordList words,unique words and all words and start analyze
    private void getReadyforAcademicWordListScan(Intent i) {

        Gson gsonn = new Gson();
        String gsonWords = gsonn.toJson(matchedacademicwordlistwords);
        String offlist = gsonn.toJson(wordsetlist);
        i.putExtra("MatchedList", gsonWords);
        i.putExtra("NumberofWords", numberofwords);
        i.putExtra("Uniquewordsnumber", numberofuniqueswords);
        i.putExtra("OffSet", offlist);
        startActivity(i);
    }

    //Üsttekinin aynısı
    private void getReadyForNewGSL(Intent i) {
        Gson son = new Gson();
        String first500 = son.toJson(matchednewgslwords);
        String offlist = son.toJson(wordsetlist);
        i.putExtra("MatchedList", first500);
        i.putExtra("NumberofWords", numberofwords);
        i.putExtra("Uniquewordsnumber", numberofuniqueswords);
        i.putExtra("OffSet", offlist);

        startActivity(i);
    }

    private void getReadyForNGSL(Intent i) {
        Gson son = new Gson();
        String first1000 = son.toJson(matchedNgsl);
        String offlist = son.toJson(wordsetlist);
        i.putExtra("MatchedList", first1000);
        i.putExtra("NumberofWords", numberofwords);
        i.putExtra("Uniquewordsnumber", numberofuniqueswords);
        i.putExtra("OffSet", offlist);
        startActivity(i);

    }

    private void getReadyForAcademicCollocation(Intent i) {
        Gson son = new Gson();
        String matched = son.toJson(matchedacademic);
        String offlist = son.toJson(uniquewords);
        i.putExtra("MatchedList", matched);
        i.putExtra("NumberofWords", numberofwords);
        i.putExtra("Uniquewordsnumber", numberofuniqueswords);
        i.putExtra("OffSet", offlist);
        startActivity(i);
    }

    private void getReadyForDiscourseConnectors(Intent i) {
        Gson son = new Gson();
        String matched = son.toJson(matcheddiscourse);
        String offlist = son.toJson(uniquewords);
        i.putExtra("MatchedList", matched);
        i.putExtra("NumberofWords", numberofwords);
        i.putExtra("Uniquewordsnumber", numberofuniqueswords);
        i.putExtra("OffSet", offlist);
        startActivity(i);
    }

    private void getReadyForAWL(Intent i) {
        Gson son = new Gson();
        String AWL1 = son.toJson(matchedawlList);
        String offset = son.toJson(wordsetlist);
        i.putExtra("MatchedList", AWL1);
        i.putExtra("OffSet", offset);
        i.putExtra("NumberofWords", numberofwords);
        i.putExtra("Uniquewordsnumber", numberofuniqueswords);
        startActivity(i);
    }

    private void getReadyForPhrasalVerbs(Intent i) {
        Gson son = new Gson();
        String matched = son.toJson(matchedPhrasalVerbs);
        String offlist = son.toJson(offlistwordsforphrasalverbs);
        i.putExtra("MatchedList", matched);
        i.putExtra("NumberofWords", numberofwords);
        i.putExtra("Uniquewordsnumber", numberofuniqueswords);
        i.putExtra("OffSet", offlist);
        startActivity(i);
    }

    //CSV to BinarySearchTree
    private void readNewGSLLibrary() {
        InputStream is = getResources().openRawResource(R.raw.first500);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");//Phrasal verb/boşluk gelirse sıçar

                String word = tokens[0].replaceAll("\\s+", "");
                //  String type = tokens[1].replaceAll("\\s+","");

                Word new_word = new Word(word);

                newGslList.insert(new_word);

            }
            is = getResources().openRawResource(R.raw.first5001000);
            reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");//Phrasal verb/boşluk gelirse sıçar

                String word = tokens[0].replaceAll("\\s+", "");
//                String type = tokens[1].replaceAll("\\s+",l"");

                Word new_word = new Word(word);

                newGslList.insert(new_word);

            }
            is = getResources().openRawResource(R.raw.first10002500);
            reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");//Phrasal verb/boşluk gelirse sıçar

                String word = tokens[0].replaceAll("\\s+", "");
//                String type = tokens[1].replaceAll("\\s+","");

                Word new_word = new Word(word);
                newGslList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //CSV to BinarySearchTree
    private void readAcademicWordsLibrary() {
        InputStream academicwords = getResources().openRawResource(R.raw.academicwords);
        BufferedReader reader = new BufferedReader(new InputStreamReader(academicwords, Charset.forName("UTF-8")));

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");//Phrasal verb/boşluk gelirse sıçar

                String word = tokens[0].replaceAll("\\s+", "");
                String root = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);

                academicwordlist.insert(new_word);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //CSV to BinarySearchTree
    private void readNGSLWordLibray() {
        InputStream academicwords = getResources().openRawResource(R.raw.first1000ngsl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(academicwords, Charset.forName("UTF-8")));

        String line;
        int column = 0;
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");


                while (column < tokens.length) {
                    String word = tokens[column].replaceAll("\\s+", "");
                    String root = tokens[0].replaceAll("\\s+", "");

                    Word new_word = new Word(word, root);

                    ngslList.insert(new_word);
                    column++;
                }
                column = 0;

            }
            column = 0;
            academicwords = getResources().openRawResource(R.raw.ngssecond1000);
            reader = new BufferedReader(new InputStreamReader(academicwords, Charset.forName("UTF-8")));

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                while (column < tokens.length) {
                    String word = tokens[column].replaceAll("\\s+", "");
                    String root = tokens[0].replaceAll("\\s+", "");

                    Word new_word = new Word(word, root);

                    ngslList.insert(new_word);
                    column++;
                }
                column = 0;

            }

            column = 0;
            academicwords = getResources().openRawResource(R.raw.ngslthird1000);
            reader = new BufferedReader(new InputStreamReader(academicwords, Charset.forName("UTF-8")));

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                while (column < tokens.length) {
                    String word = tokens[column].replaceAll("\\s+", "");
                    String root = tokens[0].replaceAll("\\s+", "");

                    Word new_word = new Word(word, root);

                    ngslList.insert(new_word);
                    column++;
                }
                column = 0;

            }

            column = 0;
            academicwords = getResources().openRawResource(R.raw.ngslsupplamental);
            reader = new BufferedReader(new InputStreamReader(academicwords, Charset.forName("UTF-8")));

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                while (column < tokens.length) {
                    String word = tokens[column].replaceAll("\\s+", "");
                    String root = tokens[0].replaceAll("\\s+", "");

                    Word new_word = new Word(word, root);

                    ngslList.insert(new_word);
                    column++;
                }
                column = 0;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read_AWL_Library() {
        String line;
        InputStream awlreader;
        BufferedReader reader;

        awlreader = getResources().openRawResource(R.raw.awlsublist1);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+", "");
                String word = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);
                awlList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
///////////////////////////////////////////////////////////////////////////////////////////
        awlreader = getResources().openRawResource(R.raw.awlsublist2);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+", "");
                String word = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);
                awlList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
/////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader = getResources().openRawResource(R.raw.awlsublist3);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+", "");
                String word = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);
                awlList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader = getResources().openRawResource(R.raw.awlsublist4);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+", "");
                String word = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);
                awlList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader = getResources().openRawResource(R.raw.awlsublist5);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+", "");
                String word = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);
                awlList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader = getResources().openRawResource(R.raw.awlsublist6);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+", "");
                String word = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);
                awlList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader = getResources().openRawResource(R.raw.awlsublist7);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+", "");
                String word = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);
                awlList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
///////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader = getResources().openRawResource(R.raw.awlsublist8);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+", "");
                String word = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);
                awlList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
////////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader = getResources().openRawResource(R.raw.awlsublist9);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+", "");
                String word = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);
                awlList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
////////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader = getResources().openRawResource(R.raw.awlsublist10);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+", "");
                String word = tokens[1].replaceAll("\\s+", "");

                Word new_word = new Word(word, root);
                awlList.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read_Phrasal_Verbs() {
        String line;
        InputStream awlreader;
        BufferedReader reader;

        awlreader = getResources().openRawResource(R.raw.frequentphrasalverbs);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                String root = tokens[0].toLowerCase();
                String word = tokens[1].toLowerCase();

                Word new_word = new Word(word, root);
                phrasalverbslist.add(new_word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void read_Academic_Collocation_List() {
        String line;
        InputStream awlreader;
        BufferedReader reader;

        awlreader = getResources().openRawResource(R.raw.academiccollocation);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String tokens = line;
                aclwordsList.add(tokens);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void read_Discourse_Connector_List() {
        String line;
        InputStream awlreader;
        BufferedReader reader;

        awlreader = getResources().openRawResource(R.raw.discourseconnnector);
        reader = new BufferedReader(new InputStreamReader(awlreader, Charset.forName("UTF-8")));

        try {
            while ((line = reader.readLine()) != null) {
                String tokens = line;
                discoursewordslist.add(tokens);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readEditText(int id) {
        String text = returnedText.getText().toString().toLowerCase();
        String[] words = text.split("[^A-Za-z]+");
        numberofwords = words.length;
        wordset.addAll(Arrays.asList(words));
        for (String w : wordset) {
            wordsetlist.add(new Word(w));
        }
        numberofuniqueswords = wordset.size();
        for (String word : wordset) {
            switch (id) {
                case 0:
                    ngslList.find(word, matchedNgsl, wordsetlist);
                    break;
                case 1:
                    newGslList.find(word, matchednewgslwords, wordsetlist);
                case 2:
                    academicwordlist.find(word, matchedacademicwordlistwords, wordsetlist);
                    break;
                case 3:
                    awlList.find(word, matchedawlList, wordsetlist);
                    break;

            }
        }
    }

    private void readEditTextForPhrasalVerbs() {
        String text = returnedText.getText().toString().toLowerCase();
        String[] words = text.split("[^A-Za-z]+");
        numberofwords = words.length;
        wordset.addAll(Arrays.asList(words));
        numberofuniqueswords = wordset.size();
        for (Word a : phrasalverbslist) {
            if (text.contains(a.word)) {
                matchedPhrasalVerbs.add(a);
            }
            text = text.replace(a.word, "");

        }
        String[] an = text.split("[^A-Za-z]+");
        for (String a : an) {
            offlistwordsforphrasalverbs.add(new Word(a));
        }

    }

    private void readEditTextStringSet(Set<String> matched) {
        String text = returnedText.getText().toString().toLowerCase();
        String[] words = text.split("[^A-Za-z]+");
        numberofwords = words.length;
        wordset.addAll(Arrays.asList(words));
        numberofuniqueswords = wordset.size();
        for (String a : aclwordsList) {
            if (text.contains(a)) {
                matched.add(a);
                text = text.replace(a, "");
            }
        }
        String[] an = text.split("[^A-Za-z]+");
        uniquewords.addAll(Arrays.asList(an));
    }


    //region All about voice recording
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean isPermitted = false;
        if (requestCode == REQUEST_RECORD_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                String permission = permissions[i];

                isPermitted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    returnedText.setText("Permission Denied");
                }
            }

            if (isPermitted) {
                speech.startListening(intent);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {

        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onRmsChanged(float v) {
        Log.i(LOG_TAG, "onRmsChanged: " + v);
        progressBar.setProgress((int) v);


    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(LOG_TAG, "onBufferReceived: " + bytes);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int i) {
        String errorMessage = getErrorText(i);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        toggleButton.setChecked(false);
    }

    @Override
    public void onResults(Bundle bundle) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = bundle
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text;
        text = matches.get(0).replace("artı", "+").replace("eksi", "-").replace("noktalı virgül", ";").replace("virgül", ",")
                .replace("çift nokta", ":");
        returnedText.append(text);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");
    }
    //endregion
}
