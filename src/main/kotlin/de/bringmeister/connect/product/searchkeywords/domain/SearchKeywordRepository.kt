package de.bringmeister.connect.product.searchkeywords.domain

interface SearchKeywordRepository {
    fun find(id: String): SearchKeyword?
    fun save(keyword: SearchKeyword)
    fun exists(id: String): Boolean
    fun delete(id: String)
    fun findAll(): List<String>
}
