package com.rafaelsilva81.mobiledevufc.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.rafaelsilva81.mobiledevufc.R
import com.rafaelsilva81.mobiledevufc.adapter.ListAdapter
import com.rafaelsilva81.mobiledevufc.controller.BusinessType
import com.rafaelsilva81.mobiledevufc.controller.EstateController
import com.rafaelsilva81.mobiledevufc.databinding.ActivityListPropertiesBinding
import com.rafaelsilva81.mobiledevufc.model.EstateModel
import kotlinx.coroutines.launch


enum class SortOrder {
    ASCENDING,
    DESCENDING
}

class ListPropertiesActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityListPropertiesBinding

    // houses data
    private val dataHouses = mutableListOf<EstateModel>()

    // estates list conditions
    private var showUnavailable = false
    private var priceDirection: Query.Direction? = null
    private var businessType: BusinessType? = null

    // Toolbar
    private lateinit var toolbar: Toolbar

    // Filter inputs
    private lateinit var showUnavailableCheckBox: CheckBox
    private lateinit var orderBySpinner: Spinner
    private lateinit var businessTypeSpinner: Spinner

    override fun onRestart() {
        super.onRestart()
        fetchData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListPropertiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Definir items
        showUnavailableCheckBox = binding.estateUnavailableCheckbox
        orderBySpinner = binding.estateOrderSpinner
        businessTypeSpinner = binding.estateTypeSpinner

        orderBySpinner.isSelected = false
        businessTypeSpinner.isSelected = false

        // Setar valores quando o spinner for alterado
        orderBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (parent != null && view != null) {
                    //scrollSt = false
                    onSelectOrderBy(parent, view, pos, id)
                    fetchData()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // nada
            }
        }

        // Setar valores quando o spinner for alterado
        businessTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                if (parent != null && view != null) {
                    //scrollSt = false
                    onBusinessTypeSelected(parent, view, pos, id)
                    fetchData()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // nada
            }
        }


        // On click do checkbox atualizar o valor da variável de pesquisa
        showUnavailableCheckBox.setOnClickListener {
            showUnavailable = showUnavailableCheckBox.isChecked
            fetchData()
        }

        binding.rdListProperties.layoutManager = LinearLayoutManager(this)
        binding.rdListProperties.setHasFixedSize(true)

        fetchData()
    }

    fun onSelectOrderBy(parent: AdapterView<*>, view: View, pos: Number, id: Number) {
        // legenda
        // pos 0 --> null
        // pos 1 --> preço crescente
        // pos 2 --> preço decrescente

        when (pos) {
            0 -> priceDirection = null
            1 -> priceDirection = Query.Direction.ASCENDING
            2 -> priceDirection = Query.Direction.DESCENDING
        }
    }

    fun onBusinessTypeSelected(parent: AdapterView<*>, view: View, pos: Number, id: Number) {
        // legenda
        // pos 0 --> null
        // pos 1 --> aluguel
        // pos 2 --> venda

        when (pos) {
            0 -> businessType = null
            1 -> businessType = BusinessType.RENT
            2 -> businessType = BusinessType.SALE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.side_navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                startActivity(Intent(applicationContext, HomeActivity::class.java))
                //finish()
            }

            R.id.navigation_add_property -> {
                startActivity(Intent(applicationContext, AddEstateActivity::class.java))
                //finish()
            }

            R.id.navigation_list_properties -> {
                return true
                //finish()
            }
        }
        return true
    }

    private fun loading(load: Boolean) {
        println("Chamando loading com $load")
        if (load) {
            binding.progressBar.visibility = View.VISIBLE
            binding.textInfo.visibility = View.VISIBLE

        } else {
            binding.progressBar.visibility = View.GONE
            binding.textInfo.visibility = View.GONE

        }
    }


    private fun fetchData() {
        println("Houses: ")
        lifecycleScope.launch {
            EstateController.list(
                businessType = businessType,
                showUnavailable = showUnavailable,
                orderPriceDirection = priceDirection,
                onSuccess = { houses ->
                    dataHouses.clear() // limpar o vetor

                    loading(true)
                    for (house in houses) {
                        println("house :" + house)
                        dataHouses.add(house)
                    }

                    loading(false)
                    binding.rdListProperties.adapter =
                        ListAdapter(applicationContext, dataHouses) { item ->
                            // função houseSelected() do adapter

                            val id: String = item.getId() ?: ""
                            val intent =
                                Intent(applicationContext, DetailsPropertyActivity::class.java)
                            intent.putExtra("id", id)

                            startActivity(intent)
                        }
                })
        }
    }


}