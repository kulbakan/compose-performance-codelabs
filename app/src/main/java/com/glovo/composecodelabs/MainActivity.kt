package com.glovo.composecodelabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.glovo.composecodelabs.codelab1.ChatScreen
import com.glovo.composecodelabs.codelab1.ChatViewModel
import com.glovo.composecodelabs.codelab2.GroceryList
import com.glovo.composecodelabs.codelab2.GroceryListViewModel
import com.glovo.composecodelabs.ui.theme.ComposeCodelabsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCodelabsTheme {

                // Codelab 1
                val vm = remember { ChatViewModel() }
                LaunchedEffect(Unit) {
                    vm.onInputChanged("Hello")
                    vm.onSend()
                    vm.onInputChanged("World")
                    vm.onSend()
                }
                ChatScreen(vm)

                // Codelab 2
//                val viewModel = remember { GroceryListViewModel() }
//                LaunchedEffect(Unit) {
//                    viewModel.addItem("Milk", 1)
//                    viewModel.addItem("Eggs", 12)
//                    viewModel.addItem("Bread", 2)
//                    viewModel.addItem("Butter", 1)
//                    viewModel.addItem("Cheese", 1)
//                }
//                GroceryList(viewModel)
            }
        }
    }
}