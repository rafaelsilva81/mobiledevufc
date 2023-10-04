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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.rafaelsilva81.mobiledevufc.R
import com.rafaelsilva81.mobiledevufc.controller.EstateController
import com.rafaelsilva81.mobiledevufc.databinding.ActivityEditPropertyBinding
import com.rafaelsilva81.mobiledevufc.helpers.DialogHelper
import com.rafaelsilva81.mobiledevufc.model.EstateModel
import com.rafaelsilva81.mobiledevufc.providers.Storage
import kotlinx.coroutines.launch


class EditPropertyActivity : AppCompatActivity() {
    // Id recebido da intent
    private lateinit var id: String

    // imagePath db
    private lateinit var pathImage: String

    // ViewBinding
    private lateinit var binding: ActivityEditPropertyBinding

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
    private var imageChanged: Boolean = false

    // Loading
    private var loadingDialog: AlertDialog? = null

    // Editable
    data class Editable(val content: String)

    // Map result
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
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
                    imageButton.text = "Imagem selecionada"
                    imageButton.setBackgroundColor(resources.getColor(R.color.primary, null))
                    imageButton.setTextColor(resources.getColor(R.color.white, null))
                    imageButton.setIconTintResource(R.color.white)
                    imageChanged = true
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPropertyBinding.inflate(layoutInflater)
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
            if (imageChanged && imageBitmap != null) {
                var ref = Storage.getReference().child(Storage.getRandomImageName())
                var uploadTask = ref.putBytes(Storage.getImageData(imageBitmap!!))
                uploadTask.addOnFailureListener {
                    DialogHelper.showAlert(
                        this,
                        "Erro",
                        "Erro ao enviar a imagem"
                    )
                }.addOnSuccessListener { taskSnapshot ->
                    pathImage = taskSnapshot.metadata?.path ?: ""
                    editEstate()
                }.addOnFailureListener {
                    DialogHelper.showAlert(
                        this,
                        "Erro",
                        "Erro ao enviar a nova imagem"
                    )
                }
            }else {
                editEstate()
            }
        }

        addressInput.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            resultLauncher.launch(intent)
        }

        // Recebe o id da intent
        val res = intent.getStringExtra("id")

        if (res != null) {
            id = res
        } else {
            finish()
        }

        fetchData();

    }

    private fun attributeVariables() {
        addressInput = binding.estateAddressInputEdit
        descriptionInput = binding.estateDescriptionInputEdit
        priceInput = binding.estatePriceInputEdit
        areaInput = binding.estateAreaInputEdit
        availableCheckBox = binding.estateAvailableCheckboxEdit
        rentCheckBox = binding.estateRentalCheckboxEdit
        imageButton = binding.estateImageButtonEdit as MaterialButton
        saveButton = binding.estateEditButtonEdit
        clearInputsButton = binding.estateClearButtonEdit
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

    fun convertToEditable(inputString: String?): android.text.Editable? {
        // Handle the case where the inputString is null
        return inputString?.let { android.text.Editable.Factory.getInstance().newEditable(it) }
    }

    private fun fetchData() {
        lifecycleScope.launch {
            val estate = EstateController.get(id)

            // setar o resto dos valores
            addressInput.text = convertToEditable(estate?.address)
            descriptionInput.text = convertToEditable(estate?.description)
            priceInput.setText(estate?.price?.toString())
            areaInput.setText(estate?.area?.toString())
            pathImage = estate?.image ?: ""
            availableCheckBox.isChecked = estate?.available == true
            rentCheckBox.isChecked = estate?.rent == true

        }
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


    private fun editEstate() {

        if (!canSubmit()) {
            DialogHelper
                .showAlert(
                    this,
                    "Erro ao editar imóvel",
                    "Você precisa preencher todos os campos antes de continuar"
                )
        } else {
            loadingDialog = DialogHelper.getDialogBuilder(
                this,
                "Aguarde",
                "Salvando edição do imóvel..."
            ).setCancelable(false).create()
            loadingDialog?.show()

            var newEstate = EstateModel(
                addressInput.text.toString(),
                descriptionInput.text.toString(),
                pathImage,
                areaInput.text.toString().toDouble(),
                priceInput.text.toString().toDouble(),
                rentCheckBox.isChecked,
                availableCheckBox.isChecked
            )

            println("newEstate: $newEstate")

            EstateController.edit(id, newEstate, onSuccess = {
                DialogHelper.showAlert(
                    this,
                    "Sucesso",
                    "Imóvel editado com sucesso"
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
                finish()
            })


        }
    }
}