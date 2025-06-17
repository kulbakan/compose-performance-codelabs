package com.glovo.composecodelabs.codelab1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ChatScreen(vm: ChatViewModel) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val bodyLazyListState = rememberLazyListState()
    println("ChatScreen recomposition")
    Scaffold(
        topBar = {
            println("Scaffold topBar recomposition")
            ChatTopBar(uiState.messages, bodyLazyListState)
        },
        bottomBar = {
            println("Scaffold bottomBar recomposition")
            ChatBottomBar(uiState.messageInput, vm::onInputChanged, vm::onSend)
        },
    ) { paddingValues ->
        println("Scaffold content recomposition")
        Chat(uiState.messages, vm::onEdit, paddingValues, bodyLazyListState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(messages: List<Message>, bodyLazyListState: LazyListState) {
    println("ChatTopBar recomposition")
    TopAppBar(
        title = {
            println("TopAppBar title recomposition")
            Text("Messages ${messages.size}")
        },
        modifier = Modifier.applyIf(bodyLazyListState.isScrolled) {
            Modifier
                .zIndex(HEADER_Z_INDEX)
                .shadow(HEADER_SHADOW)
        }
    )
}

@Composable
fun ChatBottomBar(messageInput: String, onInputChanged: (String) -> Unit, onSend: () -> Unit) {
    println("ChatBottomBar recomposition")
    Row(modifier = Modifier.fillMaxWidth()) {
        println("Row recomposition")
        TextField(
            value = messageInput,
            onValueChange = onInputChanged,
            modifier = Modifier
                .padding(start = 12.dp, top = 12.dp)
                .weight(1f),
        )
        Button(
            onClick = onSend,
            modifier = Modifier.padding(12.dp)
        ) { Text("Send") }
    }
}

@Composable
fun Chat(
    messages: List<Message>,
    onEdit: (Int) -> Unit,
    paddingValues: PaddingValues,
    bodyLazyListState: LazyListState
) {
    println("Chat recomposition")
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
            .fillMaxHeight(),
        state = bodyLazyListState
    ) {
        println("LazyColumn recomposition")
        items(messages) { message ->
            MessageItem(message, onEdit)
        }
    }
}

@Composable
fun MessageItem(message: Message, onEdit: (Int) -> Unit) {
    println("MessageItem (message ${message.id}) recomposition")
    Card(
        modifier = Modifier
            .padding(12.dp)
            .clickable { onEdit(message.id) },
    ) {
        Text(message.text, modifier = Modifier.padding(12.dp))
    }
}

val LazyListState.isScrolled: Boolean
    get() = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0

const val HEADER_Z_INDEX = 10f
val HEADER_SHADOW = 8.dp

@Composable
fun Modifier.applyIf(condition: Boolean, modifier: @Composable Modifier.() -> Modifier): Modifier =
    if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }

@Preview
@Composable
fun ChatScreenPreview() {
    val vm = remember { ChatViewModel() }
    LaunchedEffect(Unit) {
        vm.onInputChanged("Hello")
        vm.onSend()
        vm.onInputChanged("World")
        vm.onSend()
    }
    ChatScreen(vm)
}