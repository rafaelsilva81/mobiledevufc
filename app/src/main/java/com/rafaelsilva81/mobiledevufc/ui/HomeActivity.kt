package com.rafaelsilva81.mobiledevufc.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.rafaelsilva81.mobiledevufc.R
import com.rafaelsilva81.mobiledevufc.controller.DashboadController
import com.rafaelsilva81.mobiledevufc.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch


class HomeActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityHomeBinding

    // Toolbar
    private lateinit var toolbar: Toolbar

    // Valores
    private lateinit var totalEstates: TextView
    private lateinit var totalEstatesForRent: TextView
    private lateinit var totalEstatesForSale: TextView
    private lateinit var totalAvailableEstates: TextView
    private lateinit var totalUnavailableEstates: TextView

    // Botoes
    private lateinit var btnAddEstate: TextView
    private lateinit var btnListEstates: TextView

    override fun onRestart() {
        super.onRestart()
        fetchData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // seta os valores
        totalEstates = binding.totalEstateValue
        totalEstatesForRent = binding.rentEstateValue
        totalEstatesForSale = binding.sellEstateValue
        totalAvailableEstates = binding.availableEstateValue
        totalUnavailableEstates = binding.unavailableEstateValue

        // seta os botoes
        btnAddEstate = binding.registerEstateButton
        btnListEstates = binding.seeEstatesButton

        // onclick dos botoes
        btnAddEstate.setOnClickListener {
            startActivity(Intent(applicationContext, AddEstateActivity::class.java))
        }

        btnListEstates.setOnClickListener {
            startActivity(Intent(applicationContext, ListPropertiesActivity::class.java))
        }

        fetchData()
    }

    private fun fetchData() {
        lifecycleScope.launch {
            val totalEstatesValue = DashboadController.countTotalEstates()
            val totalEstatesForRentValue = DashboadController.countTotalEstatesForRent()
            val totalEstatesForSaleValue = DashboadController.countTotalEstatesForSale()
            val totalAvailableEstatesValue = DashboadController.countTotalAvailableEstates()
            val totalUnavailableEstatesValue = DashboadController.countTotalUnavailableEstates()

            totalEstates.text = totalEstatesValue.toString()
            totalEstatesForRent.text = totalEstatesForRentValue.toString()
            totalEstatesForSale.text = totalEstatesForSaleValue.toString()
            totalAvailableEstates.text = totalAvailableEstatesValue.toString()
            totalUnavailableEstates.text = totalUnavailableEstatesValue.toString()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.side_navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                return true
                //finish()
            }

            R.id.navigation_add_property -> {
                startActivity(Intent(applicationContext, AddEstateActivity::class.java))
                //finish()
            }

            R.id.navigation_list_properties -> {
                startActivity(Intent(applicationContext, ListPropertiesActivity::class.java))
                //finish()
            }
        }
        return true
    }

}