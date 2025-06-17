package com.glovo.composecodelabs.codelab2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.glovo.composecodelabs.codelab2.GroceryListUiState.AddEditItemForm

@Composable
fun GroceryList(viewModel: GroceryListViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        floatingActionButton = {
            Card(
                onClick = { viewModel.onOpenAddItem() },
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = SpaceBetween,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Item"
                    )
                }
            }
        },
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(uiState.groceryList) { item ->
                GroceryItem(
                    item = item,
                    onToggleClicked = { viewModel.onToggleItem(item.id) },
                    onRemove = { viewModel.onRemoveItem(item.id) },
                    onEdit = { viewModel.onOpenEditItem(item.id, item.name, item.quantity) },
                )
            }
        }
        uiState.addEditItemForm?.let { addItemForm ->
            AddItemBottomForm(
                addEditItemForm = addItemForm,
                onHide = { viewModel.onHideAddItem() },
                onSetName = { viewModel.onFormSetName(it) },
                onSetQuantity = { viewModel.onFormSetQuantity(it) },
                onAccept = { viewModel.onFormAccept() },
            )
        }
    }
}

@Composable
private fun GroceryItem(
    item: GroceryItem,
    onToggleClicked: () -> Unit = {},
    onRemove: () -> Unit,
    onEdit: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> onEdit()
                SwipeToDismissBoxValue.EndToStart -> onRemove()
                SwipeToDismissBoxValue.Settled -> {}
            }
            return@rememberSwipeToDismissBoxState false
        },
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier.fillMaxWidth(),
        backgroundContent = { DismissBackground(dismissState) },
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = SpaceBetween,
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RoundedCornerCheckbox(
                        isChecked = item.isBought,
                        onClick = onToggleClicked,
                        modifier = Modifier
                    )
                    Text(
                        item.name,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (item.quantity > 1) {
                        Text(
                            "${item.quantity}",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        })
}

@Composable
private fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Color.Yellow
        SwipeToDismissBoxValue.EndToStart -> Color.Red
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = "Edit"
        )
        Spacer(modifier = Modifier)
        Icon(
            Icons.Default.Delete,
            contentDescription = "Delete"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddItemBottomForm(
    addEditItemForm: AddEditItemForm,
    onHide: () -> Unit,
    onSetName: (String) -> Unit,
    onSetQuantity: (Int) -> Unit,
    onAccept: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = {
            onHide()
        },
        sheetState = sheetState
    ) {
        Column {
            TextField(
                value = addEditItemForm.name,
                onValueChange = onSetName,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = addEditItemForm.quantity.toString(),
                onValueChange = { onSetQuantity(it.toIntOrNull() ?: 0) },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (addEditItemForm.id == null) "Add" else "Update")
            }
        }
    }
}

@Preview
@Composable
private fun GroceryListPreview() {
    val viewModel = remember { GroceryListViewModel() }
    LaunchedEffect(Unit) {
        viewModel.addItem("Milk", 1)
        viewModel.addItem("Eggs", 12)
        viewModel.addItem("Bread", 2)
        viewModel.addItem("Butter", 1)
        viewModel.addItem("Cheese", 1)
    }
    GroceryList(viewModel)
}