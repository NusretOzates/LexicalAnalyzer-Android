package com.example.nusret.ifoundthisoninternet;

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
    //Words for Academic Collocations
    ArrayList<String> aclwords = new ArrayList<>();
    Set<String> uniquewords = new HashSet<>(); // Unique offlist words!
    Set<Word> offlistwordsforphrasalverbs = new HashSet<>();
    //Words for Discourse Connectors
    ArrayList<String > discoursewords= new ArrayList<>();
    //EditText strings for offlist
    Set<String> wordset = new HashSet<>();
    HashSet<Word> wordsetlist = new HashSet<>();
    private EditText returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent intent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private Button analyze;
    //Matched Words for Dictionaries
    private Set<Word> matchedfirst500newgslwords = new HashSet<>();
    private Set<Word> matchedfirst5001000newgslwords = new HashSet<>();
    private Set<Word> matchedfirst10002500newgslwords = new HashSet<>();

    private Set<Word> matchedacademicwordlistwords = new HashSet<>();

    private Set<Word> matchedNgslfirst1000 = new HashSet<>();
    private Set<Word> matchedNgslsecond1000= new HashSet<>();
    private Set<Word> matchedNgslthird1000 = new HashSet<>();
    private Set<Word> matchedNgslsupplamental= new HashSet<>();

    private Set<Word> matchedawl1 = new HashSet<>();
    private Set<Word> matchedawl2 = new HashSet<>();
    private Set<Word> matchedawl3 = new HashSet<>();
    private Set<Word> matchedawl4 = new HashSet<>();
    private Set<Word> matchedawl5 = new HashSet<>();
    private Set<Word> matchedawl6 = new HashSet<>();
    private Set<Word> matchedawl7 = new HashSet<>();
    private Set<Word> matchedawl8 = new HashSet<>();
    private Set<Word> matchedawl9 = new HashSet<>();
    private Set<Word> matchedawl10 = new HashSet<>();

    private  Set<Word> matchedPhrasalVerbs = new HashSet<>();

    private Set<String> matchedacademic= new HashSet<>();
    private Set<String> matcheddiscourse= new HashSet<>();

    //Words for Dictionaries
    private BinarySearchTree first500newgsl = new BinarySearchTree();
    private BinarySearchTree first5001000newgsl = new BinarySearchTree();
    private BinarySearchTree first10002500newgsl = new BinarySearchTree();

    private BinarySearchTree academicwordlist = new BinarySearchTree();

    private BinarySearchTree ngslfirst1000 = new BinarySearchTree();
    private BinarySearchTree ngssecond1000 = new BinarySearchTree();
    private BinarySearchTree ngslthird1000 = new BinarySearchTree();
    private BinarySearchTree ngslsupplemental = new BinarySearchTree();

    private BinarySearchTree awl1 = new BinarySearchTree();
    private BinarySearchTree awl2 = new BinarySearchTree();
    private BinarySearchTree awl3 = new BinarySearchTree();
    private BinarySearchTree awl4 = new BinarySearchTree();
    private BinarySearchTree awl5 = new BinarySearchTree();
    private BinarySearchTree awl6 = new BinarySearchTree();
    private BinarySearchTree awl7 = new BinarySearchTree();
    private BinarySearchTree awl8 = new BinarySearchTree();
    private BinarySearchTree awl9 = new BinarySearchTree();
    private BinarySearchTree awl10 = new BinarySearchTree();

    private Set<Word> phrasalverbs = new HashSet<>();

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
        ArrayAdapter<CharSequence>  adapter= ArrayAdapter.createFromResource(MainActivity.this,R.array.dictionairies,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        secenekler.setAdapter(adapter);

        returnedText =findViewById(R.id.textView1);
        progressBar  =findViewById(R.id.progressBar1);
        toggleButton =findViewById(R.id.toggleButton1);

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
        String language =  "en-US";
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);

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
        final  Intent toAnalyze = new Intent(MainActivity.this,AnalyzeDisplay.class);
        secenekler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("New-GSL"))
                {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect",1);
                            readEditTextForNewGsl();
                            getReadyForNewGSL(toAnalyze);
                        }
                    });

                }else if(parent.getItemAtPosition(position).equals("Academic Vocabulary List"))
                {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect",2);
                            readEditTextForAcademicWordList();
                            getReadyforAcademicWordListScan(toAnalyze);
                        }
                    });

                }else if(parent.getItemAtPosition(position).equals("General Service List"))
                {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect",3);
                            readEditTextForNGSL();
                            getReadyForNGSL(toAnalyze);
                        }
                    });

                } else if (parent.getItemAtPosition(position).equals("Academic Word List"))
                {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect",4);
                            readEditTextForAWLibrary();
                            getReadyForAWL(toAnalyze);
                        }
                    });

                }else if(parent.getItemAtPosition(position).equals("Academic Collocation List"))
                {

                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect",5);
                            readEditTextForAcademicCollocationList();
                            getReadyForAcademicCollocation(toAnalyze);
                        }
                    });

                }else if(parent.getItemAtPosition(position).equals("Discourse Connectors"))
                {
                    analyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toAnalyze.putExtra("DictionarySelect",6);
                            readEditTextForDiscourseConnectors();
                            getReadyForDiscourseConnectors(toAnalyze);
                        }
                    });
                }else if(parent.getItemAtPosition(position).equals("Phrasal Verbs"))
                {
                  analyze.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          toAnalyze.putExtra("DictionarySelect",7);
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
    private  void getReadyforAcademicWordListScan(Intent i)
    {

        Gson gsonn = new Gson();
        String gsonWords = gsonn.toJson(matchedacademicwordlistwords);
        String offlist = gsonn.toJson(wordsetlist);
        i.putExtra("words_as_string",gsonWords);
        i.putExtra("NumberofWords",numberofwords);
        i.putExtra("Uniquewordsnumber",numberofuniqueswords);
        i.putExtra("OffSet",offlist);
        startActivity(i);
    }

    //Üsttekinin aynısı
    public void getReadyForNewGSL(Intent i)
    {
        Gson son = new Gson();
        String first500 =son.toJson(matchedfirst500newgslwords);
        String second500 = son.toJson(matchedfirst5001000newgslwords);
        String last = son.toJson(matchedfirst10002500newgslwords);
        String offlist = son.toJson(wordsetlist);
        i.putExtra("First500",first500);
        i.putExtra("Second500",second500);
        i.putExtra("Last",last);
        i.putExtra("NumberofWords",numberofwords);
        i.putExtra("Uniquewordsnumber",numberofuniqueswords);
        i.putExtra("OffSet",offlist);

        startActivity(i);
    }

    private void getReadyForNGSL(Intent i)
    {
        Gson son = new Gson();
        String first1000 =son.toJson(matchedNgslfirst1000);
        String second1000 = son.toJson(matchedNgslsecond1000);
        String third1000 = son.toJson(matchedNgslthird1000);
        String suplemental = son.toJson(matchedNgslsupplamental);
        String offlist = son.toJson(wordsetlist);
        i.putExtra("First1000",first1000);
        i.putExtra("Second1000",second1000);
        i.putExtra("Third1000",third1000);
        i.putExtra("Supplemental",suplemental);
        i.putExtra("NumberofWords",numberofwords);
        i.putExtra("Uniquewordsnumber",numberofuniqueswords);
        i.putExtra("OffSet",offlist);
        startActivity(i);

    }

    private void getReadyForAcademicCollocation(Intent i)
    {
        Gson son = new Gson();
        String matched = son.toJson(matchedacademic);
        String offlist = son.toJson(uniquewords);
        i.putExtra("matchedlist" ,matched);
        i.putExtra("NumberofWords",numberofwords);
        i.putExtra("Uniquewordsnumber",numberofuniqueswords);
        i.putExtra("OffSet",offlist);
        startActivity(i);
    }

    private void getReadyForDiscourseConnectors(Intent i)
    {
        Gson son = new Gson();
        String matched = son.toJson(matcheddiscourse);
        String offlist = son.toJson(uniquewords);
        i.putExtra("matchedlist" ,matched);
        i.putExtra("NumberofWords",numberofwords);
        i.putExtra("Uniquewordsnumber",numberofuniqueswords);
        i.putExtra("OffSet",offlist);
        startActivity(i);
    }

    private void getReadyForAWL(Intent i)
    {
        Gson son = new Gson();
        String AWL1 = son.toJson(matchedawl1);
        String AWL2 = son.toJson(matchedawl2);
        String AWL3 = son.toJson(matchedawl3);
        String AWL4 = son.toJson(matchedawl4);
        String AWL5 = son.toJson(matchedawl5);
        String AWL6 = son.toJson(matchedawl6);
        String AWL7 = son.toJson(matchedawl7);
        String AWL8 = son.toJson(matchedawl8);
        String AWL9 = son.toJson(matchedawl9);
        String AWL10 = son.toJson(matchedawl10);
        String offset = son.toJson(wordsetlist);
        i.putExtra("Awl1",AWL1);
        i.putExtra("Awl2",AWL2);
        i.putExtra("Awl3",AWL3);
        i.putExtra("Awl4",AWL4);
        i.putExtra("Awl5",AWL5);
        i.putExtra("Awl6",AWL6);
        i.putExtra("Awl7",AWL7);
        i.putExtra("Awl8",AWL8);
        i.putExtra("Awl9",AWL9);
        i.putExtra("Awl10",AWL10);
        i.putExtra("OffSet",offset);
        i.putExtra("NumberofWords",numberofwords);
        i.putExtra("Uniquewordsnumber",numberofuniqueswords);
        startActivity(i);
    }

    private void getReadyForPhrasalVerbs(Intent i)
    {
        Gson son = new Gson();
        String matched = son.toJson(matchedPhrasalVerbs);
        String offlist = son.toJson(offlistwordsforphrasalverbs);
        i.putExtra("matchedlist" ,matched);
        i.putExtra("NumberofWords",numberofwords);
        i.putExtra("Uniquewordsnumber",numberofuniqueswords);
        i.putExtra("OffSet",offlist);
        startActivity(i);
    }

    //CSV to BinarySearchTree
    private void readNewGSLLibrary()
    {
        InputStream is = getResources().openRawResource(R.raw.first500);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line;

        try {
            while((line = reader.readLine()) != null)
            {
                String[] tokens = line.split(";");//Phrasal verb/boşluk gelirse sıçar

                String word = tokens[0].replaceAll("\\s+","");
              //  String type = tokens[1].replaceAll("\\s+","");

                Word new_word = new Word(word);

                first500newgsl.insert(new_word);

            }
            is = getResources().openRawResource(R.raw.first5001000);
            reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            while((line = reader.readLine()) != null)
            {
                String[] tokens = line.split(";");//Phrasal verb/boşluk gelirse sıçar

                String word = tokens[0].replaceAll("\\s+","");
//                String type = tokens[1].replaceAll("\\s+",l"");

                Word new_word = new Word(word);

                first5001000newgsl.insert(new_word);

            }
             is = getResources().openRawResource(R.raw.first10002500);
            reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );
            while((line = reader.readLine()) != null)
            {
                String[] tokens = line.split(";");//Phrasal verb/boşluk gelirse sıçar

                String word = tokens[0].replaceAll("\\s+","");
//                String type = tokens[1].replaceAll("\\s+","");

                Word new_word = new Word(word);
                first10002500newgsl.insert(new_word);

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
                 String root = tokens[1].replaceAll("\\s+","");

                Word new_word = new Word(word,root);

               academicwordlist.insert(new_word);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //CSV to BinarySearchTree
    private void readNGSLWordLibray()
    {
        InputStream academicwords = getResources().openRawResource(R.raw.first1000ngsl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(academicwords, Charset.forName("UTF-8")));

        String line;
        int column =0;
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");


                while(column<tokens.length)
                {
                    String word = tokens[column].replaceAll("\\s+", "");
                    String root = tokens[0].replaceAll("\\s+","");

                    Word new_word = new Word(word,root);

                    ngslfirst1000.insert(new_word);
                    column++;
                }
                column=0;

            }
            column =0;
            academicwords = getResources().openRawResource(R.raw.ngssecond1000);
            reader = new BufferedReader(new InputStreamReader(academicwords, Charset.forName("UTF-8")));

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                while(column<tokens.length)
                {
                    String word = tokens[column].replaceAll("\\s+", "");
                    String root = tokens[0].replaceAll("\\s+","");

                    Word new_word = new Word(word,root);

                    ngssecond1000.insert(new_word);
                    column++;
                }
                column=0;

            }

            column =0;
            academicwords = getResources().openRawResource(R.raw.ngslthird1000);
            reader = new BufferedReader(new InputStreamReader(academicwords, Charset.forName("UTF-8")));

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                while(column<tokens.length)
                {
                    String word = tokens[column].replaceAll("\\s+", "");
                    String root = tokens[0].replaceAll("\\s+","");

                    Word new_word = new Word(word,root);

                    ngslthird1000.insert(new_word);
                    column++;
                }
                column=0;

            }

            column =0;
            academicwords = getResources().openRawResource(R.raw.ngslsupplamental);
            reader = new BufferedReader(new InputStreamReader(academicwords, Charset.forName("UTF-8")));

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                while(column<tokens.length)
                {
                    String word = tokens[column].replaceAll("\\s+", "");
                    String root = tokens[0].replaceAll("\\s+","");

                    Word new_word = new Word(word,root);

                    ngslsupplemental.insert(new_word);
                    column++;
                }
                column=0;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read_AWL_Library()
    {
        String line;
        InputStream awlreader;
        BufferedReader reader;

        awlreader= getResources().openRawResource(R.raw.awlsublist1);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+","");
                String word = tokens[1].replaceAll("\\s+","");

                Word new_word  = new Word(word,root);
                awl1.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
///////////////////////////////////////////////////////////////////////////////////////////
        awlreader= getResources().openRawResource(R.raw.awlsublist2);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+","");
                String word = tokens[1].replaceAll("\\s+","");

                Word new_word  = new Word(word,root);
                awl2.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
/////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader= getResources().openRawResource(R.raw.awlsublist3);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+","");
                String word = tokens[1].replaceAll("\\s+","");

                Word new_word  = new Word(word,root);
                awl3.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
 /////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader= getResources().openRawResource(R.raw.awlsublist4);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+","");
                String word = tokens[1].replaceAll("\\s+","");

                Word new_word  = new Word(word,root);
                awl4.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
 //////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader= getResources().openRawResource(R.raw.awlsublist5);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+","");
                String word = tokens[1].replaceAll("\\s+","");

                Word new_word  = new Word(word,root);
                awl5.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
 /////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader= getResources().openRawResource(R.raw.awlsublist6);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+","");
                String word = tokens[1].replaceAll("\\s+","");

                Word new_word  = new Word(word,root);
                awl6.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
 //////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader= getResources().openRawResource(R.raw.awlsublist7);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+","");
                String word = tokens[1].replaceAll("\\s+","");

                Word new_word  = new Word(word,root);
                awl7.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
///////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader= getResources().openRawResource(R.raw.awlsublist8);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+","");
                String word = tokens[1].replaceAll("\\s+","");

                Word new_word  = new Word(word,root);
                awl8.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
////////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader= getResources().openRawResource(R.raw.awlsublist9);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+","");
                String word = tokens[1].replaceAll("\\s+","");

                Word new_word  = new Word(word,root);
                awl9.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
////////////////////////////////////////////////////////////////////////////////////////////////////
        awlreader= getResources().openRawResource(R.raw.awlsublist10);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].replaceAll("\\s+","");
                String word = tokens[1].replaceAll("\\s+","");

                Word new_word  = new Word(word,root);
                awl10.insert(new_word);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read_Phrasal_Verbs()
    { String line;
        InputStream awlreader;
        BufferedReader reader;

        awlreader= getResources().openRawResource(R.raw.frequentphrasalverbs);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String[] tokens = line.split(";");
                String root = tokens[0].toLowerCase();
                String word = tokens[1].toLowerCase();

                Word new_word  = new Word(word,root);
                phrasalverbs.add(new_word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void read_Academic_Collocation_List()
    {
        String line;
        InputStream awlreader;
        BufferedReader reader;

        awlreader= getResources().openRawResource(R.raw.academiccollocation);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String tokens = line;
                aclwords.add(tokens);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void read_Discourse_Connector_List()
    {
        String line;
        InputStream awlreader;
        BufferedReader reader;

        awlreader= getResources().openRawResource(R.raw.discourseconnnector);
        reader= new BufferedReader(new InputStreamReader(awlreader,Charset.forName("UTF-8")));

        try
        {
            while((line=reader.readLine())!=null)
            {
                String tokens = line;
                discoursewords.add(tokens);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readEditTextForNGSL()
    {
        String text = returnedText.getText().toString().toLowerCase();
        String[] words = text.split("[^A-Za-z]+");
        numberofwords = words.length;
        wordset.addAll(Arrays.asList(words));
        for(String w :wordset)
        {
            wordsetlist.add(new Word(w));
        }
        numberofuniqueswords = wordset.size();
        for(String word : wordset)
        {
            ngslfirst1000.find(word,matchedNgslfirst1000,wordsetlist);
            ngssecond1000.find(word,matchedNgslsecond1000,wordsetlist);
            ngslthird1000.find(word,matchedNgslthird1000,wordsetlist);
            ngslsupplemental.find(word,matchedNgslsupplamental,wordsetlist);
        }
    }

    //Read EditTex and find matched words for dictionary
    private void readEditTextForNewGsl()
    {
        String text = returnedText.getText().toString().toLowerCase();
        String[] words = text.split("[^A-Za-z]+");
        numberofwords = words.length;

        wordset.addAll(Arrays.asList(words));
        for(String w :wordset)
        {
            wordsetlist.add(new Word(w));
        }
        numberofuniqueswords = wordset.size();
        for(String word : wordset)
        {
            first500newgsl.find(word,matchedfirst500newgslwords,wordsetlist);
            first5001000newgsl.find(word,matchedfirst5001000newgslwords,wordsetlist);
            first10002500newgsl.find(word,matchedfirst10002500newgslwords,wordsetlist);
        }
    }

    //Read EditTex and find matched words for dictionary
    private  void readEditTextForAcademicWordList()
    {
        String text = returnedText.getText().toString().toLowerCase();

        String[] words = text.split("[^A-Za-z]+");
        numberofwords = words.length;
        wordset.addAll(Arrays.asList(words));
        numberofuniqueswords = wordset.size();
        for(String w :wordset)
        {
            wordsetlist.add(new Word(w));
        }
        for(String myword : wordset)
        {
            academicwordlist.find(myword,matchedacademicwordlistwords,wordsetlist);
        }
    }

    private  void readEditTextForAWLibrary()
    {
        String text = returnedText.getText().toString().toLowerCase();
        String[] words = text.split("[^A-Za-z]+");
        numberofwords = words.length;
        wordset.addAll(Arrays.asList(words));
        numberofuniqueswords = wordset.size();
        for(String w :wordset)
        {
            wordsetlist.add(new Word(w));
        }

        for(String word : wordset)
        {
            awl1.find(word,matchedawl1,wordsetlist);
            awl2.find(word,matchedawl2,wordsetlist);
            awl3.find(word,matchedawl3,wordsetlist);
            awl4.find(word,matchedawl4,wordsetlist);
            awl5.find(word,matchedawl5,wordsetlist);
            awl6.find(word,matchedawl6,wordsetlist);
            awl7.find(word,matchedawl7,wordsetlist);
            awl8.find(word,matchedawl8,wordsetlist);
            awl9.find(word,matchedawl9,wordsetlist);
            awl10.find(word,matchedawl10,wordsetlist);
        }
    }

    private void readEditTextForPhrasalVerbs()
    {
        String text = returnedText.getText().toString().toLowerCase();
        String[] words = text.split("[^A-Za-z]+");
        numberofwords = words.length;
        wordset.addAll(Arrays.asList(words));
        numberofuniqueswords = wordset.size();
        for(Word a :phrasalverbs)
        {
            if(text.contains(a.word))
            {
                matchedPhrasalVerbs.add(a);
            }
            text = text.replace(a.word,"");

        }
        String[] an = text.split("[^A-Za-z]+");
        for(String a : an)
        {
            offlistwordsforphrasalverbs.add(new Word(a));
        }

    }

    private void readEditTextForAcademicCollocationList()
    {
        String text = returnedText.getText().toString().toLowerCase();
        String[] words = text.split("[^A-Za-z]+");
        numberofwords = words.length;
        wordset.addAll(Arrays.asList(words));
        numberofuniqueswords = wordset.size();
      for(String a :aclwords)
      {
          if(text.contains(a))
          {
              matchedacademic.add(a);
              text= text.replace(a,"");
          }
      }
      String[] an = text.split("[^A-Za-z]+");
      uniquewords.addAll(Arrays.asList(an));
    }

    private void readEditTextForDiscourseConnectors()
    {
        String text = returnedText.getText().toString().toLowerCase();
        String[] words = text.split("[^A-Za-z]+");
        numberofwords = words.length;
        wordset.addAll(Arrays.asList(words));
        numberofuniqueswords = wordset.size();
        for(String a :discoursewords)
        {
            if(text.contains(a))
            {
                matcheddiscourse.add(a);
                text= text.replace(a,"");
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
            text= matches.get(0);
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
