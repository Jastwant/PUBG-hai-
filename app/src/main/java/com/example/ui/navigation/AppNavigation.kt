package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Game
import com.example.data.models.Match
import com.example.data.models.Tournament
import com.example.data.models.UserRole
import com.example.data.repository.EsportsRepository
import com.example.ui.components.EsportsTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    data class SecurityBlock(val action: com.example.data.models.SecurityDecisionAction, val reason: String?) : Screen("security_block")
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Main : Screen("main")
    object MatchDetail : Screen("match_detail")
    object Notifications : Screen("notifications")
    object AdminPortal : Screen("admin_portal")
}

enum class MainTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.SportsEsports),
    TOURNAMENTS("Tournaments", Icons.Default.EmojiEvents),
    MY_MATCHES("My Matches", Icons.Default.Gamepad),
    CHALLENGES("1v1 PvP", Icons.Default.SportsKabaddi),
    WALLET("Wallet", Icons.Default.AccountBalanceWallet),
    LEADERBOARD("Ranking", Icons.Default.Leaderboard),
    SHOP_EARN("Earn & Shop", Icons.Default.ShoppingBag),
    PROFILE("Profile", Icons.Default.Person)
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var selectedMainTab by remember { mutableStateOf(MainTab.HOME) }
    var selectedMatchId by remember { mutableStateOf<String?>(null) }
    var selectedGameIdFilter by remember { mutableStateOf<String?>(null) }

    val appConfig by EsportsRepository.appConfig.collectAsState()
    val currentUser by EsportsRepository.currentUser.collectAsState()
    val wallet by EsportsRepository.wallet.collectAsState()
    val games by EsportsRepository.games.collectAsState()
    val tournaments by EsportsRepository.tournaments.collectAsState()
    val matches by EsportsRepository.matches.collectAsState()
    val announcements by EsportsRepository.announcements.collectAsState()
    val leaderboard by EsportsRepository.leaderboard.collectAsState()
    val notifications by EsportsRepository.notifications.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current

    when (val screen = currentScreen) {
        Screen.Splash -> {
            SplashScreen(
                appConfig = appConfig,
                onSecurityBlocked = { action, reason ->
                    currentScreen = Screen.SecurityBlock(action, reason)
                },
                onNavigateNext = {
                    currentScreen = if (currentUser == null) Screen.Onboarding else Screen.Main
                }
            )
        }

        is Screen.SecurityBlock -> {
            SecurityBlockScreen(
                action = screen.action,
                reason = screen.reason,
                onCheckAgain = {
                    val decision = com.example.security.DeviceSecurityManager.evaluateSecurity(context)
                    if (decision.allowed) {
                        currentScreen = Screen.Splash
                    } else {
                        currentScreen = Screen.SecurityBlock(decision.action, decision.reason)
                    }
                }
            )
        }

        Screen.Onboarding -> {
            OnboardingScreen(
                onFinish = {
                    currentScreen = Screen.Auth
                }
            )
        }

        Screen.Auth -> {
            AuthScreen(
                onAuthSuccess = {
                    currentScreen = Screen.Main
                }
            )
        }

        Screen.MatchDetail -> {
            selectedMatchId?.let { matchId ->
                MatchDetailScreen(
                    matchId = matchId,
                    onBack = { currentScreen = Screen.Main },
                    onNavigateWallet = {
                        selectedMainTab = MainTab.WALLET
                        currentScreen = Screen.Main
                    }
                )
            }
        }

        Screen.Notifications -> {
            NotificationsScreen(
                onBack = { currentScreen = Screen.Main }
            )
        }

        Screen.AdminPortal -> {
            AdminPortalScreen(
                onBack = { currentScreen = Screen.Main }
            )
        }

        Screen.Main -> {
            Scaffold(
                topBar = {
                    EsportsTopBar(
                        title = when (selectedMainTab) {
                            MainTab.HOME -> "APEXZONE"
                            MainTab.TOURNAMENTS -> "TOURNAMENTS"
                            MainTab.MY_MATCHES -> "MY MATCHES"
                            MainTab.CHALLENGES -> "1V1 PVP ARENA"
                            MainTab.WALLET -> "ESPORTS WALLET"
                            MainTab.LEADERBOARD -> "LEADERBOARD"
                            MainTab.SHOP_EARN -> "WATCH & SHOP"
                            MainTab.PROFILE -> "GAMER PROFILE"
                        },
                        subtitle = if (selectedMainTab == MainTab.HOME) "Esports Tournament Arena" else null,
                        walletBalance = wallet.totalBalance,
                        unreadNotifCount = notifications.size,
                        onWalletClick = { selectedMainTab = MainTab.WALLET },
                        onNotifClick = { currentScreen = Screen.Notifications },
                        onAdminClick = { currentScreen = Screen.AdminPortal },
                        isAdmin = currentUser?.role == UserRole.ADMIN
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = DarkBg,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .border(androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle))
                    ) {
                        // Display primary tabs in navigation bar
                        val primaryTabs = listOf(
                            MainTab.HOME,
                            MainTab.TOURNAMENTS,
                            MainTab.MY_MATCHES,
                            MainTab.CHALLENGES,
                            MainTab.WALLET,
                            MainTab.PROFILE
                        )
                        primaryTabs.forEach { tab ->
                            NavigationBarItem(
                                selected = selectedMainTab == tab,
                                onClick = { selectedMainTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title,
                                        tint = if (selectedMainTab == tab) CyberCyanLight else TextMuted
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        fontSize = 9.sp,
                                        fontWeight = if (selectedMainTab == tab) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedMainTab == tab) CyberCyanLight else TextMuted
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = CyberCyan.copy(alpha = 0.12f)
                                )
                            )
                        }
                    }
                },
                containerColor = DarkBg
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedMainTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                games = games,
                                tournaments = tournaments,
                                matches = matches,
                                announcements = announcements,
                                leaderboard = leaderboard,
                                onSelectGame = { game ->
                                    selectedGameIdFilter = game.id
                                    selectedMainTab = MainTab.TOURNAMENTS
                                },
                                onSelectTournament = { tour ->
                                    val firstMatch = matches.find { it.tournamentId == tour.id }
                                    if (firstMatch != null) {
                                        selectedMatchId = firstMatch.id
                                        currentScreen = Screen.MatchDetail
                                    } else {
                                        selectedMainTab = MainTab.TOURNAMENTS
                                    }
                                },
                                onSelectMatch = { match ->
                                    selectedMatchId = match.id
                                    currentScreen = Screen.MatchDetail
                                },
                                onNavigateChallenges = { selectedMainTab = MainTab.CHALLENGES },
                                onNavigateWatchEarn = { selectedMainTab = MainTab.SHOP_EARN },
                                onNavigateShop = { selectedMainTab = MainTab.SHOP_EARN },
                                onNavigateRefer = { selectedMainTab = MainTab.PROFILE },
                                onNavigateAdmin = if (currentUser?.role == UserRole.ADMIN) { { currentScreen = Screen.AdminPortal } } else null
                            )
                        }

                        MainTab.TOURNAMENTS -> {
                            TournamentsScreen(
                                games = games,
                                tournaments = tournaments,
                                matches = matches,
                                selectedGameId = selectedGameIdFilter,
                                onSelectMatch = { match ->
                                    selectedMatchId = match.id
                                    currentScreen = Screen.MatchDetail
                                },
                                onNavigateWallet = { selectedMainTab = MainTab.WALLET }
                            )
                        }

                        MainTab.MY_MATCHES -> {
                            MyMatchesScreen(
                                onSelectMatch = { match ->
                                    selectedMatchId = match.id
                                    currentScreen = Screen.MatchDetail
                                }
                            )
                        }

                        MainTab.CHALLENGES -> {
                            ChallengesScreen(
                                onNavigateWallet = { selectedMainTab = MainTab.WALLET }
                            )
                        }

                        MainTab.WALLET -> {
                            WalletScreen()
                        }

                        MainTab.LEADERBOARD -> {
                            LeaderboardScreen()
                        }

                        MainTab.SHOP_EARN -> {
                            ShopEarnScreen()
                        }

                        MainTab.PROFILE -> {
                            ProfileSettingsScreen(
                                onNavigateAdmin = { currentScreen = Screen.AdminPortal },
                                onLogout = {
                                    EsportsRepository.logout()
                                    currentScreen = Screen.Auth
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
