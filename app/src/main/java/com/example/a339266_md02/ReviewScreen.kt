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
import androidx.compose.foundation.clickable
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ReviewScreen(navController: NavController) {
    var purchases by remember { mutableStateOf(listOf<JSONObject>()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // New: State for selected item
    var selectedPurchase by remember { mutableStateOf<JSONObject?>(null) }

    LaunchedEffect(true) {
        AdafruitApiService.fetchData { result, _ ->
            if (result != null) {
                purchases = result
            }
            isLoading = false
        }
    }

    val filtered = purchases.filter {
        it.optString("cryptocurrency", "").contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Cryptocurrency Exchanger",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Specific Purchases:") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
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

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filtered) { entry ->
                            val isSelected = selectedPurchase == entry
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPurchase = if (isSelected) null else entry
                                    }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(entry.optString("cryptocurrency", "-"))
                                Text(entry.optString("initialPurchasePrice", "-"))
                                Text("£${entry.optString("investmentAmount", "0.00")}")
                                Text("${entry.optString("percentageToMarket", "?")}%")
                                Text("${entry.optString("holdingTime", "?")} Days")
                            }
                            if (isSelected) {
                                Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
                            } else {
                                Divider()
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row with navigation and "Crypto Value" button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text("Previous Page")
            }

            Button(
                onClick = {
                    selectedPurchase?.let {
                        val jsonEncoded = URLEncoder.encode(it.toString(), "UTF-8")
                        val cryptoId = it.optString("cryptocurrency", "").lowercase()
                        navController.navigate("cryptoValue/$cryptoId/$jsonEncoded")
                    }
                },
                enabled = selectedPurchase != null
            ) {
                Text("Crypto Value")
            }
        }
    }
}