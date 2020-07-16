package de.bringmeister.connect.product.searchkeywords.infrastructure.dynamodb

import de.bringmeister.connect.product.searchkeywords.domain.SearchKeywordsUpdatedEvent
import de.bringmeister.connect.product.searchkeywords.domain.SearchKeywordsUpdatedEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SearchKeywordsUpdatedEventDynamoDbAdapter : SearchKeywordsUpdatedEventRepository {

    private val log = LoggerFactory.getLogger(javaClass)
    private val store = mutableMapOf<String, SearchKeywordsUpdatedEvent>()

    override fun save(event: SearchKeywordsUpdatedEvent) {
        store[event.sku] = event
    }

    override fun get(sku: String): SearchKeywordsUpdatedEvent? {
        return store[sku]
    }

    override fun delete(sku: String) {
        store.remove(sku)
    }
}
