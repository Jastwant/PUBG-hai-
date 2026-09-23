package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.models.LeaderboardEntry
import com.example.data.repository.EsportsRepository
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen() {
    val leaderboard by EsportsRepository.leaderboard.collectAsState()
    var selectedTimeframe by remember { mutableStateOf("THIS WEEK") }
    var selectedPlayer by remember { mutableStateOf<LeaderboardEntry?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Text(
                text = "ESPORTS CHAMPIONSHIPS RANKING",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Top tier fraggers and tournament champions updated in real time.",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
            )
        }

        // Timeframe Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("THIS WEEK", "THIS MONTH", "ALL TIME").forEach { tf ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedTimeframe == tf) CyberCyan else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedTimeframe == tf) CyberCyan else DarkBorder),
                        modifier = Modifier.weight(1f).clickable { selectedTimeframe = tf }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = tf,
                                color = if (selectedTimeframe == tf) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Top 3 Podium
        if (leaderboard.size >= 3) {
            val first = leaderboard[0]
            val second = leaderboard[1]
            val third = leaderboard[2]

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place
                    PodiumItem(
                        rank = 2,
                        entry = second,
                        badgeColor = Color.LightGray,
                        height = 110.dp,
                        onClick = { selectedPlayer = second }
                    )
                    // 1st Place (Taller)
                    PodiumItem(
                        rank = 1,
                        entry = first,
                        badgeColor = CyberGold,
                        height = 135.dp,
                        onClick = { selectedPlayer = first }
                    )
                    // 3rd Place
                    PodiumItem(
                        rank = 3,
                        entry = third,
                        badgeColor = RadiantOrange,
                        height = 95.dp,
                        onClick = { selectedPlayer = third }
                    )
                }
            }
        }

        item {
            Text(
                text = "FULL LEADERBOARD",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(leaderboard) { entry ->
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPlayer = entry }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = when (entry.rank) {
                                1 -> CyberGold.copy(alpha = 0.2f)
                                2 -> Color.LightGray.copy(alpha = 0.2f)
                                3 -> RadiantOrange.copy(alpha = 0.2f)
                                else -> DarkElevated
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "#${entry.rank}",
                                    color = when (entry.rank) {
                                        1 -> CyberGold
                                        2 -> Color.White
                                        3 -> RadiantOrange
                                        else -> TextSecondary
                                    },
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(entry.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("IGN: ${entry.inGameName} • ${entry.kills} Kills", color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("₹${"%.0f".format(entry.totalEarnings)}", color = CyberCyan, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        Text("${entry.points} pts", color = CyberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Player Stats Breakdown Modal
    if (selectedPlayer != null) {
        val p = selectedPlayer!!
        AlertDialog(
            onDismissRequest = { selectedPlayer = null },
            containerColor = DarkCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = CyberGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(p.username, color = Color.White, fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column {
                    Text("In-Game Tag: ${p.inGameName}", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DarkElevated).padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("MATCHES", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("${p.matchesPlayed}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("WINS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("${p.wins}", color = EmeraldGreen, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("KILLS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("${p.kills}", color = RadiantOrange, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("WIN RATE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("${((p.wins.toFloat() / p.matchesPlayed.toFloat()) * 100).toInt()}%", color = CyberGold, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(DarkElevated).padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Tournament Earnings:", color = TextSecondary, fontSize = 12.sp)
                        Text("₹${"%.2f".format(p.totalEarnings)}", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedPlayer = null },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Text("CLOSE", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun PodiumItem(
    rank: Int,
    entry: LeaderboardEntry,
    badgeColor: Color,
    height: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(badgeColor.copy(alpha = 0.2f))
                .border(2.dp, badgeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (rank == 1) Icons.Default.EmojiEvents else Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(entry.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
        Text("₹${"%.0f".format(entry.totalEarnings)}", color = CyberCyan, fontWeight = FontWeight.Black, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            color = DarkElevated,
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)),
            modifier = Modifier
                .width(85.dp)
                .height(height)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "#$rank",
                    color = badgeColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
