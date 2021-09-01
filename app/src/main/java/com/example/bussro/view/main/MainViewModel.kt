package com.example.bussro.view.main

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.bussro.view.BusListActivity
import com.example.bussro.view.FindStationActivity

class MainViewModel : ViewModel() {

    /* onClick */
    fun onClick(v: View, case: Int) {
        if (case == 1) {
            val intent = Intent(v.context, FindStationActivity::class.java)
            v.context.startActivity(intent)
        }
        else if (case == 2) {
            val intent = Intent(v.context, BusListActivity::class.java)
            v.context.startActivity(intent)
        }
        else if (case == 3) {
            val toast = Toast.makeText(v.context, "아직 구현되지 않은 기능입니다.", Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}