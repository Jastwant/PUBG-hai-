package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Offer
import com.example.data.models.ShopProduct
import com.example.data.repository.EsportsRepository
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ShopEarnScreen() {
    val context = LocalContext.current
    val wallet by EsportsRepository.wallet.collectAsState()
    val products by EsportsRepository.products.collectAsState()
    val offers by EsportsRepository.offers.collectAsState()
    val appConfig by EsportsRepository.appConfig.collectAsState()
    val todayWatchCount by EsportsRepository.todayWatchCount.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("WATCH & EARN", "SHOP & VOUCHERS", "OFFERS & TASKS")

    // Video Simulator State
    var isWatchingVideo by remember { mutableStateOf(false) }
    var videoCountdown by remember { mutableIntStateOf(10) }

    LaunchedEffect(isWatchingVideo) {
        if (isWatchingVideo) {
            videoCountdown = 10
            while (videoCountdown > 0) {
                delay(1000)
                videoCountdown--
            }
            isWatchingVideo = false
            val res = EsportsRepository.completeWatchVideo()
            if (res.isSuccess) {
                Toast.makeText(context, res.getOrNull() ?: "Reward Credited!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, res.exceptionOrNull()?.message ?: "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        containerColor = DarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = CyberCyan,
                divider = { HorizontalDivider(color = DarkBorder) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = if (selectedTab == index) CyberCyan else TextSecondary
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Watch & Earn Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Surface(
                                color = DarkSurface,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(CyberGold.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = CyberGold, modifier = Modifier.size(36.dp))
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("WATCH VIDEO SPONSORS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                    Text(
                                        "Earn ₹${"%.0f".format(appConfig.watchEarnRewardCash)} Bonus Cash + 50 points per completed video sponsor stream.",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    StatusBadge(
                                        text = "TODAY: $todayWatchCount / ${appConfig.watchEarnDailyLimit} VIDEOS COMPLETED",
                                        color = if (todayWatchCount >= appConfig.watchEarnDailyLimit) CrimsonRed else EmeraldGreen
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    if (isWatchingVideo) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(DarkBg)
                                                .padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            CircularProgressIndicator(color = CyberCyan)
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                "Playing Sponsor Stream: $videoCountdown s remaining",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text("Anti-cheat: Please do not close until timer finishes.", color = TextMuted, fontSize = 11.sp)
                                        }
                                    } else {
                                        NeonGradientButton(
                                            text = if (todayWatchCount >= appConfig.watchEarnDailyLimit) "DAILY LIMIT REACHED" else "WATCH VIDEO NOW (+₹2.00)",
                                            enabled = todayWatchCount < appConfig.watchEarnDailyLimit,
                                            onClick = { isWatchingVideo = true },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text("EARNING RULES & ANTI-CHEAT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        item {
                            Surface(
                                color = DarkSurface,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("1. Bonus balance can be utilized up to 20% on any tournament match fee.", color = TextSecondary, fontSize = 12.sp)
                                    Text("2. Minimum 10 seconds viewing required to prevent automated bots.", color = TextSecondary, fontSize = 12.sp)
                                    Text("3. Limit resets every midnight at 00:00 AM IST.", color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Shop Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("VOUCHERS & MERCHANDISE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                Text("Your Balance: ₹${"%.0f".format(wallet.totalBalance)} • ${wallet.points} pts", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        items(products) { prod ->
                            Surface(
                                color = DarkSurface,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        StatusBadge(text = prod.category.uppercase(), color = CyberCyan)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(prod.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(prod.description, color = TextSecondary, fontSize = 11.sp, maxLines = 2)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("₹${"%.0f".format(prod.priceReal)}", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                            Text("OR ${prod.pricePoints} pts", color = ElectricViolet, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Button(
                                            onClick = {
                                                val res = EsportsRepository.purchaseProduct(prod.id, false)
                                                if (res.isSuccess) {
                                                    Toast.makeText(context, res.getOrNull() ?: "Success", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, res.exceptionOrNull()?.message ?: "Error", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("BUY (₹)", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        OutlinedButton(
                                            onClick = {
                                                val res = EsportsRepository.purchaseProduct(prod.id, true)
                                                if (res.isSuccess) {
                                                    Toast.makeText(context, res.getOrNull() ?: "Success", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, res.exceptionOrNull()?.message ?: "Error", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("PTS", color = CyberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Task Offers Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("EARN FREE CASH OFFERS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }

                        items(offers) { offer ->
                            Surface(
                                color = DarkSurface,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(offer.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(offer.description, color = TextSecondary, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("+₹${"%.0f".format(offer.rewardCash)} Cash • +${offer.rewardPoints} Points", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = {
                                            EsportsRepository.creditWallet(com.example.data.models.TransactionType.BONUS, offer.rewardCash, offer.id, "Reward for ${offer.title}")
                                            Toast.makeText(context, "Task completed! ₹${offer.rewardCash} credited.", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("CLAIM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
