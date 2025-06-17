package com.glovo.composecodelabs.codelab1

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.plus

class ChatViewModel {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
    fun onSend() {
        _uiState.update {
            if (it.isMessageEditing != null) {
                it.copy(
                    messages = it.messages.map { message ->
                        if (message.id == it.isMessageEditing) message.copy(text = it.messageInput)
                        else message
                    },
                    messageInput = "",
                    isMessageEditing = null,
                )
            } else {
                it.copy(
                    messages = it.messages + Message(
                        id = it.messages.size,
                        text = it.messageInput
                    ),
                    messageInput = "",
                )
            }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(messageInput = text) }
    }

    fun onEdit(id: Int) {
        _uiState.update {
            it.copy(
                messageInput = it.messages.first { it.id == id }.text,
                isMessageEditing = id
            )
        }
    }
}

data class UiState(
    val messages: List<Message> = listOf(),
    val messageInput: String = "",
    val isMessageEditing: Int? = null,
)

data class Message(
    val id: Int,
    val text: String,
)