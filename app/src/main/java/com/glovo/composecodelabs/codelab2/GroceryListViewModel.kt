package com.glovo.composecodelabs.codelab2

import com.glovo.composecodelabs.codelab2.GroceryListUiState.AddEditItemForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import kotlin.collections.plus

class GroceryListViewModel {
    private val _uiState = MutableStateFlow(GroceryListUiState())
    val uiState = _uiState.asStateFlow()

    fun onRemoveItem(id: String) {
        _uiState.update {
            it.copy(
                groceryList = it.groceryList.filter { it.id != id }
            )
        }
    }

    fun onToggleItem(id: String) {
        _uiState.update {
            it.copy(
                groceryList = it.groceryList.map { item ->
                    if (item.id == id) item.copy(isBought = !item.isBought)
                    else item
                }
            )
        }
    }

    fun onOpenAddItem() {
        _uiState.update {
            it.copy(addEditItemForm = AddEditItemForm(
                id = null,
                name = "",
                quantity = 1,
            ))
        }
    }

    fun onOpenEditItem(id: String, name: String, quantity: Int) {
        _uiState.update {
            it.copy(addEditItemForm = AddEditItemForm(
                id = id,
                name = name,
                quantity = quantity,
            ))
        }
    }

    fun onHideAddItem() {
        _uiState.update {
            it.copy(addEditItemForm = null)
        }
    }

    fun onFormSetName(name: String) {
        _uiState.update {
            val form = it.addEditItemForm ?: return@update it
            it.copy(addEditItemForm = form.copy(name = name))
        }
    }

    fun onFormSetQuantity(quantity: Int) {
        _uiState.update {
            val form = it.addEditItemForm ?: return@update it
            it.copy(addEditItemForm = form.copy(quantity = quantity))
        }
    }

    fun onFormAccept() {
        val form = uiState.value.addEditItemForm ?: return
        if (form.id != null) {
            updateItem(form.id, form.name, form.quantity)
        } else {
            addItem(form.name, form.quantity)
        }
    }

    fun addItem(name: String, quantity: Int) {
        _uiState.update {
            it.copy(
                groceryList = it.groceryList + GroceryItem(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    quantity = quantity,
                    isBought = false,
                ),
                addEditItemForm = null,
            )
        }
    }

    fun updateItem(id: String, name: String, quantity: Int) {
        _uiState.update {
            it.copy(
                groceryList = it.groceryList.map { item ->
                    if (item.id == id) item.copy(name = name, quantity = quantity)
                    else item
                },
                addEditItemForm = null,
            )
        }
    }
}

data class GroceryListUiState(
    val groceryList: List<GroceryItem> = emptyList(),
    val addEditItemForm: AddEditItemForm? = null,
) {
    data class AddEditItemForm(
        val id: String?,
        val name: String,
        val quantity: Int,
    )
}

data class GroceryItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val isBought: Boolean,
)