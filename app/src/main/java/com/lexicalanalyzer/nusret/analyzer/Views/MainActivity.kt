package com.lexicalanalyzer.nusret.analyzer.Views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.lexicalanalyzer.nusret.analyzer.R
import com.lexicalanalyzer.nusret.analyzer.Utils.BinarySearchTree
import com.lexicalanalyzer.nusret.analyzer.Utils.ViewChanger
import com.lexicalanalyzer.nusret.analyzer.Utils.Word
import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), RecognitionListener {


    // Reference for Base Link
    var storage = FirebaseStorage.getInstance()
    var baseReferenceForFiles = storage.reference


    //is speech listening permitted ?
    var isPermitted = false

    private var numberofwords: Int = 0
    private var numberofuniqueswords: Int = 0

    private var uniquewords: MutableSet<String> = HashSet() // Unique offlist words!
    private var offlistwordsforphrasalverbs: MutableSet<Word> = HashSet()

    //EditText strings for offlist
    private var wordset: MutableSet<String> = HashSet()
    private var wordsetlist = HashSet<Word>()
    private var aclwordsList = ArrayList<String>()
    private var discoursewordslist = ArrayList<String>()
    private var returnedText: EditText? = null
    private var toggleButton: ToggleButton? = null
    private var progressBar: ProgressBar? = null
    private var speech: SpeechRecognizer? = null

    private val LOG_TAG = "VoiceRecognitionAct"
    private var analyze: Button? = null
    //Matched Words for Dictionaries
    private val matchednewgslwords = HashSet<Word>()
    private val matchedacademicwordlistwords = HashSet<Word>()
    private val matchedNgsl = HashSet<Word>()
    private val matchedawlList = HashSet<Word>()
    private val matchedPhrasalVerbs = HashSet<Word>()
    private val matchedacademic = HashSet<String>()
    private val matcheddiscourse = HashSet<String>()
    //Words for Dictionaries
    private val newGslList = BinarySearchTree()
    private val academicwordlist = BinarySearchTree()
    private val ngslList = BinarySearchTree()
    private val awlList = BinarySearchTree()
    private val phrasalverbslist = HashSet<Word>()

    fun clearAll() {
        numberofuniqueswords = 0
        numberofwords = 0
        uniquewords.clear()
        offlistwordsforphrasalverbs.clear()
        wordset.clear()
        wordsetlist.clear()
        aclwordsList.clear()
        discoursewordslist.clear()
        matchednewgslwords.clear()
        matchedacademicwordlistwords.clear()
        matchedNgsl.clear()
        matchedawlList.clear()
        matchedPhrasalVerbs.clear()
        matchedacademic.clear()
        matcheddiscourse.clear()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        //Reading Libraries
        Thread(Runnable {
            readNewGSLLibrary()
            readAcademicWordsLibrary()
            readNGSLWordLibray()
            read_AWL_Library()
            readAcademicCollocationList()
            readDiscourseConnectorList()
            read_Phrasal_Verbs()
        }
        ).start()
        val secenekler = findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(this@MainActivity, R.array.dictionairies, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        secenekler.adapter = adapter

        returnedText = findViewById(R.id.textView1)
        progressBar = findViewById(R.id.progressBar1)
        toggleButton = findViewById(R.id.toggleButton1)

        val refresh = findViewById<Button>(R.id.Refresh)
        refresh.setOnClickListener { returnedText!!.setText(" ") }

        progressBar!!.visibility = View.INVISIBLE
        //Ses dinleme ve algılayıp yazmak için
        speech = SpeechRecognizer.createSpeechRecognizer(this)
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this))
        speech!!.setRecognitionListener(this)
        val language = "en-US"

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, language)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

        //Ses kaydetmeye başlamak içib
        toggleButton!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                progressBar!!.visibility = View.VISIBLE
                progressBar!!.isIndeterminate = true
                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        REQUEST_RECORD_PERMISSION)

            } else {
                progressBar!!.isIndeterminate = false
                progressBar!!.visibility = View.INVISIBLE
                speech!!.stopListening()
            }
        }


        //BURADAN AŞAĞIYA SÖZLÜK SEÇİMİ
        analyze = findViewById(R.id.analyzebttn)
        val toAnalyze = Intent(this@MainActivity, AnalyzeDisplay::class.java)
        secenekler.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (parent.getItemAtPosition(position) == "New-GSL") {
                    analyze!!.setOnClickListener {
                        toAnalyze.putExtra("DictionarySelect", 1)
                        readEditText(1)
                        getReadyForNewGSL(toAnalyze)

                    }

                } else if (parent.getItemAtPosition(position) == "Academic Vocabulary List") {
                    analyze!!.setOnClickListener {
                        toAnalyze.putExtra("DictionarySelect", 2)
                        readEditText(2)
                        getReadyforAcademicWordListScan(toAnalyze)
                    }

                } else if (parent.getItemAtPosition(position) == "General Service List") {
                    analyze!!.setOnClickListener {
                        toAnalyze.putExtra("DictionarySelect", 3)
                        readEditText(0)
                        getReadyForNGSL(toAnalyze)
                    }

                } else if (parent.getItemAtPosition(position) == "Academic Word List") {
                    analyze!!.setOnClickListener {
                        toAnalyze.putExtra("DictionarySelect", 4)
                        readEditText(3)
                        getReadyForAWL(toAnalyze)
                    }

                } else if (parent.getItemAtPosition(position) == "Academic Collocation List") {

                    analyze!!.setOnClickListener {
                        toAnalyze.putExtra("DictionarySelect", 5)
                        readEditTextStringSet(matchedacademic)
                        getReadyForAcademicCollocation(toAnalyze)
                    }

                } else if (parent.getItemAtPosition(position) == "Discourse Connectors") {
                    analyze!!.setOnClickListener {
                        toAnalyze.putExtra("DictionarySelect", 6)
                        readEditTextStringSet(matcheddiscourse)
                        getReadyForDiscourseConnectors(toAnalyze)
                    }
                } else if (parent.getItemAtPosition(position) == "Phrasal Verbs") {
                    analyze!!.setOnClickListener {
                        toAnalyze.putExtra("DictionarySelect", 7)
                        readEditTextForPhrasalVerbs()
                        getReadyForPhrasalVerbs(toAnalyze)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

    }

    //To Send found AcademicWordList words,unique words and all words and start analyze
    private fun getReadyforAcademicWordListScan(i: Intent) {

        val gsonn = Gson()
        val gsonWords = gsonn.toJson(matchedacademicwordlistwords)
        val offlist = gsonn.toJson(wordsetlist)
        i.putExtra("MatchedList", gsonWords)
        i.putExtra("NumberofWords", numberofwords)
        i.putExtra("Uniquewordsnumber", numberofuniqueswords)
        i.putExtra("OffSet", offlist)
        clearAll()
        startActivity(i)
    }

    //Üsttekinin aynısı
    private fun getReadyForNewGSL(i: Intent) {
        val son = Gson()
        val first500 = son.toJson(matchednewgslwords)
        val offlist = son.toJson(wordsetlist)
        i.putExtra("MatchedList", first500)
        i.putExtra("NumberofWords", numberofwords)
        i.putExtra("Uniquewordsnumber", numberofuniqueswords)
        i.putExtra("OffSet", offlist)
        clearAll()
        startActivity(i)
    }

    private fun getReadyForNGSL(i: Intent) {
        val son = Gson()
        val first1000 = son.toJson(matchedNgsl)
        val offlist = son.toJson(wordsetlist)
        i.putExtra("MatchedList", first1000)
        i.putExtra("NumberofWords", numberofwords)
        i.putExtra("Uniquewordsnumber", numberofuniqueswords)
        i.putExtra("OffSet", offlist)
        clearAll()
        startActivity(i)

    }

    private fun getReadyForAcademicCollocation(i: Intent) {
        val son = Gson()
        val matched = son.toJson(matchedacademic)
        val offlist = son.toJson(uniquewords)
        i.putExtra("MatchedList", matched)
        i.putExtra("NumberofWords", numberofwords)
        i.putExtra("Uniquewordsnumber", numberofuniqueswords)
        i.putExtra("OffSet", offlist)
        clearAll()
        startActivity(i)
    }

    private fun getReadyForDiscourseConnectors(i: Intent) {
        val son = Gson()
        val matched = son.toJson(matcheddiscourse)
        val offlist = son.toJson(uniquewords)
        i.putExtra("MatchedList", matched)
        i.putExtra("NumberofWords", numberofwords)
        i.putExtra("Uniquewordsnumber", numberofuniqueswords)
        i.putExtra("OffSet", offlist)
        clearAll()
        startActivity(i)
    }

    private fun getReadyForAWL(i: Intent) {
        val son = Gson()
        val AWL1 = son.toJson(matchedawlList)
        val offset = son.toJson(wordsetlist)
        i.putExtra("MatchedList", AWL1)
        i.putExtra("OffSet", offset)
        i.putExtra("NumberofWords", numberofwords)
        i.putExtra("Uniquewordsnumber", numberofuniqueswords)

        clearAll()
        startActivity(i)
    }

    private fun getReadyForPhrasalVerbs(i: Intent) {
        val son = Gson()
        val matched = son.toJson(matchedPhrasalVerbs)
        val offlist = son.toJson(offlistwordsforphrasalverbs)
        i.putExtra("MatchedList", matched)
        i.putExtra("NumberofWords", numberofwords)
        i.putExtra("Uniquewordsnumber", numberofuniqueswords)
        i.putExtra("OffSet", offlist)
        clearAll()
        startActivity(i)
    }

    //CSV to BinarySearchTree
    private fun readNewGSLLibrary() {

        var res = arrayOf(R.raw.first500, R.raw.first5001000, R.raw.first10002500)

        for (reses in res) {
            var inputStream = resources.openRawResource(reses)

            var reader = BufferedReader(
                    InputStreamReader(inputStream, Charset.forName("UTF-8"))
            )
            while (true) {
                val line = reader.readLine() ?: break
                val tokens = line.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                //  val word = tokens[0].replace("\\s+".toRegex(), "")
                val word = tokens[0].trimStart().trimEnd()

                val new_word = Word(word)

                newGslList.insert(new_word)

            }
        }
    }

    //CSV to BinarySearchTree FIREBASED
    private fun readAcademicWordsLibrary() {
        val academicWordDirectory = baseReferenceForFiles.child("Academic Word List")

        academicWordDirectory.listAll()
                .addOnSuccessListener { listResult ->
                    listResult.items.forEach { item ->
                        // All the items under listRef.
                        val file = File.createTempFile(item.name, "csv")
                        val fileRef = academicWordDirectory.child(item.name)
                        fileRef.getFile(file).addOnSuccessListener {
                            file.readLines().forEach { s ->
                                val tokens = s.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()//Phrasal verb/boşluk gelirse sıçar

                                val word = tokens[1].trimStart().trimEnd()
                                val root = tokens[0].trimStart().trimEnd()

                                val newWord = Word(word, root)

                                academicwordlist.insert(newWord)
                            }

                        }
                    }
                }
                .addOnFailureListener {
                    // Uh-oh, an error occurred!
                }

    }

    //CSV to BinarySearchTree
    private fun readNGSLWordLibray() {

        val resourcesList = arrayOf(R.raw.first1000ngsl, R.raw.ngssecond1000, R.raw.ngslthird1000, R.raw.ngslsupplamental)

        for (resource in resourcesList) {
            val academicwords = resources.openRawResource(resource)
            val reader = BufferedReader(InputStreamReader(academicwords, Charset.forName("UTF-8")))
            while (true) {
                var line = reader.readLine() ?: break
                val tokens = line.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                var column = 0

                while (column < tokens.size) {
                    val word = tokens[column].trimStart().trimEnd()
                    val root = tokens[0].trimStart().trimEnd()

                    val new_word = Word(word, root)

                    ngslList.insert(new_word)
                    column++
                }
            }
        }
    }

    //FIREBASED
    private fun read_AWL_Library() {
        val awlDirectory = baseReferenceForFiles.child("AWL")
        awlDirectory.listAll()
                .addOnSuccessListener { listResult ->
                    listResult.items.forEach { item ->
                        // All the items under listRef.
                        val file = File.createTempFile(item.name, "csv")
                        val fileRef = awlDirectory.child(item.name)
                        fileRef.getFile(file).addOnSuccessListener {
                            file.readLines().forEach { s ->
                                val tokens = s.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()//Phrasal verb/boşluk gelirse sıçar

                                val word = tokens[1].trimStart().trimEnd()
                                val root = tokens[0].trimStart().trimEnd()

                                val newWord = Word(word, root)

                                awlList.insert(newWord)
                            }

                        }
                    }
                }
                .addOnFailureListener {
                    // Uh-oh, an error occurred!
                }
    }

    private fun read_Phrasal_Verbs() {

        val awlreader: InputStream = resources.openRawResource(R.raw.frequentphrasalverbs)
        val reader: BufferedReader

        reader = BufferedReader(InputStreamReader(awlreader, Charset.forName("UTF-8")))

        try {
            while (true) {
                val line = reader.readLine() ?: break
                val tokens = line.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val root = tokens[0].toLowerCase(Locale.ROOT)
                val word = tokens[1].toLowerCase(Locale.ROOT)

                val new_word = Word(word, root)
                phrasalverbslist.add(new_word)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun readAcademicCollocationList() {
        val awlreader: InputStream = resources.openRawResource(R.raw.academiccollocation)
        val reader: BufferedReader

        reader = BufferedReader(InputStreamReader(awlreader, Charset.forName("UTF-8")))

        try {
            while (true) {
                val line = reader.readLine() ?: break
                aclwordsList.add(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun readDiscourseConnectorList() {
        val awlreader = resources.openRawResource(R.raw.discourseconnnector)
        val reader: BufferedReader

        reader = BufferedReader(InputStreamReader(awlreader, Charset.forName("UTF-8")))

        try {
            while (true) {
                val line = reader.readLine() ?: break
                discoursewordslist.add(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun readEditText(id: Int) {
        val text = returnedText!!.text.toString().toLowerCase().replace("\\s+".toRegex(), " ").replace("[^a-zA-Z\\s]".toRegex(), "").trim { it <= ' ' }
        val words = text.split("[^A-Za-z]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        numberofwords = words.size
        wordset.addAll(Arrays.asList(*words))
        for (w in wordset) {
            wordsetlist.add(Word(w))
        }
        numberofuniqueswords = wordset.size
        for (word in wordset) {
            when (id) {
                0 -> ngslList.find(word, matchedNgsl, wordsetlist)
                1 -> {
                    newGslList.find(word, matchednewgslwords, wordsetlist)
                }
                2 -> academicwordlist.find(word, matchedacademicwordlistwords, wordsetlist)
                3 -> awlList.find(word, matchedawlList, wordsetlist)
            }
        }
    }

    private fun readEditTextForPhrasalVerbs() {
        var text = returnedText!!.text.toString().toLowerCase().replace("\\s+".toRegex(), " ").replace("[^a-zA-Z\\s]".toRegex(), "").trim { it <= ' ' }
        val words = text.split("[^A-Za-z]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        numberofwords = words.size
        wordset.addAll(Arrays.asList(*words))
        numberofuniqueswords = wordset.size
        for (a in phrasalverbslist) {
            if (text.contains(a.word)) {
                matchedPhrasalVerbs.add(a)
            }
            text = text.replace(a.word, "")

        }
        val an = text.split("[^A-Za-z]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (a in an) {
            offlistwordsforphrasalverbs.add(Word(a))
        }

    }

    private fun readEditTextStringSet(matched: MutableSet<String>) {
        var text = returnedText!!.text.toString().toLowerCase().replace("\\s+".toRegex(), " ").replace("[^a-zA-Z\\s]".toRegex(), "").trim { it <= ' ' }
        val words = text.split("[^A-Za-z]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        numberofwords = words.size
        wordset.addAll(Arrays.asList(*words))
        numberofuniqueswords = wordset.size
        for (a in aclwordsList) {
            if (text.contains(a)) {
                matched.add(a)
                text = text.replace(a, "")
            }
        }
        val an = text.split("[^A-Za-z]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        uniquewords.addAll(Arrays.asList(*an))
    }


    //region All about voice recording
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (requestCode == REQUEST_RECORD_PERMISSION) {
            for (i in grantResults.indices) {
                isPermitted = grantResults[i] == PackageManager.PERMISSION_GRANTED

                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    returnedText!!.setText("Permission Denied")
                }
            }
            if (isPermitted) {
                speech!!.startListening(intent)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (speech != null) {
            speech!!.destroy()
            Log.i(LOG_TAG, "destroy")
        }
    }

    override fun onReadyForSpeech(bundle: Bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech")
        Toast.makeText(this, "Ready For Speech", Toast.LENGTH_SHORT).show()
    }

    override fun onBeginningOfSpeech() {

        Log.i(LOG_TAG, "onBeginningOfSpeech")
        progressBar!!.isIndeterminate = false
        progressBar!!.max = 10
        Toast.makeText(this, "Speech Begin", Toast.LENGTH_SHORT).show()
    }

    override fun onRmsChanged(v: Float) {
        Log.i(LOG_TAG, "onRmsChanged: $v")
        progressBar!!.progress = v.toInt()
    }

    override fun onBufferReceived(bytes: ByteArray) {
        Log.i(LOG_TAG, "onBufferReceived: $bytes")
    }

    override fun onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech")
        progressBar!!.isIndeterminate = true
        toggleButton!!.isChecked = false
        Toast.makeText(this, "Speech ended", Toast.LENGTH_SHORT).show()
    }

    override fun onError(i: Int) {
        val errorMessage = getErrorText(i)
        Log.d(LOG_TAG, "FAILED $errorMessage")
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        toggleButton!!.isChecked = false
        Toast.makeText(this, "There is an error : $errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onResults(bundle: Bundle) {
        Log.i(LOG_TAG, "onResults")
        val matches = bundle
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text: String
        text = matches!![0].replace("artı", "+").replace("eksi", "-").replace("noktalı virgül", ";").replace("virgül", ",")
                .replace("çift nokta", ":").replace("nokta", ".").replace("soru işareti", "?")
                .replace("Parantez aç", "(").replace("parantez kapat", ")").replace("ünlem", "!").replace("eğik çizgi", "/")
                .replace("satır başı", "\n   ").replace("Satır başı", "\n   ")
        returnedText!!.append(text)
    }

    override fun onPartialResults(bundle: Bundle) {
        Log.i(LOG_TAG, "onPartialResults")
    }

    override fun onEvent(i: Int, bundle: Bundle) {
        Log.i(LOG_TAG, "onEvent")
    }
    //endregion


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> ViewChanger.changeView(this, 0)
            R.id.TheProject -> ViewChanger.changeView(this, 1)
            R.id.AboutUs -> ViewChanger.changeView(this, 2)
        }
        return true
    }

    companion object {

        private val REQUEST_RECORD_PERMISSION = 100

        fun getErrorText(errorCode: Int): String {
            val message: String
            message = when (errorCode) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                SpeechRecognizer.ERROR_SERVER -> "error from server"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Didn't understand, please try again."
            }
            return message
        }
    }

}