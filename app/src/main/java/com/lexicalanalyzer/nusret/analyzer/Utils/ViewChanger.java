package com.lexicalanalyzer.nusret.analyzer.Utils;

import android.app.Activity;
import android.content.Intent;

import com.lexicalanalyzer.nusret.analyzer.Views.AboutUs;
import com.lexicalanalyzer.nusret.analyzer.Views.MainActivity;
import com.lexicalanalyzer.nusret.analyzer.Views.TheProject;

public class ViewChanger {
    private static Class[] classes = {MainActivity.class, TheProject.class, AboutUs.class};

    public static void changeView(Activity from, int to) {
        Intent i = new Intent(from, classes[to]);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        from.startActivity(i);
        from.finish();
    }


}
