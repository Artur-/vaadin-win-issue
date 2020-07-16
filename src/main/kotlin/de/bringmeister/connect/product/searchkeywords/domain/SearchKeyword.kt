package de.bringmeister.connect.product.searchkeywords.domain

data class SearchKeyword(
    val id: String,
    val skus: Set<String> = setOf()
)
