package com.vogel.testwebsocket

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val socketStatus = mutableStateOf(false)

    private val _messages = mutableStateOf(Pair(false, ""))
    var messages = mutableStateOf("World")

    private val _isUserLogged = MutableSharedFlow<UiEvent>()
    val isUserConnected = _isUserLogged.asSharedFlow()

    private val okHttpClient: OkHttpClient = OkHttpClient()
    private val webSocketListener = WebSocketListen(this)

    init {
        establishConnectionWithSocket()
    }

    fun establishConnectionWithSocket() {
        val request = Request.Builder()
            .url(WEBSOCKET_URL)
            .build()

        okHttpClient.newWebSocket(request, webSocketListener)
    }

    fun addMessage(message: Pair<Boolean, String>) = viewModelScope.launch(Dispatchers.Main) {
        if (socketStatus.value) {
            _messages.value = message
            if (message.second == "true") {
                _isUserLogged.emit(UiEvent.Navigate(null))
            }
            messages.value = message.second
        }
    }

    fun setStatus(status: Boolean) = viewModelScope.launch(Dispatchers.Main) {
        socketStatus.value = status
        if (!socketStatus.value) {
            _isUserLogged.emit(UiEvent.Error(null))
        }
    }
}

sealed class UiEvent {
    data class Navigate(val params: Any?) : UiEvent()
    data class Error(val params: Any?) : UiEvent()
}

const val WEBSOCKET_URL =
    "wss://s8776.nyc1.piesocket.com/v3/1?api_key=Svg4DykETRUAGw2E9ZprvZYM9h2RFZaZD8oWNPGx&notify_self=1"
