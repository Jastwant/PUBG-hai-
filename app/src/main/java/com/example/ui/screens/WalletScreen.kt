package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.data.repository.EsportsRepository
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen() {
    val context = LocalContext.current
    val wallet by EsportsRepository.wallet.collectAsState()
    val transactions by EsportsRepository.transactions.collectAsState()
    val appConfig by EsportsRepository.appConfig.collectAsState()
    val currentUser by EsportsRepository.currentUser.collectAsState()

    var showAddMoneyDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showReconciliationDialog by remember { mutableStateOf(false) }
    var reconciliationReport by remember { mutableStateOf<ReconciliationReport?>(null) }
    var selectedTxnFilter by remember { mutableStateOf("ALL") }

    // Add Money state
    var addAmountText by remember { mutableStateOf("100") }
    var selectedGateway by remember { mutableStateOf("Razorpay UPI") }
    var isSimulatingGateway by remember { mutableStateOf(false) }
    var gatewayStep by remember { mutableStateOf(1) } // 1: Select, 2: Simulated Webhook Callback
    var createdOrder by remember { mutableStateOf<PaymentOrder?>(null) }

    // Withdraw state
    var withdrawAmountText by remember { mutableStateOf("200") }
    var upiOrBankText by remember { mutableStateOf("apex@upi") }
    var ifscText by remember { mutableStateOf("HDFC0001234") }
    var withdrawMethod by remember { mutableStateOf("UPI") }

    val filteredTxns = transactions.filter {
        when (selectedTxnFilter) {
            "ALL" -> true
            "DEPOSIT" -> it.type == TransactionType.DEPOSIT
            "WINNING" -> it.type == TransactionType.WINNING || it.type == TransactionType.PRIZE_WINNING
            "ENTRY_FEE" -> it.type == TransactionType.ENTRY_FEE || it.type == TransactionType.TOURNAMENT_ENTRY
            "WITHDRAWAL" -> it.type == TransactionType.WITHDRAWAL
            "BONUS" -> it.type == TransactionType.BONUS || it.type == TransactionType.REFERRAL
            "REFUND" -> it.type == TransactionType.REFUND
            "ADJUSTMENT" -> it.type == TransactionType.ADJUSTMENT
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Master Wallet Card
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        )
                        .padding(20.dp)
                ) {
                    // Top Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL PORTFOLIO VALUE",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "₹${"%.2f".format(wallet.totalBalance)}",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Surface(
                            color = CyberGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Stars, contentDescription = null, tint = CyberGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${wallet.points} Pts", color = CyberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Available to Join Match indicator bar
                    Surface(
                        color = DarkElevated,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Available to Play / Join:",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "₹${"%.2f".format(wallet.availableBalance)}",
                                color = EmeraldGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DarkBorder.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // 4-Quadrant Balance Breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BalanceSubBox(
                            modifier = Modifier.weight(1f),
                            label = "DEPOSIT",
                            amount = wallet.depositBalance,
                            note = "Entry Pool",
                            color = CyberCyan,
                            icon = Icons.Default.AddCard
                        )
                        BalanceSubBox(
                            modifier = Modifier.weight(1f),
                            label = "WINNINGS",
                            amount = wallet.winningBalance,
                            note = "Withdrawable",
                            color = CyberGold,
                            icon = Icons.Default.EmojiEvents
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BalanceSubBox(
                            modifier = Modifier.weight(1f),
                            label = "LOCKED ESCROW",
                            amount = wallet.lockedBalance,
                            note = if (wallet.lockedBalance > 0) "Pending Hold" else "No active hold",
                            color = if (wallet.lockedBalance > 0) Color(0xFFFFA726) else TextMuted,
                            icon = Icons.Default.Lock
                        )
                        BalanceSubBox(
                            modifier = Modifier.weight(1f),
                            label = "PROMO BONUS",
                            amount = wallet.bonusBalance,
                            note = "Up to 20% / match",
                            color = NeonPink,
                            icon = Icons.Default.CardGiftcard
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action Buttons (Add Money & Withdraw)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                gatewayStep = 1
                                isSimulatingGateway = false
                                createdOrder = null
                                showAddMoneyDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ADD MONEY", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }

                        Button(
                            onClick = { showWithdrawDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkElevated),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = CyberGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WITHDRAW", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Audit & Reconciliation Trigger
                    OutlinedButton(
                        onClick = {
                            reconciliationReport = EsportsRepository.runWalletReconciliation()
                            showReconciliationDialog = true
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Daily Money Ledger Audit & Reconciliation",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // KYC Status Banner (if not verified)
        val isKyc = currentUser?.isKycVerified == true
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isKyc) EmeraldGreen.copy(alpha = 0.12f) else CrimsonRed.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isKyc) EmeraldGreen.copy(alpha = 0.3f) else CrimsonRed.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isKyc) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isKyc) EmeraldGreen else CrimsonRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isKyc) "KYC Verified (Instant Withdrawals Enabled)" else "KYC Required for Withdrawals",
                                color = if (isKyc) EmeraldGreen else CrimsonRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isKyc) "Government ID validated for ${currentUser?.fullName ?: "User"}" else "Mandatory RBI/Gaming compliance before money payout",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    if (!isKyc) {
                        TextButton(
                            onClick = {
                                currentUser?.id?.let {
                                    EsportsRepository.adminToggleUserKyc(it)
                                    Toast.makeText(context, "KYC verified instantly for testing!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("Verify Now", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Transactions Header & Filters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TRANSACTION LEDGER",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "${filteredTxns.size} Entries",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Ledger Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("ALL", "DEPOSIT", "WINNING", "ENTRY_FEE", "WITHDRAWAL", "BONUS", "REFUND", "ADJUSTMENT")) { filter ->
                    val isSelected = selectedTxnFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTxnFilter = filter },
                        label = {
                            Text(
                                text = filter.replace("_", " "),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CyberCyan else DarkBorder
                        )
                    )
                }
            }
        }

        // Transactions List
        if (filteredTxns.isEmpty()) {
            item {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Inbox, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No ledger transactions found", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Transactions will appear here as you deposit, play, and win.", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(filteredTxns) { txn ->
                val isCredit = txn.type == TransactionType.DEPOSIT ||
                        txn.type == TransactionType.WINNING ||
                        txn.type == TransactionType.PRIZE_WINNING ||
                        txn.type == TransactionType.BONUS ||
                        txn.type == TransactionType.REFERRAL ||
                        txn.type == TransactionType.REFUND ||
                        (txn.type == TransactionType.ADJUSTMENT && txn.newBalance >= txn.previousBalance)

                val dateStr = remember(txn.timestamp) {
                    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(txn.timestamp))
                }

                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isCredit) EmeraldGreen.copy(alpha = 0.15f) else CrimsonRed.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = if (isCredit) EmeraldGreen else CrimsonRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = txn.description,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "$dateStr • ${txn.paymentMethod ?: txn.type.name}",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${if (isCredit) "+" else "-"}₹${"%.2f".format(txn.amount)}",
                                    color = if (isCredit) EmeraldGreen else CrimsonRed,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                val badgeColor = when (txn.status) {
                                    TransactionStatus.SUCCESS -> EmeraldGreen
                                    TransactionStatus.PENDING -> CyberGold
                                    TransactionStatus.FAILED -> CrimsonRed
                                    TransactionStatus.REVERSED, TransactionStatus.REJECTED, TransactionStatus.CANCELLED -> Color(0xFFA855F7)
                                }
                                StatusBadge(text = txn.status.name, color = badgeColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(6.dp))

                        // Ledger details row: Reference ID, Idempotency key, and Balance change
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("RefId", txn.referenceId))
                                    Toast.makeText(context, "Copied Ref ID: ${txn.referenceId}", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text(
                                    text = "Ref: ${txn.referenceId}",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(11.dp))
                            }

                            Text(
                                text = "₹${"%.0f".format(txn.previousBalance)} → ₹${"%.0f".format(txn.newBalance)}",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }

    // --- ADD MONEY / PAYMENT GATEWAY SIMULATION DIALOG ---
    if (showAddMoneyDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isSimulatingGateway) showAddMoneyDialog = false
            },
            containerColor = DarkCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (gatewayStep == 1) "Add Money via Gateway" else "Gateway Verification",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (gatewayStep == 1) {
                        Text("Select Quick Amount (₹):", color = TextSecondary, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("50", "100", "200", "500").forEach { amt ->
                                val isChosen = addAmountText == amt
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isChosen) CyberCyan else DarkElevated,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isChosen) CyberCyan else DarkBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { addAmountText = amt }
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "₹$amt",
                                            color = if (isChosen) Color.Black else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = addAmountText,
                            onValueChange = { addAmountText = it },
                            label = { Text("Enter Custom Amount (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Select Payment Gateway / Method:", color = TextSecondary, fontSize = 12.sp)
                        listOf("Razorpay UPI", "Paytm / Net Banking", "PhonePe UPI", "Cashfree Direct").forEach { gw ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedGateway == gw) DarkElevated else Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedGateway == gw) CyberCyan.copy(alpha = 0.5f) else Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedGateway = gw }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedGateway == gw,
                                        onClick = { selectedGateway = gw },
                                        colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(gw, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Surface(
                            color = EmeraldGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "256-bit Encrypted. Double-credit protection enabled.",
                                    color = EmeraldGreen,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    } else {
                        // Gateway step 2: Webhook Simulation
                        Surface(
                            color = DarkElevated,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ORDER CREATED", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Order ID: ${createdOrder?.orderId}", color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                Text("Amount: ₹${createdOrder?.amount}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                                Text("Gateway: ${createdOrder?.gateway}", color = TextSecondary, fontSize = 12.sp)
                                Text("Status: PENDING WEBHOOK", color = CyberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = "To test real-world gateway resilience, choose whether this simulated payment succeeds or gets declined by the bank.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                if (gatewayStep == 1) {
                    Button(
                        onClick = {
                            val amt = addAmountText.toDoubleOrNull() ?: 100.0
                            val orderRes = EsportsRepository.createDepositOrder(amt, selectedGateway)
                            if (orderRes.isSuccess) {
                                createdOrder = orderRes.getOrNull()
                                gatewayStep = 2
                            } else {
                                Toast.makeText(context, orderRes.exceptionOrNull()?.message ?: "Order failed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("CREATE PAYMENT ORDER", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                createdOrder?.let { ord ->
                                    val res = EsportsRepository.verifyPaymentWebhook(
                                        orderId = ord.orderId,
                                        gatewayPaymentId = "PAY_${UUID.randomUUID().toString().take(8).uppercase()}",
                                        isSuccessful = true
                                    )
                                    showAddMoneyDialog = false
                                    Toast.makeText(context, res.getOrNull() ?: "Payment Credited!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text("SIMULATE SUCCESS", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }
            },
            dismissButton = {
                if (gatewayStep == 1) {
                    TextButton(onClick = { showAddMoneyDialog = false }) { Text("Cancel", color = TextSecondary) }
                } else {
                    Button(
                        onClick = {
                            createdOrder?.let { ord ->
                                EsportsRepository.verifyPaymentWebhook(
                                    orderId = ord.orderId,
                                    gatewayPaymentId = "PAY_FAILED",
                                    isSuccessful = false
                                )
                                showAddMoneyDialog = false
                                Toast.makeText(context, "Payment declined simulation processed.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Text("SIMULATE FAIL", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }

    // --- WITHDRAW MONEY DIALOG ---
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            containerColor = DarkCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = CyberGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Withdraw Winnings", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Withdrawable Balance Notice
                    Surface(
                        color = DarkElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Withdrawable Winnings:", color = TextSecondary, fontSize = 11.sp)
                                Text("₹${"%.2f".format(wallet.winningBalance)}", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                            Text(
                                text = "Only tournament & challenge winnings can be cashed out.",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Method selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { withdrawMethod = "UPI" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (withdrawMethod == "UPI") CyberGold else DarkElevated
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("UPI ID", color = if (withdrawMethod == "UPI") Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { withdrawMethod = "BANK" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (withdrawMethod == "BANK") CyberGold else DarkElevated
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Bank Account", color = if (withdrawMethod == "BANK") Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        label = { Text("Withdraw Amount (₹)") },
                        supportingText = { Text("Min: ₹${appConfig.minWithdrawalAmount} • Max: ₹10,000/day", color = TextMuted, fontSize = 10.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGold,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = upiOrBankText,
                        onValueChange = { upiOrBankText = it },
                        label = { Text(if (withdrawMethod == "UPI") "UPI ID (e.g. mobile@upi)" else "Bank Account Number") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGold,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (withdrawMethod == "BANK") {
                        OutlinedTextField(
                            value = ifscText,
                            onValueChange = { ifscText = it },
                            label = { Text("Bank IFSC Code (e.g. HDFC0001234)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberGold,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = CyberGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Payouts process in ~10-15 mins via IMPS / UPI Route.",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawAmountText.toDoubleOrNull() ?: 0.0
                        val targetDetails = if (withdrawMethod == "BANK") "$upiOrBankText (IFSC: $ifscText)" else upiOrBankText
                        val res = EsportsRepository.requestWithdrawal(amt, withdrawMethod, targetDetails)
                        if (res.isSuccess) {
                            showWithdrawDialog = false
                            Toast.makeText(context, res.getOrNull() ?: "Submitted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, res.exceptionOrNull()?.message ?: "Failed", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGold)
                ) {
                    Text("CONFIRM WITHDRAWAL", color = Color.Black, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }

    // --- RECONCILIATION & AUDIT REPORT DIALOG ---
    if (showReconciliationDialog && reconciliationReport != null) {
        val rep = reconciliationReport!!
        AlertDialog(
            onDismissRequest = { showReconciliationDialog = false },
            containerColor = DarkCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (rep.isBalanced) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (rep.isBalanced) EmeraldGreen else CrimsonRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (rep.isBalanced) "Ledger Audit: BALANCED" else "Ledger Audit: MISMATCH",
                        color = if (rep.isBalanced) EmeraldGreen else CrimsonRed,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = rep.alertMessage,
                        color = if (rep.isBalanced) EmeraldGreen else CrimsonRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = DarkElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ReconcileRow("Active Wallet Liabilities:", "₹${"%.2f".format(rep.totalWalletBalances)}")
                            ReconcileRow("Total Credits (Money In):", "+₹${"%.2f".format(rep.totalCredits)}", EmeraldGreen)
                            ReconcileRow("Total Debits (Money Out):", "-₹${"%.2f".format(rep.totalDebits)}", CrimsonRed)
                            ReconcileRow("Expected Net Ledger:", "₹${"%.2f".format(rep.netLedgerBalance)}")
                            HorizontalDivider(color = DarkBorder)
                            ReconcileRow("Discrepancy (Delta):", "₹${"%.2f".format(rep.discrepancy)}", if (rep.isBalanced) EmeraldGreen else CrimsonRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Breakdown of Validated Transactions (${rep.transactionCount} entries):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("• Total User Deposits: ₹${"%.2f".format(rep.totalDeposits)}", color = TextSecondary, fontSize = 11.sp)
                            Text("• Total Match Entry Fees: ₹${"%.2f".format(rep.totalEntryFees)}", color = TextSecondary, fontSize = 11.sp)
                            Text("• Total Prizes Distributed: ₹${"%.2f".format(rep.totalPrizes)}", color = TextSecondary, fontSize = 11.sp)
                            Text("• Total Withdrawals Processed: ₹${"%.2f".format(rep.totalWithdrawals)}", color = TextSecondary, fontSize = 11.sp)
                            Text("• Total Refunds Processed: ₹${"%.2f".format(rep.totalRefunds)}", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showReconciliationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Text("CLOSE AUDIT", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun BalanceSubBox(
    modifier: Modifier = Modifier,
    label: String,
    amount: Double,
    note: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Black)
                Icon(icon, contentDescription = null, tint = color.copy(alpha = 0.8f), modifier = Modifier.size(13.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("₹${"%.2f".format(amount)}", color = color, fontSize = 16.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(note, color = TextSecondary, fontSize = 9.sp, maxLines = 1)
        }
    }
}

@Composable
fun ReconcileRow(label: String, value: String, valueColor: Color = Color.White) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Text(value, color = valueColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
