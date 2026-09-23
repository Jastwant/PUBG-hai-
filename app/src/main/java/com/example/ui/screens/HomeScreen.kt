package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.data.repository.EsportsRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    games: List<Game>,
    tournaments: List<Tournament>,
    matches: List<Match>,
    announcements: List<Announcement>,
    leaderboard: List<LeaderboardEntry>,
    onSelectGame: (Game) -> Unit,
    onSelectTournament: (Tournament) -> Unit,
    onSelectMatch: (Match) -> Unit,
    onNavigateChallenges: () -> Unit,
    onNavigateWatchEarn: () -> Unit,
    onNavigateShop: () -> Unit,
    onNavigateRefer: () -> Unit,
    onNavigateAdmin: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val filteredGames = games.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    val pinnedMatches = matches.filter { it.isPinned || it.status == MatchStatus.REGISTRATION_OPEN }
    val ongoingMatches = matches.filter { it.status == MatchStatus.ONGOING }
    val wallet by EsportsRepository.wallet.collectAsState()
    var matchToJoin by remember { mutableStateOf<Match?>(null) }
    var joinSuccessResult by remember { mutableStateOf<JoinResult?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
        // Admin Quick Launcher Banner if admin callback provided
        if (onNavigateAdmin != null) {
            item {
                Surface(
                    color = DarkElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onNavigateAdmin() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CyberCyanLight, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("ADMIN CONTROL CENTER", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                Text("Manage tournaments, rooms, payouts & users", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyanLight, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
        // Announcement Banner
        if (announcements.isNotEmpty()) {
            item {
                val topAnnouncement = announcements.first()
                Surface(
                    color = ElectricViolet.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = topAnnouncement.title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search games, tournaments, maps...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Hero Gaming Slider Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E3A8A), Color(0xFF0F172A), DarkBg)
                        )
                    )
                    .border(1.dp, DarkBorderSubtle, RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CyberCyan
                        ) {
                            Text(
                                text = "FEATURED",
                                color = DarkBg,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "PRIZE POOL: ₹50,000",
                            color = CyberCyanLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "BATTLEGROUND\nELITE SERIES",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        lineHeight = 24.sp
                    )
                    Text(
                        text = "Squad Erangel Battle Royale • Solo & Team registrations open",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )
                    Button(
                        onClick = {
                            tournaments.firstOrNull()?.let { onSelectTournament(it) }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("JOIN NOW", color = DarkBg, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
            }
        }

        // Quick Hub Grid (4 Feature Tiles)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickHubTile(
                    title = "1v1 PvP",
                    subtitle = "Challenges",
                    icon = Icons.Default.SportsKabaddi,
                    tint = NeonPink,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateChallenges
                )
                QuickHubTile(
                    title = "Watch & Earn",
                    subtitle = "Free Cash",
                    icon = Icons.Default.PlayCircle,
                    tint = CyberGold,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateWatchEarn
                )
                QuickHubTile(
                    title = "Store & UC",
                    subtitle = "Diamonds",
                    icon = Icons.Default.ShoppingCart,
                    tint = CyberCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateShop
                )
                QuickHubTile(
                    title = "Refer ₹25",
                    subtitle = "Invite Clan",
                    icon = Icons.Default.GroupAdd,
                    tint = EmeraldGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateRefer
                )
            }
        }

        // Popular Esports Games Header
        item {
            SectionHeader(
                title = "FEATURED GAMES",
                subtitle = "Select game to view live tournaments",
                actionText = "View All",
                onAction = {}
            )
        }

        // Games Horizontal Cards
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredGames) { game ->
                    GameCard(game = game, onClick = { onSelectGame(game) })
                }
            }
        }

        // Live / Ongoing Matches Section
        if (ongoingMatches.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "🔴 LIVE MATCHES NOW",
                    subtitle = "Watch ongoing streams or view live rooms",
                    actionText = null
                )
            }
            items(ongoingMatches) { match ->
                MatchCard(
                    match = match,
                    onClick = { onSelectMatch(match) },
                    onJoinClick = { onSelectMatch(match) }
                )
            }
        }

        // Pinned & Upcoming Matches
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "UPCOMING MATCHES",
                subtitle = "Slots filling fast - book before registration closes",
                actionText = "See All",
                onAction = {}
            )
        }

        items(pinnedMatches) { match ->
            MatchCard(
                match = match,
                onClick = { onSelectMatch(match) },
                onJoinClick = { matchToJoin = match }
            )
        }

        // Top 5 Leaderboard Preview
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "🏆 TOP ESPORTS PLAYERS",
                subtitle = "Weekly ranking champions",
                actionText = "Full Board"
            )
        }

        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    leaderboard.take(3).forEachIndexed { index, entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = when (index) {
                                        0 -> CyberGold.copy(alpha = 0.2f)
                                        1 -> Color.LightGray.copy(alpha = 0.2f)
                                        else -> RadiantOrange.copy(alpha = 0.2f)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${entry.rank}",
                                            color = when (index) {
                                                0 -> CyberGold
                                                1 -> Color.White
                                                else -> RadiantOrange
                                            },
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = entry.username,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "IGN: ${entry.inGameName} • ${entry.wins} Wins",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${"%.0f".format(entry.totalEarnings)}",
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${entry.points} pts",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        if (index < 2) {
                            HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))
                        }
                    }
                }
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
                    onNavigateRefer()
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
                    val targetMatch = matches.find { it.title == result.matchTitle } ?: pinnedMatches.firstOrNull()
                    joinSuccessResult = null
                    if (targetMatch != null) {
                        onSelectMatch(targetMatch)
                    }
                }
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        if (actionText != null && onAction != null) {
            Text(
                text = actionText,
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onAction() }
            )
        }
    }
}

@Composable
fun QuickHubTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, tint.copy(alpha = 0.35f)),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun GameCard(
    game: Game,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(84.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(DarkElevated)
                .border(1.dp, DarkBorderSubtle, RoundedCornerShape(18.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            val color = when {
                game.id.contains("bgmi") -> CyberCyan
                game.id.contains("ff") -> RadiantOrange
                game.id.contains("ludo") -> EmeraldGreen
                game.id.contains("codm") -> OceanBlue
                else -> ElectricViolet
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                val icon = when {
                    game.id.contains("bgmi") -> Icons.Default.GpsFixed
                    game.id.contains("ff") -> Icons.Default.LocalFireDepartment
                    game.id.contains("ludo") -> Icons.Default.Casino
                    game.id.contains("codm") -> Icons.Default.Security
                    else -> Icons.Default.SportsEsports
                }
                Icon(icon, contentDescription = null, tint = DarkBg, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = game.name,
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

@Composable
fun MatchCard(
    match: Match,
    onClick: () -> Unit,
    onJoinClick: (() -> Unit)? = null
) {
    val participants by EsportsRepository.participants.collectAsState()
    val currentUser by EsportsRepository.currentUser.collectAsState()
    val myParticipant = participants.find { it.matchId == match.id && it.userId == currentUser?.id }
    val isJoined = myParticipant != null
    val isLive = match.status.isLive
    val isFull = match.playersJoined >= match.maxSlots
    val isCompleted = match.status.isCompleted

    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLive) CrimsonRed.copy(alpha = 0.6f)
            else if (isJoined) EmeraldGreen.copy(alpha = 0.6f)
            else DarkBorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = match.title.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${match.type.name} • ${match.mapName.uppercase()} • 🕐 ${match.startTime}",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (isJoined) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EmeraldGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "🟢 JOINED",
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else if (isLive) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CrimsonRed.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed)
                    ) {
                        Text(
                            text = "🔴 LIVE",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else if (isFull) {
                    StatusBadge(text = "MATCH FULL", color = RadiantOrange)
                } else {
                    StatusBadge(text = "₹${"%.0f".format(match.prizePool)} PRIZE", color = CyberGold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4-Column Stats Box: Prize, Entry, Slots, Per Kill
            Surface(
                color = DarkElevated,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PRIZE POOL", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("₹${"%.0f".format(match.prizePool)}", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ENTRY FEE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (match.entryFee == 0.0) "FREE" else "₹${"%.0f".format(match.entryFee)}",
                            color = CyberCyanLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SLOTS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("${match.playersJoined}/${match.maxSlots}", color = if (isFull) CrimsonRed else Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("PER KILL", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("₹${"%.0f".format(match.perKillReward)}", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slots Progress Bar
            val progress = (match.playersJoined.toFloat() / match.maxSlots.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape),
                color = if (isFull) CrimsonRed else CyberCyan,
                trackColor = DarkElevated
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action / Status Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isJoined) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Your Slot: #${myParticipant?.slotNumber}",
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("VIEW MATCH", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                } else {
                    Text(
                        text = if (isFull) "All slots booked" else "${match.maxSlots - match.playersJoined} slots remaining",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    if (isFull) {
                        Button(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("MATCH FULL", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    } else if (match.status == MatchStatus.REGISTRATION_OPEN) {
                        Button(
                            onClick = { onJoinClick?.invoke() ?: onClick() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("JOIN NOW", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    } else if (isLive) {
                        Button(
                            onClick = onClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("LIVE NOW", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = if (match.status == MatchStatus.UPCOMING) "REGISTRATION CLOSED" else match.status.name,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
