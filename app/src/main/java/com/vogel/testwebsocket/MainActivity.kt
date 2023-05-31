package com.vogel.testwebsocket

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.vogel.testwebsocket.ui.theme.TestWebSocketTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TestWebSocketTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }

}

@Composable
fun Greeting(viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.isUserConnected.collectLatest { socketState ->
            when (socketState) {
                is UiEvent.Navigate -> {
                    ContextCompat.startActivity(
                        context,
                        Intent(context, TestRedirect::class.java),
                        null
                    )
                }
                is UiEvent.Error -> {
                    Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    Column(modifier = Modifier.padding(20.dp)) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = CutCornerShape(10.dp),
            onClick = {
                viewModel.establishConnectionWithSocket()
            }) {
            Text(
                text = "Connect to WebSocket"
            )
        }
        Text(text = "Hello ${viewModel.messages.value}!")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestWebSocketTheme {
    }
}