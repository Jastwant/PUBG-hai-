package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.data.models.*
import com.example.data.repository.EsportsRepository
import com.example.ui.components.GameIconResolver
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ChallengesScreen(
    onNavigateWallet: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by EsportsRepository.currentUser.collectAsState()
    val challenges by EsportsRepository.challenges.collectAsState()
    val challengeMessages by EsportsRepository.challengeMessages.collectAsState()
    val games by EsportsRepository.games.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var activeChallengeForLobby by remember { mutableStateOf<PvPChallenge?>(null) }

    // Create Form state
    var selectedGameId by remember { mutableStateOf("game_bgmi") }
    var entryAmountText by remember { mutableStateOf("50") }
    var creatorIgn by remember { mutableStateOf(currentUser?.username ?: "") }

    // Chat in Lobby state
    var messageInput by remember { mutableStateOf("") }

    Scaffold(
        containerColor = DarkBg,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = CyberCyan,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("CREATE 1V1 CHALLENGE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    ) { innerPadding ->
        if (activeChallengeForLobby != null) {
            val chal = activeChallengeForLobby!!
            val msgs = challengeMessages[chal.id] ?: emptyList()

            // Challenge Lobby Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(DarkBg)
            ) {
                // Header
                Surface(color = DarkSurface, tonalElevation = 4.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { activeChallengeForLobby = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("1v1 Match Lobby: ${chal.gameName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Prize Pool: ₹${"%.0f".format(chal.prizeAmount)}", color = CyberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        StatusBadge(text = chal.status.name, color = CyberCyan)
                    }
                }

                // Opponents Card
                Surface(
                    color = DarkElevated,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(chal.creatorUsername, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("IGN: ${chal.creatorInGameName}", color = CyberCyan, fontSize = 11.sp)
                            StatusBadge("HOST", ElectricViolet)
                        }
                        Text("VS", color = RadiantOrange, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(chal.opponentUsername.ifBlank { "Waiting..." }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(if (chal.opponentInGameName.isNotBlank()) "IGN: ${chal.opponentInGameName}" else "Open Slot", color = TextSecondary, fontSize = 11.sp)
                            StatusBadge("CONTENDER", NeonPink)
                        }
                    }
                }

                // Room Code Box
                Surface(
                    color = DarkSurface,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("CUSTOM ROOM CODE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = chal.roomCode.ifBlank { "Awaiting host to create in-game custom room" },
                                color = if (chal.roomCode.isNotBlank()) CyberCyan else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Button(
                            onClick = {
                                EsportsRepository.submitChallengeProof(chal.id, chal.creatorId == currentUser?.id, "proof_screenshot.png")
                                Toast.makeText(context, "Screenshot Proof Submitted for Admin Verification!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("UPLOAD PROOF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "LOBBY CHAT & COORDINATION",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (msgs.isEmpty()) {
                        item {
                            Text(
                                text = "Lobby opened. Greet your opponent and exchange custom room password.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }
                    items(msgs) { msg ->
                        val isMe = msg.senderId == currentUser?.id
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                color = if (isMe) ElectricViolet else DarkSurface,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = msg.senderName,
                                        color = if (isMe) CyberCyan else CyberGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(text = msg.message, color = Color.White, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Chat Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .background(DarkSurface)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Type message or room code...", color = TextMuted, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank()) {
                                EsportsRepository.sendChallengeMessage(chal.id, messageInput)
                                messageInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = CyberCyan)
                    }
                }
            }
        } else {
            // Challenges Listing Screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(DarkBg),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "1V1 PVP CHALLENGE ARENA",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Bet your skills head-to-head. Winner takes 90% prize automatically.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                    )
                }

                if (challenges.isEmpty()) {
                    item {
                        Text("No active challenges right now. Click + to create one!", color = TextMuted, fontSize = 13.sp)
                    }
                } else {
                    items(challenges) { chal ->
                        val isCreator = chal.creatorId == currentUser?.id
                        val isOpponent = chal.opponentId == currentUser?.id

                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (chal.status == ChallengeStatus.ACCEPTED || isCreator || isOpponent) {
                                        activeChallengeForLobby = chal
                                    }
                                }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        GameIconResolver(gameId = chal.gameId, modifier = Modifier.size(28.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(chal.gameName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    StatusBadge(
                                        text = chal.status.name,
                                        color = if (chal.status == ChallengeStatus.OPEN) CyberCyan else EmeraldGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkElevated)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("HOST: ${chal.creatorUsername}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Entry Bet: ₹${"%.0f".format(chal.entryAmount)}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("PRIZE: ₹${"%.0f".format(chal.prizeAmount)}", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                if (chal.status == ChallengeStatus.OPEN && !isCreator) {
                                    Button(
                                        onClick = {
                                            val res = EsportsRepository.acceptChallenge(chal.id, currentUser?.username ?: "Player")
                                            if (res.isSuccess) {
                                                activeChallengeForLobby = chal.copy(status = ChallengeStatus.ACCEPTED)
                                                Toast.makeText(context, "Challenge Accepted! Entering lobby...", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, res.exceptionOrNull()?.message ?: "Error", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                    ) {
                                        Text("ACCEPT CHALLENGE (₹${"%.0f".format(chal.entryAmount)})", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { activeChallengeForLobby = chal },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(40.dp)
                                    ) {
                                        Text("ENTER LOBBY & CHAT", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Create Challenge Dialog
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                containerColor = DarkCard,
                title = { Text("Create 1v1 PvP Challenge", color = Color.White, fontWeight = FontWeight.Black) },
                text = {
                    Column {
                        Text("Select Game:", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            games.take(3).forEach { g ->
                                FilterChip(
                                    selected = selectedGameId == g.id,
                                    onClick = { selectedGameId = g.id },
                                    label = { Text(g.name.take(8)) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = entryAmountText,
                            onValueChange = { entryAmountText = it },
                            label = { Text("Entry Bet Amount (₹)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = creatorIgn,
                            onValueChange = { creatorIgn = it },
                            label = { Text("Your In-Game Name (IGN)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amt = entryAmountText.toDoubleOrNull() ?: 50.0
                            val selectedGame = games.find { it.id == selectedGameId }
                            val res = EsportsRepository.createChallenge(
                                selectedGameId,
                                "${selectedGame?.name ?: "Game"} 1v1 Match",
                                amt,
                                creatorIgn
                            )
                            if (res.isSuccess) {
                                showCreateDialog = false
                                Toast.makeText(context, "Challenge created! Waiting for contenders.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, res.exceptionOrNull()?.message ?: "Error creating", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("POST CHALLENGE", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) { Text("Cancel", color = TextSecondary) }
                }
            )
        }
    }
}
