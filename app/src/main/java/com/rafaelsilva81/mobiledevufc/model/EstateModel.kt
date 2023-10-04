package com.rafaelsilva81.mobiledevufc.model

import java.util.UUID

class EstateModel {
    private var id: String? = null
    var address: String? = null
    var area: Double? = null
    var price: Double? = null
    var available: Boolean? = null
    var rent: Boolean? = null
    var description: String? = null
    var image: String? = null

    constructor() {}

    constructor(
        address: String?,
        description: String?,
        image: String?,
        area: Double?,
        price: Double?,
        rent: Boolean?,
        available: Boolean?
    ) {
        this.id = UUID.randomUUID().toString()
        this.address = address
        this.description = description
        this.image = image
        this.area = area
        this.price = price
        this.rent = rent
        this.available = available
    }

    fun getAreaString(): String {
        return if (this.area == null) {
            "0 m²"
        } else {
            // 2 casas decimais
            val area = String.format("%.2f", this.price)
            return "$area m²"
        }
    }

    fun getPriceString(): String {
        return if (this.price == null) {
            "R$ 00,00"
        } else {
            println("Price: $price")
            // 2 casas decimais
            val price = String.format("%.2f", this.price)
            return "R$ $price"
        }
    }

    fun getId(): String? {
        return this.id
    }

    fun setId(id: String) {
        this.id = id
    }

    override fun toString(): String {
        return "EstateModel(id=$id, address=$address, area=$area, price=$price, available=$available, rent=$rent, description=$description, image=$image)"
    }
}