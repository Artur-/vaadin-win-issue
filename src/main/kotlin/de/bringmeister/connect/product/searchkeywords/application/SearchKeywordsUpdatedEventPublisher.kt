package de.bringmeister.connect.product.searchkeywords.application

import de.bringmeister.connect.product.searchkeywords.domain.SearchKeywordsUpdatedEvent
import de.bringmeister.connect.product.searchkeywords.domain.SearchKeywordsUpdatedEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class SearchKeywordsUpdatedEventPublisher(
    private val keywordUpdatedEventRepo: SearchKeywordsUpdatedEventRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun generatedUpdatedEvent(sku: String, keywordList: Set<String>) {
        val keywordEvent = SearchKeywordsUpdatedEvent(
            objectId = sku,
            revision = 1L,
            lastModifiedDate = OffsetDateTime.now(),
            sku = sku,
            searchKeywords = keywordList
        )
        keywordUpdatedEventRepo.save(keywordEvent)
        log.info("Saved search-keywords-updated-event. [event={}]", keywordEvent)

    }
}
