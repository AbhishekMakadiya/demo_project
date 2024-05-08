package com.demo.model

data class NewsDataFromJson(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)