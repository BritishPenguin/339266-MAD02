package com.example.a339266_md02

// Android Imports
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

// Data Class that stores selected cryptocurrency and cryptocurrency coinGecko ID
data class Cryptocurrency(val name: String, val coingeckoId: String)

// Main Class starting point of the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptocurrencyExchangerApp()
        }
    }
}

// Composable for managing the navigation between pages
@Composable
fun CryptocurrencyExchangerApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        // Navigation to Purchase screen parses CryptoID for importing Crypto exchange from CoinGecko
        composable(
            "purchase/{cryptoId}",
            arguments = listOf(navArgument("cryptoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: "bitcoin"
            PurchaseScreen(navController = navController, cryptoAbbreviation = cryptoId)
        }
        // Navigation to CryptoValueScreen
        composable(
            "cryptoValue/{cryptoId}/{json}",
            arguments = listOf(
                navArgument("cryptoId") { type = NavType.StringType },
                navArgument("json") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cryptoId = backStackEntry.arguments?.getString("cryptoId") ?: ""
            val json = backStackEntry.arguments?.getString("json") ?: ""
            CryptoValueScreen(navController = navController, cryptoId = cryptoId, encodedJson = json)
        }
        // Navigation to Review screen (no data parsing)
        composable("review") {
            ReviewScreen(navController)
        }
    }
}

// Home Screen where available cryptocurrencies are displayed (search bar included)
@Composable
fun HomeScreen(navController: NavController) {
    val allCryptos = remember {
        listOf(
            Cryptocurrency("0x", "0x"),
            Cryptocurrency("Ampleforth", "ampleforth"),
            Cryptocurrency("Ankr", "ankr"),
            Cryptocurrency("Apollo", "apollo-currency"),
            Cryptocurrency("Bancor Network Token", "bancor"),
            Cryptocurrency("Binance Coin", "binancecoin"),
            Cryptocurrency("Bitcoin", "bitcoin"),
            Cryptocurrency("Bitcoin-Cash", "bitcoin-cash"),
            Cryptocurrency("Cardano", "cardano"),
            Cryptocurrency("Chainlink", "chainlink"),
            Cryptocurrency("Dash", "dash"),
            Cryptocurrency("Ethereum", "ethereum"),
            Cryptocurrency("Tether", "tether"),
            Cryptocurrency("Polkadot", "polkadot"),
            Cryptocurrency("Uniswap", "uniswap"),
            Cryptocurrency("Litecoin", "litecoin"),
            Cryptocurrency("Internet-Computer", "internet-computer"),
            Cryptocurrency("EOS", "eos"),
            Cryptocurrency("The-Graph", "the-graph"),
            Cryptocurrency("Maker", "maker"),
            Cryptocurrency("Numeraire", "numeraire"),
            Cryptocurrency("Decentraland", "decentraland"),
            Cryptocurrency("Sushi", "sushi"),
            Cryptocurrency("Filecoin", "filecoin")
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredCryptos = allCryptos.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.coingeckoId.contains(searchQuery, ignoreCase = true)
    }

    var selectedCrypto by remember { mutableStateOf<Cryptocurrency?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Cryptocurrency Exchanger",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Search Currencies", modifier = Modifier.padding(end = 8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredCryptos) { crypto ->
                    val isSelected = selectedCrypto == crypto
                    val backgroundColor = if (isSelected) Color.LightGray else Color.Transparent
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .selectable(
                                selected = isSelected,
                                onClick = {
                                    selectedCrypto = if (isSelected) null else crypto
                                }
                            )
                            .background(backgroundColor),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = crypto.name)
                        Text(text = crypto.coingeckoId)
                    }
                    Divider()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    selectedCrypto?.let {
                        navController.navigate("purchase/${it.coingeckoId}")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                enabled = selectedCrypto != null
            ) {
                Text(
                    text = "Purchase",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    softWrap = true,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Button(
                onClick = {
                    navController.navigate("review")
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Review",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    softWrap = true,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}