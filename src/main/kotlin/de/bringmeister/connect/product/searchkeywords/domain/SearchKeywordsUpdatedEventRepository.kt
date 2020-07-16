package de.bringmeister.connect.product.searchkeywords.domain

interface SearchKeywordsUpdatedEventRepository {
    fun save(event: SearchKeywordsUpdatedEvent)
    fun get(sku: String): SearchKeywordsUpdatedEvent?
    fun delete(sku: String)
}
