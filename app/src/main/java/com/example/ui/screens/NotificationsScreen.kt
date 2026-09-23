package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AppNotification
import com.example.data.repository.EsportsRepository
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    val notifications by EsportsRepository.notifications.collectAsState()

    Scaffold(
        topBar = {
            Surface(color = DarkSurface, tonalElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "NOTIFICATIONS & ALERTS",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = DarkBg
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No notifications yet", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications) { notif ->
                    Surface(
                        color = DarkSurface,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.category) {
                                            "WALLET" -> CyberGold.copy(alpha = 0.2f)
                                            "MATCH" -> CyberCyan.copy(alpha = 0.2f)
                                            "CHALLENGE" -> NeonPink.copy(alpha = 0.2f)
                                            else -> ElectricViolet.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.category) {
                                        "WALLET" -> Icons.Default.AccountBalanceWallet
                                        "MATCH" -> Icons.Default.SportsEsports
                                        "CHALLENGE" -> Icons.Default.SportsKabaddi
                                        else -> Icons.Default.Notifications
                                    },
                                    contentDescription = null,
                                    tint = when (notif.category) {
                                        "WALLET" -> CyberGold
                                        "MATCH" -> CyberCyan
                                        "CHALLENGE" -> NeonPink
                                        else -> ElectricViolet
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(notif.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    StatusBadge(notif.category, CyberCyan)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(notif.message, color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
