package com.rafaelsilva81.mobiledevufc.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.rafaelsilva81.mobiledevufc.R
import com.rafaelsilva81.mobiledevufc.controller.EstateController
import com.rafaelsilva81.mobiledevufc.databinding.ActivityAddEstateBinding
import com.rafaelsilva81.mobiledevufc.helpers.DialogHelper
import com.rafaelsilva81.mobiledevufc.model.EstateModel
import com.rafaelsilva81.mobiledevufc.providers.Storage


class AddEstateActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityAddEstateBinding

    // Toolbar
    private lateinit var toolbar: Toolbar

    // Form fields
    private lateinit var addressInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var priceInput: TextInputEditText
    private lateinit var areaInput: TextInputEditText
    private lateinit var availableCheckBox: CheckBox
    private lateinit var rentCheckBox: CheckBox

    // Buttons
    private lateinit var imageButton: MaterialButton
    private lateinit var saveButton: Button
    private lateinit var clearInputsButton: Button

    // Image data
    private var imageBitmap: Bitmap? = null

    // Loading
    private var loadingDialog: AlertDialog? = null

    // Map result
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                
                val data: Intent? = result.data
                // get endereço
                val address = data?.getStringExtra("address")
                // set endereço no campo de endereço
                addressInput.setText(address)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageBitmap = data?.extras?.get("data") as Bitmap?
                if (imageBitmap != null) {
                    println("Image bitmap: $imageBitmap")
                    imageButton.text = "Imagem selecionada"
                    imageButton.setBackgroundColor(resources.getColor(R.color.primary, null))
                    imageButton.setTextColor(resources.getColor(R.color.white, null))
                    imageButton.setIconTintResource(R.color.white)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEstateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        attributeVariables()

        clearInputsButton.setOnClickListener {
            clearInputs()
        }

        imageButton.setOnClickListener {
            openCamera()
        }

        saveButton.setOnClickListener {
            addEstate()
        }

        addressInput.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun attributeVariables() {
        addressInput = binding.estateAddressInput
        descriptionInput = binding.estateDescriptionInput
        priceInput = binding.estatePriceInput
        areaInput = binding.estateAreaInput
        availableCheckBox = binding.estateAvailableCheckbox
        rentCheckBox = binding.estateRentalCheckbox
        imageButton = binding.estateImageButton as MaterialButton
        saveButton = binding.estateConfirmButton
        clearInputsButton = binding.estateClearButton
    }

    private fun clearInputs() {
        addressInput.text?.clear()
        descriptionInput.text?.clear()
        priceInput.text?.clear()
        areaInput.text?.clear()
        availableCheckBox.isChecked = true
        rentCheckBox.isChecked = false

        imageButton.text = "Selecionar imagem"
        imageButton.setBackgroundColor(resources.getColor(R.color.transparent, null))
        imageButton.setTextColor(resources.getColor(R.color.primary, null))
        imageButton.setIconTintResource(R.color.primary)
    }

    private fun canSubmit(): Boolean {
        return addressInput.text?.isNotEmpty() == true &&
                descriptionInput.text?.isNotEmpty() == true &&
                priceInput.text?.isNotEmpty() == true &&
                areaInput.text?.isNotEmpty() == true

    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val chooserIntent = Intent.createChooser(takePictureIntent, "Select Picture")
            takePictureLauncher.launch(chooserIntent)
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
                return true
                //finish()
            }

            R.id.navigation_list_properties -> {
                startActivity(Intent(applicationContext, ListPropertiesActivity::class.java))
                //finish()
            }
        }
        return true
    }


    private fun addEstate() {
        if (!canSubmit()) {
            DialogHelper
                .showAlert(
                    this,
                    "Erro ao salvar imóvel",
                    "Você precisa preencher todos os campos antes de continuar"
                )
        } else {
            loadingDialog = DialogHelper.getDialogBuilder(
                this,
                "Aguarde",
                "Salvando imóvel..."
            ).setCancelable(false).create()
            loadingDialog?.show()

            var ref = Storage.getReference().child(Storage.getRandomImageName())
            var uploadTask = ref.putBytes(Storage.getImageData(imageBitmap!!))
            uploadTask.addOnFailureListener {
                DialogHelper.showAlert(
                    this,
                    "Erro",
                    "Erro ao enviar a imagem"
                )
            }.addOnSuccessListener { taskSnapshot ->
                var newEstate = EstateModel(
                    addressInput.text.toString(),
                    descriptionInput.text.toString(),
                    taskSnapshot.metadata?.path,
                    areaInput.text.toString().toDouble(),
                    priceInput.text.toString().toDouble(),
                    rentCheckBox.isChecked,
                    availableCheckBox.isChecked
                )

                EstateController.add(newEstate, onSuccess = {
                    DialogHelper.showAlert(
                        this,
                        "Sucesso",
                        "Imóvel cadastrado com sucesso"
                    )
                }, onError = {
                    DialogHelper.showAlert(
                        this,
                        "Erro",
                        "Erro ao cadastrar imóvel"
                    )
                }, onComplete = {
                    clearInputs()
                    loadingDialog?.dismiss()
                })
            }

        }
    }
}