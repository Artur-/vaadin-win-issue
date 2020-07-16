package de.bringmeister.connect.product.searchkeywords.ports.api

import de.bringmeister.connect.product.searchkeywords.application.*
import de.bringmeister.connect.product.searchkeywords.domain.SearchKeyword
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/searchkeywords")
class SearchKeywordController(
    private val keywordService: SearchKeywordService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun postSearchKeyword(@RequestBody newKeyword: SearchKeyword): ResponseEntity<Any> {
        return try {
            if (hasKeywordWithoutWhitespace(newKeyword) && newKeyword.skus.isNotEmpty()) {
                keywordService.createSearchKeyword(newKeyword)
                ResponseEntity(newKeyword, CREATED)
            } else {
                ResponseEntity(newKeyword, BAD_REQUEST)
            }
        } catch (e: SearchKeywordAlreadyExistsException) {
            ResponseEntity(newKeyword.id, BAD_REQUEST)
        }
    }

    @PutMapping("/{id}")
    fun replaceOrRemoveSkusOfSearchkeyword(
        @PathVariable id: String,
        @RequestBody skus: Set<String>
    ): ResponseEntity<Any> {
        return try {
            val updatedKeyword = keywordService.setOrRemoveSkusOfSearchKeyword(id, skus)
            ResponseEntity(updatedKeyword, OK)
        } catch (e: SearchKeywordDoNotExistsException) {
            ResponseEntity(id, BAD_REQUEST)
        }
    }

    @GetMapping("/products/{sku}/keywords")
    fun findKeywordsBySku(@PathVariable sku: String): ResponseEntity<Any> {
        return try {
            val keywordList = keywordService.findKeywordsBySku(sku)
            ResponseEntity(keywordList, OK)
        } catch (e: SkuNotFoundException) {
            ResponseEntity(sku, NOT_FOUND)
        }
    }

    @GetMapping("/keywords/{id}/skus")
    fun findSkusByKeyword(@PathVariable id: String): ResponseEntity<Any> {
        return try {
            val skus = keywordService.findSkusByKeyword(id)
            ResponseEntity(skus, OK)
        } catch (e: KeywordNotFoundException) {
            ResponseEntity(id, NOT_FOUND)
        }
    }

    @GetMapping("/keywords")
    fun listAllKeywords(): ResponseEntity<Any> {
        val allKeywords = keywordService.findAllKeywords()
        return ResponseEntity(allKeywords, OK)
    }
}

private fun hasKeywordWithoutWhitespace(searchKeyword: SearchKeyword): Boolean {
    return !"[\\s]".toRegex().containsMatchIn(searchKeyword.id)
}
