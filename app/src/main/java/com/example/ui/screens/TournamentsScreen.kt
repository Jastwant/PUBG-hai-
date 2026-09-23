package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Game
import com.example.data.models.JoinResult
import com.example.data.models.Match
import com.example.data.models.MatchStatus
import com.example.data.models.Tournament
import com.example.data.repository.EsportsRepository
import com.example.ui.components.MatchJoinConfirmationDialog
import com.example.ui.components.MatchJoinSuccessDialog
import com.example.ui.theme.*

@Composable
fun TournamentsScreen(
    games: List<Game>,
    tournaments: List<Tournament>,
    matches: List<Match>,
    selectedGameId: String?,
    onSelectMatch: (Match) -> Unit,
    onNavigateWallet: (() -> Unit)? = null
) {
    val wallet by EsportsRepository.wallet.collectAsState()
    var activeGameFilter by remember { mutableStateOf(selectedGameId ?: "ALL") }
    var activeTypeFilter by remember { mutableStateOf("ALL") }

    var matchToJoin by remember { mutableStateOf<Match?>(null) }
    var joinSuccessResult by remember { mutableStateOf<JoinResult?>(null) }

    val filteredMatches = matches.filter { match ->
        (activeGameFilter == "ALL" || match.gameId == activeGameFilter) &&
        (activeTypeFilter == "ALL" || match.type.name == activeTypeFilter)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Game Filter Selector Bar
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = activeGameFilter == "ALL",
                            onClick = { activeGameFilter = "ALL" },
                            label = { Text("All Games") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = DarkSurface,
                                labelColor = Color.White
                            )
                        )
                    }
                    items(games) { game ->
                        FilterChip(
                            selected = activeGameFilter == game.id,
                            onClick = { activeGameFilter = game.id },
                            label = { Text(game.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = DarkSurface,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Tournament Format Filter (SOLO / DUO / SQUAD)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL", "SOLO", "DUO", "SQUAD", "TEAM").forEach { type ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (activeTypeFilter == type) ElectricViolet else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (activeTypeFilter == type) CyberCyan else DarkBorder
                            ),
                            modifier = Modifier.weight(1f),
                            onClick = { activeTypeFilter = type }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = type,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "AVAILABLE TOURNAMENTS & MATCHES (${filteredMatches.size})",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (filteredMatches.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No matches found for selected filters",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredMatches) { match ->
                    MatchCard(
                        match = match,
                        onClick = { onSelectMatch(match) },
                        onJoinClick = { matchToJoin = match }
                    )
                }
            }
        }

        // Match Join Confirmation Dialog
        matchToJoin?.let { match ->
            MatchJoinConfirmationDialog(
                match = match,
                wallet = wallet,
                onDismiss = { matchToJoin = null },
                onNavigateWallet = {
                    matchToJoin = null
                    onNavigateWallet?.invoke()
                },
                onJoinSuccess = { result ->
                    matchToJoin = null
                    joinSuccessResult = result
                }
            )
        }

        // Match Join Success Modal
        joinSuccessResult?.let { result ->
            MatchJoinSuccessDialog(
                joinResult = result,
                onDismiss = { joinSuccessResult = null },
                onViewMatch = {
                    val targetMatch = matches.find { it.title == result.matchTitle } ?: filteredMatches.firstOrNull()
                    joinSuccessResult = null
                    if (targetMatch != null) {
                        onSelectMatch(targetMatch)
                    }
                }
            )
        }
    }
}
