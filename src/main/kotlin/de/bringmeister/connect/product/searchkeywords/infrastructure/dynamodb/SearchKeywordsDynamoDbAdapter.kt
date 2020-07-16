package de.bringmeister.connect.product.searchkeywords.infrastructure.dynamodb

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import de.bringmeister.connect.product.searchkeywords.domain.SearchKeyword
import de.bringmeister.connect.product.searchkeywords.domain.SearchKeywordRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.Date

@Service
class SearchKeywordsDynamoDbAdapter : SearchKeywordRepository {

    private val log = LoggerFactory.getLogger(javaClass)
    private val store = mutableMapOf<String, SearchKeyword>()

    override fun find(id: String): SearchKeyword? {
        return store[id]
    }

    override fun save(keyword: SearchKeyword) {
        store[keyword.id] = keyword
    }

    override fun exists(id: String): Boolean {
        return store[id] != null
    }

    override fun delete(id: String) {
        store.remove(id)
    }

    override fun findAll(): List<String> {
        return store.values.map { it.id }
    }
}
