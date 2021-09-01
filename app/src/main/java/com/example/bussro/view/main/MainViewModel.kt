package com.example.bussro.view.main

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.bussro.view.BusListActivity
import com.example.bussro.view.FindStationActivity
import com.example.bussro.view.settings.SettingsActivity

/**
 * [MainViewModel]
 * MainActivity 의 ViewModel
 *
 * @author 윤주연(otu165)
 */

class MainViewModel : ViewModel() {

    /* onClick */
    fun onClick(v: View, case: Int) {
        var intent: Intent? = null
        var toast: Toast? = null

        when (case) {
            0 -> {
                intent = Intent(v.context, SettingsActivity::class.java)
            }
            1 -> {
                intent = Intent(v.context, FindStationActivity::class.java)
            }
            2 -> {
                intent = Intent(v.context, BusListActivity::class.java)
            }
            3 -> {
                toast = Toast.makeText(v.context, "[히스토리]\n아직 구현되지 않은 기능입니다.", Toast.LENGTH_SHORT)
            }
        }

        intent?.apply {
            v.context.startActivity(this)
        }

        toast?.show()
    }
}