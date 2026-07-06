package com.arq.currencyconverter.feature.converter.data.response

import com.google.gson.annotations.SerializedName

data class TickerResponse(
    @SerializedName("ask")
    val ask: String,
    @SerializedName("bid")
    val bid: String,
    @SerializedName("book")
    val book: String,
    @SerializedName("date")
    val date: String
)
