package com.lexicalanalyzer.nusret.analyzer.Utils

import android.app.Activity
import android.content.Intent

import com.lexicalanalyzer.nusret.analyzer.Views.AboutUs
import com.lexicalanalyzer.nusret.analyzer.Views.ContactUs
import com.lexicalanalyzer.nusret.analyzer.Views.MainActivity
import com.lexicalanalyzer.nusret.analyzer.Views.TheProject

object ViewChanger {
    private val classes = arrayOf<Class<*>>(MainActivity::class.java, TheProject::class.java, AboutUs::class.java, ContactUs::class.java)

    fun changeView(from: Activity, to: Int) {
        val i = Intent(from, classes[to])
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        from.startActivity(i)
        from.finish()
    }


}
