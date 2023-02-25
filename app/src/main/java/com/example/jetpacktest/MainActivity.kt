package com.example.jetpacktest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacktest.ui.theme.JetpackTestTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Loader()
                }
            }
        }
        randomWebRequest()
    }

    private fun randomWebRequest() {
        // Create Web Request
        val net = Thread(Runnable {
            val client = OkHttpClient()
            val response = JSONObject(run("https://www.boredapi.com/api/activity", client))
            Log.i("HTTP Log", response.getDouble("accessibility").toFloat().toString())
            setContent {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content(data = response)
                }
            }
        })
        net.start()
    }

    private fun run(url: String, client: OkHttpClient): String {
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.body.string()
        } catch (e: IOException) {
            Log.e("IO Error", "An Error Occurred when executing http request")
            "{\"Error\": \"Failed Request\"}"
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Loader() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                Modifier
                    .height(60.dp)
                    .width(60.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(Modifier.height(40.dp))
                    Text(text = "Please Wait...", textAlign = TextAlign.Center)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content(data: JSONObject) {
        JetpackTestTheme {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Activity:", fontSize = 24.sp, textAlign = TextAlign.Left)
                        Text(text = data.getString("activity"), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Category:", fontSize = 24.sp, textAlign = TextAlign.Left)
                        Text(text = data.getString("type"), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Price:", fontSize = 24.sp, textAlign = TextAlign.Left)
                        LinearProgressIndicator(data.getDouble("price").toFloat())
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Participants:", fontSize = 24.sp, textAlign = TextAlign.Left)
                        Text(
                            text = data.getInt("participants").toString(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Accessibility:", fontSize = 24.sp, textAlign = TextAlign.Left)
                        LinearProgressIndicator(data.getDouble("accessibility").toFloat())
                        Spacer(modifier = Modifier.height(15.dp))
                        Button(onClick = {
                            setContent {
                                Surface(
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.background
                                ) {
                                    Loader()
                                }
                            }
                            randomWebRequest()
                        }) {
                            Text(text = "New Request")
                        }
                    }
            }
        }
    }
}