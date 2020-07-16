package de.bringmeister.connect.product.searchkeywords.application

import de.bringmeister.connect.product.searchkeywords.domain.SearchKeyword
import de.bringmeister.connect.product.searchkeywords.domain.SearchKeywordRepository
import de.bringmeister.connect.product.searchkeywords.domain.SearchKeywordsUpdatedEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SearchKeywordService(
    private val keywordRepository: SearchKeywordRepository,
    private val publisher: SearchKeywordsUpdatedEventPublisher,
    private val keywordUpdatedEventRepo: SearchKeywordsUpdatedEventRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun createSearchKeyword(keyword: SearchKeyword) {
        if (keywordRepository.exists(keyword.id))
            throw SearchKeywordAlreadyExistsException("Search Keyword already exists. [id=$keyword.id]")

        keywordRepository.save(keyword)
        publishedAddedKeywordToSku(keyword.id, keyword.skus)
    }

    fun setOrRemoveSkusOfSearchKeyword(keyword: String, skus: Set<String>): SearchKeyword {
        if (keywordRepository.exists(keyword)) {
            val loadedSkus = keywordRepository.find(keyword)!!.skus
            val updatedSkus = when (loadedSkus.containsAll(skus)) {
                false -> {
                    publishedAddedKeywordToSku(keyword, skus)
                    loadedSkus + skus
                }
                true -> {
                    publishedRemovedKeywordFromSku(keyword, skus)
                    loadedSkus - skus
                }
            }
            val updatedKeyword = SearchKeyword(keyword, updatedSkus)
            keywordRepository.save(updatedKeyword)
            return updatedKeyword
        } else {
            throw SearchKeywordDoNotExistsException("Search Keyword does not exists. [keyword=$keyword]")
        }
    }

    private fun isKeywordNewAtSku(keyword: String, keywordList: Set<String>?): Boolean {
        return keywordList?.contains(keyword) != true
    }

    private fun publishedAddedKeywordToSku(keyword: String, skus: Set<String>) {
        skus.forEach { sku ->
            val loadedEvent = keywordUpdatedEventRepo.get(sku)
            val loadedKeywordList = loadedEvent?.searchKeywords
            if (isKeywordNewAtSku(keyword, loadedKeywordList)) {
                val keywordList = when (loadedKeywordList) {
                    null -> setOf(keyword)
                    else -> loadedKeywordList + keyword
                }
                publisher.generatedUpdatedEvent(sku, keywordList)
            }
        }
    }

    private fun publishedRemovedKeywordFromSku(keyword: String, skus: Set<String>) {
        skus.forEach { sku ->
            val loadedEvent = keywordUpdatedEventRepo.get(sku)
            val loadedKeywordList = loadedEvent!!.searchKeywords
            val keywordList = loadedKeywordList - keyword
            publisher.generatedUpdatedEvent(sku, keywordList)
        }
    }

    fun findKeywordsBySku(sku: String): Set<String> {
        when (val loadedEvent = keywordUpdatedEventRepo.get(sku)) {
            null -> throw SkuNotFoundException("SKU was not found. [sku=$sku]")
            else -> return loadedEvent.searchKeywords
        }
    }

    fun findSkusByKeyword(keyword: String): Set<String> {
        when (val loadedKeyword = keywordRepository.find(keyword)) {
            null -> throw KeywordNotFoundException("Keyword was not found. [keyword=$keyword")
            else -> return loadedKeyword.skus
        }
    }

    fun findAllKeywords(): List<String> {
        return keywordRepository.findAll()
    }
}

class SearchKeywordDoNotExistsException(keyword: String) :
    RuntimeException("Search Keyword does not exist. [keyword=$keyword]")

class SearchKeywordAlreadyExistsException(keyword: String) :
    RuntimeException("Search Keyword already exists. [keyword=$keyword]")

class SkuNotFoundException(sku: String) : RuntimeException("SKU was not found. [sku=$sku]")

class KeywordNotFoundException(keyword: String) : RuntimeException("Keyword was not found. [keyword=$keyword")
