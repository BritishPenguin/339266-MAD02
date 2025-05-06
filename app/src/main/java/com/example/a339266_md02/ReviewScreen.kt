package com.example.a339266_md02

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.a339266_md02.network.AdafruitApiService
import org.json.JSONObject

@Composable
fun ReviewScreen(navController: NavController) {
    //Cryptocurrency Purchases fetched from Adafruit
    var purchases by remember { mutableStateOf(listOf<JSONObject>()) }

    // User input for list filtering
    var searchQuery by remember { mutableStateOf("") }

    // Checks to see if data is still being sent
    var isLoading by remember { mutableStateOf(true) }

    // 🌐 Fetch data from Adafruit when the screen is first composed
    LaunchedEffect(true) {
        AdafruitApiService.fetchData { result, _ ->
            if (result != null) {
                purchases = result
            }
            isLoading = false
        }
    }

    // Filtering Purchases based on name of cryptocurrency
    val filtered = purchases.filter {
        it.optString("cryptocurrency", "").contains(searchQuery, ignoreCase = true)
    }

    //UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        //Title
        Text(
            text = "Cryptocurrency Exchanger",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Specific Purchases:") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Creates Scrollable List for viewing large numbers of cryptocurrency purchases
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (isLoading) {
                // Shows a loading indicator due to Adafruit rate limiting
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                //Creates a table layout
                Column(modifier = Modifier.fillMaxSize()) {
                    // Table headers
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cryptocurrency")
                        Text("Amount")
                        Text("Investment")
                        Text("Percentage")
                        Text("Days Left")
                    }

                    Divider()

                    // Column to Show List Results
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filtered) { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(entry.optString("cryptocurrency", "-"))
                                Text(entry.optString("initialPurchasePrice", "-"))
                                Text("£${entry.optString("investmentAmount", "0.00")}")
                                Text("${entry.optString("percentageToMarket", "?")}%")
                                Text("${entry.optString("holdingTime", "?")} Days")
                            }
                            Divider()
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        //Back Button to return to main page
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Previous Page")
        }
    }
}