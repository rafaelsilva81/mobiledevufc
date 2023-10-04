package com.rafaelsilva81.mobiledevufc.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.rafaelsilva81.mobiledevufc.R
import com.rafaelsilva81.mobiledevufc.databinding.ListAdapterBinding
import com.rafaelsilva81.mobiledevufc.model.EstateModel

class ListAdapter(
    private val applicationContext: Context,
    private val data: MutableList<EstateModel>,
    val houseSelected: (EstateModel) -> Unit
) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    // Método chamado quando uma nova célula é criada
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ListAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = data.size

    // Para cada celula adicionada
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]

        // Carrega todos os dados
        if (item.image != null && item.image != "") {
            val storageRef = FirebaseStorage.getInstance().reference
            storageRef.child(item.image!!).downloadUrl.addOnSuccessListener {
                Glide.with(applicationContext)
                    .load(it)
                    .placeholder(R.drawable.house_placeholder)
                    .centerCrop()
                    .into(holder.binding.image)
            }.addOnFailureListener {
                println("Erro ao baixar imagem")
            }
        }

        if (item.available == true) {
            holder.binding.detailAvailableValue.text = "Disponível"
        } else {
            holder.binding.detailAvailableValue.text = "Indisponível"
        }

        if (item.rent == true) {
            holder.binding.detailRentValue.text = "Aluguel"
        } else {
            holder.binding.detailRentValue.text = "Compra"
        }

        holder.binding.detailAddressValue.text = item.address

        holder.binding.detailPriceValue.text = item.getPriceString()

        holder.binding.btnDetails.setOnClickListener {
            houseSelected(item) // Chama a função de retorno quando um item é selecionado
        }
    }

    // Classe interna para manter a referência à vista de layout da célula
    class MyViewHolder(val binding: ListAdapterBinding) : RecyclerView.ViewHolder(binding.root)
}
