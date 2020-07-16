package de.bringmeister.connect.product.searchkeywords.domain

import java.time.OffsetDateTime

data class SearchKeywordsUpdatedEvent(
     val objectId: String,
     val revision: Long,
     val lastModifiedDate: OffsetDateTime,
    val sku: String,
    val searchKeywords: Set<String> = emptySet()
)
