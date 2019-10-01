package com.lexicalanalyzer.nusret.analyzer.Views

import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lexicalanalyzer.nusret.analyzer.R
import com.lexicalanalyzer.nusret.analyzer.Utils.ViewChanger

class ContactUs : AppCompatActivity() {
    private var view: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        view = findViewById(R.id.textView4)
        view!!.text = Html.fromHtml("<strong>Location : </strong> <br/><br/>Department of Foreign Language, Faculty of Education, Maltepe University, <br/><br/> Istanbul-Turkey <br/><br/> <strong>Mail Us : </strong>  <br/><br/> info@lexicalanalyzer.com  <br/><br/>\n" + "        <strong>Call Us : </strong> <br/><br/>+90 216 626 1050 (EXT.2230)")
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
