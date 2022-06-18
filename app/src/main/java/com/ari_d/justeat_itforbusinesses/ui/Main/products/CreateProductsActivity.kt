package com.ari_d.justeat_itforbusinesses.ui.Main.products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ari_d.justeat_itforbusinesses.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateProductsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_JustEatItForBusinesses_NoActionBar)
        setContentView(R.layout.activity_create_products)
    }
}