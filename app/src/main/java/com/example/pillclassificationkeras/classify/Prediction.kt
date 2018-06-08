package com.example.pillclassificationkeras.classify

import com.google.gson.annotations.SerializedName

class Prediction(msg:List<String>) {
    @SerializedName("result")
    val msg:List<String> = msg
}