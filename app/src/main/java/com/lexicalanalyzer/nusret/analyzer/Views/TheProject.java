package com.lexicalanalyzer.nusret.analyzer.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.lexicalanalyzer.nusret.analyzer.R;
import com.lexicalanalyzer.nusret.analyzer.Utils.ViewChanger;

public class TheProject extends AppCompatActivity {

    private WebView theproject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_project);
        theproject = findViewById(R.id.theprojectpage);
        theproject.getSettings().setJavaScriptEnabled(true);
        theproject.loadUrl("http://www.lexicalanalyzer.com/the-project");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.home):
                ViewChanger.changeView(this, 0);
                break;
            case (R.id.TheProject):
                ViewChanger.changeView(this, 1);
                break;
            case (R.id.AboutUs):
                ViewChanger.changeView(this, 2);
                break;

        }


        return true;
    }
}
