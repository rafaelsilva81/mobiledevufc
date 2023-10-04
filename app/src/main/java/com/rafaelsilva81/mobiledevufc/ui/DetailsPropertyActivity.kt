package com.rafaelsilva81.mobiledevufc.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.rafaelsilva81.mobiledevufc.R
import com.rafaelsilva81.mobiledevufc.controller.EstateController
import com.rafaelsilva81.mobiledevufc.databinding.ActivityDetailsPropertyBinding
import com.rafaelsilva81.mobiledevufc.helpers.DialogHelper
import kotlinx.coroutines.launch


class DetailsPropertyActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityDetailsPropertyBinding

    // Toolbar
    private lateinit var toolbar: Toolbar

    // Campos
    private var addressValueTextView: TextView? = null
    private var priceValueTextView: TextView? = null
    private var availableValueTextView: TextView? = null
    private var rentValueTextView: TextView? = null
    private var areaValueTextView: TextView? = null
    private var descriptionValueTextView: TextView? = null
    private var imageValueView: ImageView? = null

    // Id recebido da intent
    private lateinit var id: String

    // Botoes
    private var editButton: Button? = null
    private var deleteButton: Button? = null

    override fun onRestart() {
        super.onRestart()
        fetchData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Inicializa os campos
        addressValueTextView = binding.detailAddressValue
        priceValueTextView = binding.detailPriceValue
        availableValueTextView = binding.detailAvailableValue
        rentValueTextView = binding.detailRentValue
        areaValueTextView = binding.detailAreaValue
        descriptionValueTextView = binding.detailDescriptionValue
        imageValueView = binding.detailImageView

        // Inicializa os botoes
        editButton = binding.detailEditButton
        deleteButton = binding.detailRemoveButton

        // Recebe o id da intent
        val res = intent.getStringExtra("id")

        // Fazer o textview de descricao ser scrollable
        descriptionValueTextView?.movementMethod = android.text.method.ScrollingMovementMethod()

        // TODO: Remover isso aqui e trazer pelo intent mesmo
        //id = "byeeBXuHKzLocB5q3BJQ"
        if (res != null) {
            id = res
        } else {
            finish()
        }

        fetchData();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.side_navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun fetchData() {
        lifecycleScope.launch {
            val estate = EstateController.get(id)
            if (estate?.image != null && estate.image != "") {
                val storageRef = FirebaseStorage.getInstance().reference

                storageRef.child(estate?.image!!).downloadUrl.addOnSuccessListener {
                    Glide.with(applicationContext)
                        .load(it)
                        .placeholder(R.drawable.house_placeholder)
                        .centerCrop()
                        .into(imageValueView!!)
                }.addOnFailureListener {
                    println("Erro ao baixar imagem")
                }
            }

            // setar o resto dos valores
            addressValueTextView?.text = estate?.address
            priceValueTextView?.text = "Preço: ${estate?.getPriceString()}"
            availableValueTextView?.text =
                if (estate?.available == true) "Imóvel Disponível" else "Imóvel Indisponível"
            rentValueTextView?.text = if (estate?.rent == true) "Para Alugar" else "Para Venda"
            areaValueTextView?.text = "Área: ${estate?.getAreaString()}"
            descriptionValueTextView?.text = estate?.description

            editButton?.setOnClickListener {
                println("Clicou no botao de editar")
                val intent =
                    Intent(applicationContext, EditPropertyActivity::class.java)
                intent.putExtra("id", id)

                startActivity(intent)
            }

            deleteButton?.setOnClickListener {
                println("Clicou no botao de remover")
                deleteEstate()
            }

        }
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
                startActivity(Intent(applicationContext, ListPropertiesActivity::class.java))
                //finish()
            }
        }
        return true
    }

    private fun deleteEstate() {
        val askForRemove = DialogHelper.getDialogBuilder(
            this,
            "Remover Imóvel",
            "Tem certeza que deseja remover este imóvel? Essa ação não pode ser desfeita."
        )

        askForRemove.apply {
            setPositiveButton("Sim") { dialog, _ ->
                dialog.dismiss()
                EstateController.delete(id, onSuccess = {
                    finish()
                }, onError = {
                    DialogHelper.showAlert(
                        this@DetailsPropertyActivity,
                        "Erro",
                        "Erro ao remover imóvel"
                    )
                })
            }
            setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
        }

        askForRemove.show()
    }
}