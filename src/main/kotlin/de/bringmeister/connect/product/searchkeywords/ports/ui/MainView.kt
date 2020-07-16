package de.bringmeister.connect.product.searchkeywords.ports.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.listbox.ListBox
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import de.bringmeister.connect.product.searchkeywords.application.SearchKeywordService
import de.bringmeister.connect.product.searchkeywords.domain.SearchKeyword
import de.bringmeister.connect.product.searchkeywords.domain.SearchKeywordRepository

@Route
class MainView(
    private val searchKeywordRepository: SearchKeywordRepository,
    private val searchKeywordService: SearchKeywordService
) : VerticalLayout() {

    init {

        //
        // Components
        //

        val listBox = ListBox<String>().apply {
            this.setItems(getSearchKeywords())
        }

        val skusArea = TextArea().apply { this.setSizeFull() }
        val newKeywordTxt = TextField()
        val newKeywordBtn = Button("New")
        val removeKeywordBtn = Button("Remove")
        val saveButton = Button("Save").apply {
            this.setWidthFull()
            this.isEnabled = false
        }

        skusArea.addInputListener {
            saveButton.isEnabled = true
        }

        listBox.addValueChangeListener {
            val searchKeyword = searchKeywordRepository.find(it.value)
            skusArea.value = searchKeyword?.skus?.joinToString("\n") ?: ""
            saveButton.isEnabled = false
        }

        newKeywordBtn.addClickListener {
            val searchKeyword = newKeywordTxt.value
            searchKeywordService.createSearchKeyword(SearchKeyword(searchKeyword, emptySet()))
            newKeywordTxt.clear()
            val newValues = getSearchKeywords(searchKeyword)
            listBox.setItems(newValues)
            listBox.value = searchKeyword
            skusArea.clear()
        }

        saveButton.addClickListener {
            val skus = skusArea.value.split("\n").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
            searchKeywordService.setOrRemoveSkusOfSearchKeyword(listBox.value, skus)
            saveButton.isEnabled = false
        }

        val createBox = HorizontalLayout().apply {
            this.add(newKeywordTxt)
            this.add(newKeywordBtn)
            this.add(removeKeywordBtn)
        }

        val saveBox = HorizontalLayout().apply {
            this.add(saveButton)
        }

        //
        // Layout
        //

        this.add(Label("Search Keyword Service"))

        val layout = HorizontalLayout().also {
            this.add(it)
            it.setSizeFull()
        }

        VerticalLayout().also {
            layout.add(it)
            it.add(listBox)
            it.add(createBox)
        }

        VerticalLayout().also {
            layout.add(it)
            it.add(skusArea)
            it.add(saveBox)
        }
    }

    private fun getSearchKeywords(additionalKeyword: String? = null): List<String> {
        val keywords = searchKeywordRepository.findAll() + additionalKeyword
        return keywords.filterNotNull().toList().sorted()
    }
}
