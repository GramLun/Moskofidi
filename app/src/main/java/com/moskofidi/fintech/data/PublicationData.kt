package com.moskofidi.fintech.data

data class PublicationData(
    val description: String,
    val gifURL: String,
)

data class ResultList(var result: List<PublicationData>)
