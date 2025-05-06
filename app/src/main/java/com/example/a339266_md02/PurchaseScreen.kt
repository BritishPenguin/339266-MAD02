package com.example.a339266_md02

// Android and Compose imports
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a339266_md02.network.AdafruitApiService
import com.example.a339266_md02.network.RetrofitInstance
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun PurchaseScreen(
    navController: NavController,
    cryptoAbbreviation: String = "bitcoin",
    cryptoName: String = "Bitcoin" // Name for displaying or parsing
) {
    // The input fields for cryptocurrency information
    var investmentAmount by remember { mutableStateOf("") }
    var highestSellingPrice by remember { mutableStateOf("") }
    var lowestSellingPrice by remember { mutableStateOf("") }
    var percentageToMarket by remember { mutableStateOf("") }
    var holdingTime by remember { mutableStateOf("") }
    var initialPurchasePrice by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    //Fetches Cryptocurrency Exchange Rates (Investment Amount (gdp) ---> Crypto Amount (crypto)
    LaunchedEffect(investmentAmount, cryptoAbbreviation) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(500)
            val gbp = investmentAmount.toDoubleOrNull()
            val safeId = cryptoAbbreviation.lowercase().trim()

            if (gbp != null && gbp > 0 && safeId.isNotBlank()) {
                try {
                    val response = RetrofitInstance.api.getPrice(ids = safeId, vsCurrencies = "gbp")
                    val price = response[safeId]?.get("gbp") ?: 0.0
                    if (price > 0) {
                        val cryptoAmount = gbp / price
                        initialPurchasePrice = "%.8f".format(cryptoAmount)
                    } else {
                        initialPurchasePrice = ""
                    }
                } catch (e: Exception) {
                    initialPurchasePrice = ""
                }
            } else {
                initialPurchasePrice = ""
            }
        }
    }

    //Checks to see if fields have something inputted
    val isFormValid by derivedStateOf {
        val investment = investmentAmount.toDoubleOrNull() ?: -1.0
        val high = highestSellingPrice.toDoubleOrNull() ?: -1.0
        val low = lowestSellingPrice.toDoubleOrNull() ?: -1.0
        val percent = percentageToMarket.toDoubleOrNull() ?: -1.0
        val time = holdingTime.toIntOrNull() ?: -1
        val initial = initialPurchasePrice.toDoubleOrNull() ?: -1.0

        investment > 0 && high > 0 && low > 0 && percent in 0.0..100.0 && time > 0 && initial > 0
    }

    // Only allows integers and decimals
    fun filterToDigits(value: String): String {
        return value.filter { it.isDigit() || it == '.' }
    }

    // UI Layout
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Cryptocurrency Exchanger",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Investment input
        OutlinedTextField(
            value = investmentAmount,
            onValueChange = { investmentAmount = filterToDigits(it) },
            label = { Text("Enter Investment Amount: £GBP") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // High/Low selling price inputs
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = highestSellingPrice,
                onValueChange = { highestSellingPrice = filterToDigits(it) },
                label = { Text("Highest Selling Price: £GBP") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = lowestSellingPrice,
                onValueChange = { lowestSellingPrice = filterToDigits(it) },
                label = { Text("Lowest Selling Price: £GBP") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Percentage & Holding Time inputs
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = percentageToMarket,
                onValueChange = { percentageToMarket = filterToDigits(it) },
                label = { Text("Percentage to Market:") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = holdingTime,
                onValueChange = { holdingTime = it.filter { c -> c.isDigit() } },
                label = { Text("Holding Time (days):") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Read-Only Calculated Crypto Amount based of Investment Amount
        OutlinedTextField(
            value = initialPurchasePrice,
            onValueChange = {},
            label = { Text("Crypto Amount") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Notes input (Ignore data validation)
        Text("Notes:", fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth().height(150.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons For Purchasing Cryptocurrencies and Returning to Home Page
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    // 👇 JSON object containing all cryptocurrency data inputted
                    val rawData = JSONObject().apply {
                        put("cryptocurrency", cryptoAbbreviation) // Crypto ID from Main Page sent as well
                        put("investmentAmount", investmentAmount)
                        put("highestSellingPrice", highestSellingPrice)
                        put("lowestSellingPrice", lowestSellingPrice)
                        put("percentageToMarket", percentageToMarket)
                        put("holdingTime", holdingTime)
                        put("initialPurchasePrice", initialPurchasePrice)
                        put("notes", notes)
                    }

                    val wrapped = JSONObject().apply {
                        put("value", rawData.toString()) //Editing input format to meet Adafruit requirements
                    }

                    AdafruitApiService.sendData(wrapped) { success, error ->
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            if (success) {
                                Toast.makeText(context, "Data sent to Adafruit IO successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to send data: $error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = isFormValid,
                modifier = Modifier.weight(1f)
            ) {
                Text("Purchase Crypto")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Previous Page")
            }
        }
    }
}