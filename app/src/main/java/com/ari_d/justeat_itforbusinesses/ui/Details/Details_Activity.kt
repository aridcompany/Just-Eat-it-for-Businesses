package com.ari_d.justeat_itforbusinesses.ui.Details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.ari_d.justeat_itforbusinesses.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Details_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_JustEatItForBusinesses)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_Host_Fragment)
                as NavHostFragment
        navHostFragment.navController
    }
}