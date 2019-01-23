package com.lexicalanalyzer.nusret.analyzer.Views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import com.lexicalanalyzer.nusret.analyzer.R
import com.lexicalanalyzer.nusret.analyzer.Utils.ViewChanger

class AboutUs : AppCompatActivity() {

    internal lateinit var aboutus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        aboutus = findViewById(R.id.textView3)
        aboutus.text = Html.fromHtml("<p style = 'color : black;'><strong>Project Leader</strong></p>\n" +
                "\n" +
                "<p style = 'color : black;'><em><strong>Seyed Ali Rezvani Kalajahi, PhD</strong></em></p>\n" +
                "\n" +
                "<p>Post-doctoral Research Fellow, Department of ELT, Faculty of Education, Maltepe University, Istanbul, Turkey</span></p>\n" +
                "\n" +
                "<p> </p>\n" +
                "\n" +
                "<p style = 'color : black;'><strong>Mentor & Co-researcher</strong></p>\n" +
                "\n" +
                "<p style = 'color : black;'><em><strong>Hakan Dilman, PhD</strong></em></p>\n" +
                "\n" +
                "<p>Department of ELT, Faculty of Education, Maltepe University, Istanbul, Turkey</p>\n" +
                "\n" +
                "<p> </p>\n" +
                "\n" +
                "<p><strong>Associate Researchers</strong></p>\n" +
                "\n" +
                "<p><em><strong>Yasemin Aksoyalp, MA</em></strong></p>\n" +
                "\n" +
                "<p>School of Foreign Languages, Turkish German Univesity, Beykoz, Istanbul, Turkey</p>\n" +
                "\n" +
                "<p><em><strong>Özlem Pervan, MA</strong><em></p>\n" +
                "\n" +
                "<p>Department of ELT, Faculty of Education, Maltepe University, Istanbul, Turkey</p>\n" +
                "\n")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> ViewChanger.changeView(this, 0)
            R.id.TheProject -> ViewChanger.changeView(this, 1)
            R.id.AboutUs -> ViewChanger.changeView(this, 2)
            R.id.ContactUs -> ViewChanger.changeView(this, 3)
        }
        return true
    }
}
