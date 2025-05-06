package com.example.a339266_md02

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a339266_md02.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URLDecoder

@Composable
fun CryptoValueScreen(cryptoId: String, encodedJson: String, navController: NavController) {
    val json = remember {
        val decoded = URLDecoder.decode(encodedJson, "UTF-8")
        JSONObject(decoded)
    }

    val investmentAmount = json.optDouble("investmentAmount", 0.0)
    val coinAmount = json.optDouble("initialPurchasePrice", 1.0)

    var currentUnitValue by remember { mutableStateOf<Double?>(null) }
    val scope = rememberCoroutineScope()

    // Fetch current unit value from CoinGecko
    LaunchedEffect(cryptoId) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getPrice(cryptoId, "gbp")
                val price = response[cryptoId]?.get("gbp")
                if (price != null) {
                    currentUnitValue = price
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Calculate values once price is available
    val currentValue = currentUnitValue?.let { it * coinAmount }
    val percentageChange = currentValue?.let {
        ((it - investmentAmount) / investmentAmount) * 100
    }
    val originalSingleValue = if (coinAmount != 0.0) investmentAmount / coinAmount else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Cryptocurrency Exchanger",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = "£%.2f".format(investmentAmount),
            onValueChange = {},
            label = { Text("Original Value:") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = currentValue?.let { "£%.2f".format(it) } ?: "Loading...",
            onValueChange = {},
            label = { Text("Current Value:") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = percentageChange?.let { "%.2f%%".format(it) } ?: "Loading...",
            onValueChange = {},
            label = { Text("Percentage Change:") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = "%.2f".format(originalSingleValue),
            onValueChange = {},
            label = { Text("Original Single Value:") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = currentUnitValue?.let { "£%.2f".format(it) } ?: "Loading...",
            onValueChange = {},
            label = { Text("Current Single Value:") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Previous Page")
        }
    }
}