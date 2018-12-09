package com.anwesh.uiprojects.linkeddumblerotstepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.dumblerotstepview.DumbleRotStepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DumbleRotStepView.create(this)
    }
}
