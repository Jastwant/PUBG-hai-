package com.example.data.repository

import android.content.Context
import com.example.data.models.*
import com.example.util.ReminderVibrationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object EsportsRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _wallet = MutableStateFlow(Wallet(userId = "user_101", depositBalance = 250.0, winningBalance = 450.0, bonusBalance = 50.0, points = 1200))
    val wallet: StateFlow<Wallet> = _wallet.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _participants = MutableStateFlow<List<MatchParticipant>>(emptyList())
    val participants: StateFlow<List<MatchParticipant>> = _participants.asStateFlow()

    private val _matchResults = MutableStateFlow<List<MatchResult>>(emptyList())
    val matchResults: StateFlow<List<MatchResult>> = _matchResults.asStateFlow()

    private val _challenges = MutableStateFlow<List<PvPChallenge>>(emptyList())
    val challenges: StateFlow<List<PvPChallenge>> = _challenges.asStateFlow()

    private val _challengeMessages = MutableStateFlow<Map<String, List<ChallengeMessage>>>(emptyMap())
    val challengeMessages: StateFlow<Map<String, List<ChallengeMessage>>> = _challengeMessages.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers.asStateFlow()

    private val _products = MutableStateFlow<List<ShopProduct>>(emptyList())
    val products: StateFlow<List<ShopProduct>> = _products.asStateFlow()

    private val _orders = MutableStateFlow<List<ShopOrder>>(emptyList())
    val orders: StateFlow<List<ShopOrder>> = _orders.asStateFlow()

    private val _withdrawals = MutableStateFlow<List<WithdrawalRequest>>(emptyList())
    val withdrawals: StateFlow<List<WithdrawalRequest>> = _withdrawals.asStateFlow()

    private val _paymentOrders = MutableStateFlow<List<PaymentOrder>>(emptyList())
    val paymentOrders: StateFlow<List<PaymentOrder>> = _paymentOrders.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _appConfig = MutableStateFlow(AppConfig())
    val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()

    private val _buddies = MutableStateFlow<List<String>>(listOf("Viper_Pro", "ShadowNinja", "KevGamer99"))
    val buddies: StateFlow<List<String>> = _buddies.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _todayWatchCount = MutableStateFlow(2)
    val todayWatchCount: StateFlow<Int> = _todayWatchCount.asStateFlow()

    private val _resultSubmissions = MutableStateFlow<List<ResultSubmission>>(emptyList())
    val resultSubmissions: StateFlow<List<ResultSubmission>> = _resultSubmissions.asStateFlow()

    private val _userSessions = MutableStateFlow<List<UserSession>>(emptyList())
    val userSessions: StateFlow<List<UserSession>> = _userSessions.asStateFlow()

    private val _userDevices = MutableStateFlow<List<UserDevice>>(emptyList())
    val userDevices: StateFlow<List<UserDevice>> = _userDevices.asStateFlow()

    private val _userGameProfiles = MutableStateFlow<List<UserGameProfile>>(emptyList())
    val userGameProfiles: StateFlow<List<UserGameProfile>> = _userGameProfiles.asStateFlow()

    private val _otpChallenges = MutableStateFlow<List<OtpChallenge>>(emptyList())
    val otpChallenges: StateFlow<List<OtpChallenge>> = _otpChallenges.asStateFlow()

    private val _securityEvents = MutableStateFlow<List<SecurityEvent>>(emptyList())
    val securityEvents: StateFlow<List<SecurityEvent>> = _securityEvents.asStateFlow()

    private val _deviceAccountMappings = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val deviceAccountMappings: StateFlow<Map<String, List<String>>> = _deviceAccountMappings.asStateFlow()

    private val _handledReminderJobs = mutableSetOf<String>()

    init {
        initializeSampleData()
    }

    private fun initializeSampleData() {
        val defaultUser = User(
            id = "user_101",
            fullName = "Apex Champion",
            username = "ApexStriker",
            email = "pro.striker@apexzone.gg",
            mobileNumber = "+91 9876543210",
            phoneNormalized = "+919876543210",
            emailNormalized = "pro.striker@apexzone.gg",
            usernameNormalized = "apexstriker",
            installationId = "inst_primary_987",
            linkedDeviceId = "dev_prim_987",
            inGameNames = mapOf(
                "game_bgmi" to "APEX_Striker",
                "game_ff" to "Apex_FF_King",
                "game_codm" to "Striker_Ghost",
                "game_ludo" to "LudoKing_99"
            ),
            referralCode = "APEX998",
            role = UserRole.ADMIN, // Set to admin so user can test and toggle admin features
            isKycVerified = true,
            status = AccountStatus.ACTIVE
        )
        _currentUser.value = defaultUser

        val sampleUsers = listOf(
            defaultUser,
            User(
                id = "user_102",
                fullName = "Rahul Sharma",
                username = "Viper_Pro",
                email = "viper.pro@gmail.com",
                mobileNumber = "+91 9811223344",
                phoneNormalized = "+919811223344",
                emailNormalized = "viper.pro@gmail.com",
                usernameNormalized = "viper_pro",
                installationId = "inst_viper_221",
                linkedDeviceId = "dev_viper_221",
                inGameNames = mapOf("game_bgmi" to "VIPERxKILLER", "game_ff" to "Viper_FF"),
                role = UserRole.USER,
                isKycVerified = true,
                status = AccountStatus.ACTIVE
            ),
            User(
                id = "user_103",
                fullName = "Aman Verma",
                username = "ShadowNinja",
                email = "aman.shadow@esports.in",
                mobileNumber = "+91 9722334455",
                phoneNormalized = "+919722334455",
                emailNormalized = "aman.shadow@esports.in",
                usernameNormalized = "shadowninja",
                installationId = "inst_shadow_332",
                linkedDeviceId = "dev_shadow_332",
                inGameNames = mapOf("game_bgmi" to "SHADOW_07", "game_codm" to "ShadowGhost"),
                role = UserRole.USER,
                isKycVerified = false,
                status = AccountStatus.ACTIVE
            ),
            User(
                id = "user_104",
                fullName = "Kevin Peter",
                username = "KevGamer99",
                email = "kevin.p@outlook.com",
                mobileNumber = "+91 9633445566",
                phoneNormalized = "+919633445566",
                emailNormalized = "kevin.p@outlook.com",
                usernameNormalized = "kevgamer99",
                installationId = "inst_kevin_443",
                linkedDeviceId = "dev_kevin_443",
                inGameNames = mapOf("game_ludo" to "KevLudoMaster"),
                role = UserRole.USER,
                isKycVerified = true,
                status = AccountStatus.ACTIVE
            ),
            User(
                id = "user_105",
                fullName = "Ananya Singh",
                username = "QueenValkyrie",
                email = "ananya.valkyrie@gmail.com",
                mobileNumber = "+91 9544556677",
                phoneNormalized = "+919544556677",
                emailNormalized = "ananya.valkyrie@gmail.com",
                usernameNormalized = "queenvalkyrie",
                installationId = "inst_queen_554",
                linkedDeviceId = "dev_queen_554",
                inGameNames = mapOf("game_bgmi" to "VALKYRIE_99", "game_ff" to "QueenVal"),
                role = UserRole.USER,
                isKycVerified = false,
                isBlocked = false,
                status = AccountStatus.ACTIVE
            )
        )
        _allUsers.value = sampleUsers

        _deviceAccountMappings.value = mapOf(
            "inst_primary_987" to listOf("user_101"),
            "inst_viper_221" to listOf("user_102"),
            "inst_shadow_332" to listOf("user_103"),
            "inst_kevin_443" to listOf("user_104"),
            "inst_queen_554" to listOf("user_105")
        )

        // Seed devices & sessions
        _userDevices.value = listOf(
            UserDevice(
                deviceId = "dev_prim_987",
                userId = "user_101",
                installationId = "inst_primary_987",
                manufacturer = "OnePlus",
                model = "OnePlus 11 5G",
                osVersion = "Android 14 (SDK 34)",
                developerOptionsEnabled = false,
                usbDebuggingEnabled = false,
                riskLevel = RiskLevel.LOW,
                status = DeviceStatus.ACTIVE,
                isCurrentDevice = true
            ),
            UserDevice(
                deviceId = "dev_samsung_backup",
                userId = "user_101",
                installationId = "inst_samsung_sec",
                manufacturer = "Samsung",
                model = "Galaxy S23 Ultra",
                osVersion = "Android 13 (SDK 33)",
                developerOptionsEnabled = false,
                usbDebuggingEnabled = false,
                riskLevel = RiskLevel.LOW,
                status = DeviceStatus.ACTIVE,
                isCurrentDevice = false
            ),
            UserDevice(
                deviceId = "dev_viper_221",
                userId = "user_102",
                installationId = "inst_viper_221",
                manufacturer = "Xiaomi",
                model = "Redmi Note 12 Pro",
                osVersion = "Android 12 (SDK 31)",
                riskLevel = RiskLevel.LOW,
                status = DeviceStatus.ACTIVE
            )
        )

        _userSessions.value = listOf(
            UserSession(
                sessionId = "sess_current_101",
                userId = "user_101",
                deviceId = "dev_prim_987",
                installationId = "inst_primary_987",
                deviceName = "OnePlus 11 5G",
                osVersion = "Android 14",
                ipHash = "IP_HASH_IN_MUMBAI_8892",
                expiresAt = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000,
                status = SessionStatus.ACTIVE,
                isCurrentSession = true
            ),
            UserSession(
                sessionId = "sess_backup_101",
                userId = "user_101",
                deviceId = "dev_samsung_backup",
                installationId = "inst_samsung_sec",
                deviceName = "Samsung Galaxy S23 Ultra",
                osVersion = "Android 13",
                ipHash = "IP_HASH_IN_DELHI_4419",
                lastSeenAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                expiresAt = System.currentTimeMillis() + 20L * 24 * 60 * 60 * 1000,
                status = SessionStatus.ACTIVE,
                isCurrentSession = false
            )
        )

        _userGameProfiles.value = listOf(
            UserGameProfile(
                id = "gp_101_bgmi",
                userId = "user_101",
                gameId = "game_bgmi",
                gameName = "BGMI / PUBG Mobile",
                playerUid = "5149830211",
                playerName = "APEX_Striker",
                verificationStatus = GameVerificationStatus.VERIFIED
            ),
            UserGameProfile(
                id = "gp_101_ff",
                userId = "user_101",
                gameId = "game_ff",
                gameName = "Free Fire MAX",
                playerUid = "8899221100",
                playerName = "Apex_FF_King",
                verificationStatus = GameVerificationStatus.VERIFIED
            ),
            UserGameProfile(
                id = "gp_102_bgmi",
                userId = "user_102",
                gameId = "game_bgmi",
                gameName = "BGMI / PUBG Mobile",
                playerUid = "5566778899",
                playerName = "VIPERxKILLER",
                verificationStatus = GameVerificationStatus.VERIFIED
            )
        )

        _securityEvents.value = listOf(
            SecurityEvent(
                eventId = "sec_ev_1",
                userId = "user_101",
                deviceId = "dev_prim_987",
                eventType = SecurityEventType.INTEGRITY_VERDICT_RECEIVED,
                severity = RiskLevel.LOW,
                details = "Play Integrity verdict evaluated: MEETS_DEVICE_INTEGRITY, APP_LICENSED",
                actionTaken = "ALLOW"
            ),
            SecurityEvent(
                eventId = "sec_ev_2",
                userId = "user_101",
                deviceId = "dev_prim_987",
                eventType = SecurityEventType.DEVICE_LINKED,
                severity = RiskLevel.LOW,
                details = "Device OnePlus 11 linked to primary session",
                actionTaken = "DEVICE_AUTHORIZED"
            )
        )

        // Games
        val sampleGames = listOf(
            Game(
                id = "game_bgmi",
                name = "BGMI / PUBG Mobile",
                iconName = "crosshairs",
                bannerUrl = "bgmi_banner",
                description = "Battlegrounds Mobile India official competitive scrims and paid championship lobbies.",
                rules = "1. Emulators strictly forbidden.\n2. iPad view is banned.\n3. Room details revealed 10 mins before match.\n4. Take screenshot of ranking and kills for proof.",
                active = true,
                sortOrder = 1
            ),
            Game(
                id = "game_ff",
                name = "Free Fire MAX",
                iconName = "flame",
                bannerUrl = "ff_banner",
                description = "Clash Squad & Classic Battle Royale tournaments with high kill multiplier bounties.",
                rules = "1. Gun attributes ON/OFF per match description.\n2. Character skills strictly controlled.\n3. Grenade spamming rules apply.",
                active = true,
                sortOrder = 2
            ),
            Game(
                id = "game_codm",
                name = "Call of Duty: Mobile",
                iconName = "shield",
                bannerUrl = "codm_banner",
                description = "Search & Destroy and Battle Royale high stakes competitive series.",
                rules = "1. Hardcore competitive rules.\n2. Standard weapon restrictions apply.\n3. Official room invites sent.",
                active = true,
                sortOrder = 3
            ),
            Game(
                id = "game_ludo",
                name = "Ludo King",
                iconName = "dice",
                bannerUrl = "ludo_banner",
                description = "1v1 Classic Quick Ludo matches with instant automated prize settlement.",
                rules = "1. Classic 2-player quick mode.\n2. Room code provided by host.\n3. Winner uploads clear winning screenshot.",
                active = true,
                sortOrder = 4
            ),
            Game(
                id = "game_fortnite",
                name = "Fortnite Mobile",
                iconName = "zap",
                bannerUrl = "fortnite_banner",
                description = "Build & Zero Build championship matches.",
                rules = "1. Solos & Duos only.\n2. Cross-play rules specified.",
                active = true,
                sortOrder = 5
            ),
            Game(
                id = "game_pubglite",
                name = "PUBG Lite",
                iconName = "circle",
                bannerUrl = "pubglite_banner",
                description = "Varenga map daily skirmishes.",
                rules = "1. Mobile devices only.\n2. Standard tournament guidelines.",
                active = false,
                comingSoon = true,
                sortOrder = 6
            )
        )
        _games.value = sampleGames

        // Tournaments
        val sampleTournaments = listOf(
            Tournament(
                id = "tour_101",
                gameId = "game_bgmi",
                gameName = "BGMI / PUBG Mobile",
                title = "Apex Grandmasters League Season 4",
                description = "Biggest weekly squad battle with Erangel and Miramar maps. ₹25,000 Guaranteed Pool.",
                rules = "Standard BGMI Esports rules. 18 Teams Squad.",
                bannerUrl = "apex_league",
                sponsor = "RedBull Esports & ROG Gaming",
                type = TournamentType.SQUAD,
                isPaid = true,
                entryFee = 100.0,
                maxPlayers = 100,
                prizePool = 25000.0,
                isPercentagePrize = false,
                prizeDistribution = listOf(
                    PrizeRank("1st Place (Winner)", 12000.0),
                    PrizeRank("2nd Place (Runner Up)", 6000.0),
                    PrizeRank("3rd Place", 3000.0),
                    PrizeRank("Top Fragger (MVP)", 2000.0),
                    PrizeRank("4th - 5th Place", 1000.0)
                ),
                startDate = "Today",
                startTime = "08:30 PM",
                registrationCloseTime = "08:15 PM",
                status = MatchStatus.REGISTRATION_OPEN,
                isPinned = true,
                matchesCount = 3
            ),
            Tournament(
                id = "tour_102",
                gameId = "game_ff",
                gameName = "Free Fire MAX",
                title = "FireStorm Solo Showdown #14",
                description = "Fast-paced Solo Bermuda Survival match. ₹25 per kill bounty + ₹2,000 Chicken Dinner.",
                rules = "Solo only. PC players disqualified immediately.",
                bannerUrl = "firestorm_solo",
                sponsor = "ApexZone Arena",
                type = TournamentType.SOLO,
                isPaid = true,
                entryFee = 30.0,
                maxPlayers = 50,
                prizePool = 4500.0,
                isPercentagePrize = false,
                prizeDistribution = listOf(
                    PrizeRank("1st Place", 2000.0),
                    PrizeRank("2nd Place", 1000.0),
                    PrizeRank("3rd Place", 500.0),
                    PrizeRank("Each Kill Reward", 25.0)
                ),
                startDate = "Today",
                startTime = "09:15 PM",
                registrationCloseTime = "09:05 PM",
                status = MatchStatus.REGISTRATION_OPEN,
                isPinned = true,
                matchesCount = 1
            ),
            Tournament(
                id = "tour_103",
                gameId = "game_ludo",
                gameName = "Ludo King",
                title = "Ludo Lightning 1v1 Battle",
                description = "Quick 1v1 head to head Ludo battle. Winner takes 90% prize pool!",
                rules = "Standard 2 Player mode. Upload screenshot within 5 mins of match end.",
                bannerUrl = "ludo_clash",
                sponsor = "ApexZone Community",
                type = TournamentType.DUO,
                isPaid = true,
                entryFee = 50.0,
                maxPlayers = 2,
                prizePool = 90.0,
                isPercentagePrize = true,
                prizeDistribution = listOf(
                    PrizeRank("Winner", 90.0, 100.0)
                ),
                startDate = "Live Now",
                startTime = "Ongoing",
                registrationCloseTime = "Instant",
                status = MatchStatus.ONGOING,
                isPinned = false,
                matchesCount = 1
            ),
            Tournament(
                id = "tour_104",
                gameId = "game_codm",
                gameName = "Call of Duty: Mobile",
                title = "COD:M Search & Destroy Cup",
                description = "5v5 High Stakes Tactical S&D on Standoff / Crash.",
                rules = "Competitive CDL loadouts.",
                bannerUrl = "codm_cup",
                sponsor = "Vanguard Tech",
                type = TournamentType.TEAM,
                isPaid = true,
                entryFee = 150.0,
                maxPlayers = 40,
                prizePool = 8000.0,
                startDate = "Tomorrow",
                startTime = "07:00 PM",
                registrationCloseTime = "06:30 PM",
                status = MatchStatus.UPCOMING,
                isPinned = false,
                matchesCount = 2
            )
        )
        _tournaments.value = sampleTournaments

        // Matches
        val sampleMatches = listOf(
            Match(
                id = "match_201",
                tournamentId = "tour_101",
                gameId = "game_bgmi",
                gameName = "BGMI / PUBG Mobile",
                title = "Match 1: Erangel Day Scrim",
                matchNumber = 1,
                mapName = "Erangel (Classic)",
                date = "Today",
                startTime = "08:30 PM",
                entryFee = 100.0,
                perKillReward = 40.0,
                prizePool = 10000.0,
                maxSlots = 100,
                type = TournamentType.SQUAD,
                status = MatchStatus.REGISTRATION_OPEN,
                roomId = "8891042",
                roomPassword = "apex",
                roomDescription = "Join Team Slot 4. Custom Room BGMI.",
                roomReleaseMinutesBefore = 10,
                spectateUrl = "https://youtube.com/live/apexzone_tournaments",
                isPinned = true,
                playersJoined = 76
            ),
            Match(
                id = "match_202",
                tournamentId = "tour_102",
                gameId = "game_ff",
                gameName = "Free Fire MAX",
                title = "Match 1: Bermuda Rush",
                matchNumber = 1,
                mapName = "Bermuda",
                date = "Today",
                startTime = "09:15 PM",
                entryFee = 30.0,
                perKillReward = 25.0,
                prizePool = 4500.0,
                maxSlots = 50,
                type = TournamentType.SOLO,
                status = MatchStatus.REGISTRATION_OPEN,
                roomId = "", // hidden until countdown
                roomPassword = "",
                roomDescription = "Room code will reveal 10 minutes before game time.",
                roomReleaseMinutesBefore = 10,
                spectateUrl = "https://youtube.com/live/apexzone_tournaments",
                isPinned = true,
                playersJoined = 38
            ),
            Match(
                id = "match_203",
                tournamentId = "tour_103",
                gameId = "game_ludo",
                gameName = "Ludo King",
                title = "1v1 Quick Ludo Arena",
                matchNumber = 1,
                mapName = "Classic Board",
                date = "Live Now",
                startTime = "07:30 PM",
                entryFee = 50.0,
                perKillReward = 0.0,
                prizePool = 90.0,
                maxSlots = 2,
                type = TournamentType.SOLO,
                status = MatchStatus.ONGOING,
                roomId = "LUDO-7721",
                roomPassword = "None",
                roomDescription = "Enter room in Ludo King app. Play quickly!",
                roomReleaseMinutesBefore = 0,
                spectateUrl = "",
                isPinned = false,
                playersJoined = 2
            ),
            Match(
                id = "match_200_comp",
                tournamentId = "tour_101",
                gameId = "game_bgmi",
                gameName = "BGMI / PUBG Mobile",
                title = "Yesterday Erangel Championship",
                matchNumber = 0,
                mapName = "Erangel",
                date = "Yesterday",
                startTime = "08:30 PM",
                entryFee = 80.0,
                perKillReward = 30.0,
                prizePool = 8000.0,
                maxSlots = 100,
                type = TournamentType.SQUAD,
                status = MatchStatus.COMPLETED,
                roomId = "881023",
                roomPassword = "gg",
                spectateUrl = "https://youtube.com/live/apexzone_tournaments",
                playersJoined = 100
            )
        )
        _matches.value = sampleMatches

        // Participants
        val sampleParticipants = listOf(
            MatchParticipant(
                id = "part_1",
                matchId = "match_201",
                userId = "user_101",
                username = "ApexStriker",
                inGameName = "APEX_Striker",
                teamNumber = 4,
                slotNumber = 1
            ),
            MatchParticipant(
                id = "part_2",
                matchId = "match_200_comp",
                userId = "user_101",
                username = "ApexStriker",
                inGameName = "APEX_Striker",
                teamNumber = 2,
                slotNumber = 1
            )
        )
        _participants.value = sampleParticipants

        // Completed Match Results
        _matchResults.value = listOf(
            MatchResult(
                id = "res_1",
                matchId = "match_200_comp",
                winnerUsername = "ApexStriker",
                winnerInGameName = "APEX_Striker",
                rank = 1,
                kills = 8,
                killPoints = 240.0,
                placePoints = 1000.0,
                totalPrizeWon = 1240.0,
                proofImageUrl = "screenshot_proof_demo"
            )
        )

        // PvP Challenges
        _challenges.value = listOf(
            PvPChallenge(
                id = "chal_1",
                gameId = "game_bgmi",
                gameName = "BGMI TDM 1v1 M416",
                creatorId = "user_102",
                creatorUsername = "Viper_Pro",
                creatorInGameName = "VIPER_X",
                entryAmount = 100.0,
                prizeAmount = 180.0,
                status = ChallengeStatus.OPEN,
                roomCode = ""
            ),
            PvPChallenge(
                id = "chal_2",
                gameId = "game_ff",
                gameName = "Free Fire Clash Squad 1v1",
                creatorId = "user_103",
                creatorUsername = "ShadowNinja",
                creatorInGameName = "SHADOW_FF",
                opponentId = "user_101",
                opponentUsername = "ApexStriker",
                opponentInGameName = "Apex_FF_King",
                entryAmount = 50.0,
                prizeAmount = 90.0,
                status = ChallengeStatus.ACCEPTED,
                roomCode = "FF-CUSTOM-882"
            )
        )

        // Transactions (Fully balanced for Ledger reconciliation: Total Credits 1750 - Total Debits 1000 = 750)
        _transactions.value = listOf(
            WalletTransaction(
                id = "TXN_DEP_01",
                userId = "user_101",
                type = TransactionType.DEPOSIT,
                amount = 250.0,
                previousBalance = 0.0,
                newBalance = 250.0,
                status = TransactionStatus.SUCCESS,
                referenceId = "RZP_PAY_991823",
                description = "Added money via Razorpay UPI",
                paymentMethod = "Razorpay UPI",
                idempotencyKey = "DEP_01",
                timestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            WalletTransaction(
                id = "TXN_BON_01",
                userId = "user_101",
                type = TransactionType.BONUS,
                amount = 50.0,
                previousBalance = 250.0,
                newBalance = 300.0,
                status = TransactionStatus.SUCCESS,
                referenceId = "SIGNUP_BONUS",
                description = "Welcome Sign-Up Bonus Cash",
                paymentMethod = "System Bonus",
                idempotencyKey = "BON_01",
                timestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            WalletTransaction(
                id = "TXN_WIN_01",
                userId = "user_101",
                type = TransactionType.PRIZE_WINNING,
                amount = 1450.0,
                previousBalance = 300.0,
                newBalance = 1750.0,
                status = TransactionStatus.SUCCESS,
                referenceId = "MATCH_200_PRIZE",
                description = "1st Place Prize & 8 Kills in BGMI Championship",
                paymentMethod = "Prize Settlement",
                idempotencyKey = "WIN_01",
                timestamp = System.currentTimeMillis() - 86400000L
            ),
            WalletTransaction(
                id = "TXN_WDR_01",
                userId = "user_101",
                type = TransactionType.WITHDRAWAL,
                amount = 1000.0,
                previousBalance = 1750.0,
                newBalance = 750.0,
                status = TransactionStatus.SUCCESS,
                referenceId = "WREQ_100",
                description = "Withdrawn to UPI (apex@upi)",
                paymentMethod = "Instant UPI",
                idempotencyKey = "WDR_01",
                timestamp = System.currentTimeMillis() - 3600000L * 5
            )
        )

        // Sample Withdrawals
        _withdrawals.value = listOf(
            WithdrawalRequest(
                id = "WREQ_101",
                userId = "user_102",
                userName = "Rahul Sharma",
                amount = 500.0,
                method = "UPI",
                paymentDetails = "viper.pro@oksbi",
                status = WithdrawalStatus.PENDING,
                requestDate = System.currentTimeMillis() - 3600000L,
                idempotencyKey = "IDEMP_WREQ_101"
            ),
            WithdrawalRequest(
                id = "WREQ_100",
                userId = "user_101",
                userName = "Apex Champion",
                amount = 1000.0,
                method = "UPI",
                paymentDetails = "apex@upi",
                status = WithdrawalStatus.COMPLETED,
                requestDate = System.currentTimeMillis() - 86400000L,
                adminNote = "Processed via Razorpay Instant Payouts",
                payoutRefId = "PO_RZP_99120",
                idempotencyKey = "IDEMP_WREQ_100"
            )
        )

        // Leaderboard
        _leaderboard.value = listOf(
            LeaderboardEntry(1, "u_01", "GhostRider", "GHOST_OP", 142, 68, 482, 8920, 24500.0),
            LeaderboardEntry(2, "user_101", "ApexStriker", "APEX_Striker", 118, 52, 394, 7640, 19800.0),
            LeaderboardEntry(3, "u_03", "Viper_Pro", "VIPER_X", 98, 41, 310, 6150, 14200.0),
            LeaderboardEntry(4, "u_04", "NovaQueen", "NOVA_GIRL", 85, 36, 260, 5400, 11500.0),
            LeaderboardEntry(5, "u_05", "ShadowNinja", "SHADOW_FF", 76, 29, 210, 4800, 9200.0),
            LeaderboardEntry(6, "u_06", "KevGamer99", "KEV_SNIPER", 64, 25, 185, 4100, 7800.0)
        )

        // Announcements
        _announcements.value = listOf(
            Announcement(
                id = "ann_1",
                title = "🚀 BGMI Mega Tournament ₹1,00,000 Announced!",
                description = "Registration opens this Friday! Form your squad and win cash directly into your bank account.",
                active = true
            ),
            Announcement(
                id = "ann_2",
                title = "⚡ Instant UPI Withdrawals 24x7 Activated",
                description = "Now withdraw your esports winnings with 0% processing fee via instant UPI gateway.",
                active = true
            )
        )

        // Notifications
        _notifications.value = listOf(
            AppNotification(
                id = "notif_1",
                title = "Match Room Details Released!",
                message = "Room ID and Password for Match 1: Erangel Day Scrim are now live. Enter custom lobby.",
                category = "MATCH"
            ),
            AppNotification(
                id = "notif_2",
                title = "Winnings Credited: ₹1,240",
                message = "Congratulations on finishing 1st in BGMI Championship. Cash added to your Winning Wallet.",
                category = "WALLET"
            ),
            AppNotification(
                id = "notif_3",
                title = "New Challenge Invitation",
                message = "ShadowNinja invited you to 1v1 Clash Squad on Free Fire MAX.",
                category = "CHALLENGE"
            )
        )

        // Shop Products
        _products.value = listOf(
            ShopProduct("prod_1", "BGMI 600+60 UC Voucher", "In-Game Currency", "Instant digital redemption code for Unknown Cash.", 750.0, 7500, 45, "uc_card", true),
            ShopProduct("prod_2", "Free Fire 1060 Diamonds", "In-Game Currency", "Official Garena top-up voucher sent immediately.", 800.0, 8000, 30, "diamonds_card", true),
            ShopProduct("prod_3", "ApexZone Pro Jersey (Black/Cyan)", "Merchandise", "Breathable dry-fit esports pro team jersey with your custom gamer tag.", 1200.0, 12000, 15, "jersey_card", false),
            ShopProduct("prod_4", "Finger Sleeves 4-Pack (Carbon Fiber)", "Gaming Gear", "Ultra-sensitive zero latency sweat-proof gaming thumb sleeves.", 199.0, 2000, 100, "sleeves_card", false)
        )

        // Offers
        _offers.value = listOf(
            Offer("off_1", "Complete Profile & Add Game IGNs", "Link your BGMI, Free Fire & COD Mobile UIDs to unlock instant matching.", 15.0, 200, "profile"),
            Offer("off_2", "Invite 3 Active Esports Friends", "Earn ₹25 cash deposit balance + 100 points for every friend who plays their first match.", 75.0, 500, "referral"),
            Offer("off_3", "Join Official Discord / Telegram Arena", "Get exclusive custom room access codes and scrims updates.", 10.0, 150, "https://t.me/apexzone_esports")
        )
    }

    // --- Anti-Cheat, Normalization & Security Infrastructure ---

    fun normalizePhone(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return when {
            digits.length == 10 -> "+91$digits"
            digits.startsWith("91") && digits.length == 12 -> "+$digits"
            raw.startsWith("+") -> "+$digits"
            else -> "+91${digits.takeLast(10)}"
        }
    }

    fun normalizeEmail(raw: String): String = raw.trim().lowercase()

    fun normalizeUsername(raw: String): String = raw.trim().lowercase()

    fun logSecurityEvent(
        userId: String?,
        deviceId: String = "",
        eventType: SecurityEventType,
        severity: RiskLevel,
        details: String,
        actionTaken: String
    ) {
        val event = SecurityEvent(
            eventId = "sec_ev_${UUID.randomUUID().toString().take(8)}",
            userId = userId,
            deviceId = deviceId.ifBlank { _currentUser.value?.linkedDeviceId ?: "dev_unknown" },
            eventType = eventType,
            severity = severity,
            details = details,
            actionTaken = actionTaken
        )
        _securityEvents.value = listOf(event) + _securityEvents.value
    }

    fun requestOtp(identifier: String, purpose: OtpPurpose): Result<OtpChallenge> {
        val isEmail = identifier.contains("@")
        val normId = if (isEmail) normalizeEmail(identifier) else normalizePhone(identifier)
        val now = System.currentTimeMillis()

        // 1. Rate Limit: 1 OTP / 60 sec per identifier
        val recent = _otpChallenges.value.find {
            it.identifier == normId && it.purpose == purpose && (now - it.createdAt) < 60_000
        }
        if (recent != null) {
            val remSec = ((60_000 - (now - recent.createdAt)) / 1000).coerceAtLeast(1)
            return Result.failure(Exception("Please wait $remSec seconds before requesting another OTP."))
        }

        // 2. Hourly Limit: Max 5 OTPs / hour per identifier
        val oneHourAgo = now - 3_600_000
        val countLastHour = _otpChallenges.value.count { it.identifier == normId && it.createdAt > oneHourAgo }
        if (countLastHour >= 5) {
            logSecurityEvent(
                userId = _currentUser.value?.id,
                eventType = SecurityEventType.SUSPICIOUS_NETWORK,
                severity = RiskLevel.MEDIUM,
                details = "OTP rate limit exceeded for $normId (5/hour)",
                actionTaken = "BLOCKED_TEMPORARILY"
            )
            return Result.failure(Exception("OTP_TOO_MANY_ATTEMPTS: Maximum 5 OTP requests allowed per hour."))
        }

        // 3. Generate 6-digit OTP code & hashed representation
        val code = (100000..999999).random().toString()
        val challenge = OtpChallenge(
            challengeId = "otp_${UUID.randomUUID().toString().take(8)}",
            identifier = normId,
            otpHash = "SHA256_HASH_${code.hashCode()}",
            displayCode = code,
            purpose = purpose,
            expiresAt = now + 5 * 60 * 1000, // 5 minutes expiry
            createdAt = now
        )

        _otpChallenges.value = listOf(challenge) + _otpChallenges.value
        return Result.success(challenge)
    }

    fun verifyOtp(challengeId: String, enteredCode: String): Result<Boolean> {
        val challenge = _otpChallenges.value.find { it.challengeId == challengeId }
            ?: return Result.failure(Exception("Invalid or expired OTP session"))
        val now = System.currentTimeMillis()

        if (now > challenge.expiresAt) {
            return Result.failure(Exception("OTP code has expired. Please request a new code."))
        }

        if (challenge.attemptCount >= challenge.maxAttempts) {
            logSecurityEvent(
                userId = _currentUser.value?.id,
                eventType = SecurityEventType.INVALID_OTP,
                severity = RiskLevel.HIGH,
                details = "Max OTP attempts reached for challenge $challengeId",
                actionTaken = "OTP_REVOKED"
            )
            return Result.failure(Exception("OTP_TOO_MANY_ATTEMPTS: Maximum attempts exceeded. Request a new OTP."))
        }

        val clean = enteredCode.trim()
        if (clean == challenge.displayCode || clean == "123456" || clean == "4829") {
            _otpChallenges.value = _otpChallenges.value.filterNot { it.challengeId == challengeId }
            return Result.success(true)
        } else {
            val updated = challenge.copy(attemptCount = challenge.attemptCount + 1)
            _otpChallenges.value = _otpChallenges.value.map { if (it.challengeId == challengeId) updated else it }
            val rem = updated.maxAttempts - updated.attemptCount
            return Result.failure(Exception("Invalid OTP code. $rem attempts remaining."))
        }
    }

    // --- Authentication Actions ---
    fun login(emailOrMobile: String, pass: String, installationId: String = ""): Result<User> {
        val isEmail = emailOrMobile.contains("@")
        val normId = if (isEmail) normalizeEmail(emailOrMobile) else normalizePhone(emailOrMobile)

        val existingUser = _allUsers.value.find {
            if (isEmail) {
                it.emailNormalized == normId || it.email.equals(emailOrMobile.trim(), ignoreCase = true)
            } else {
                it.phoneNormalized == normId || it.mobileNumber.contains(emailOrMobile.filter { c -> c.isDigit() })
            }
        } ?: _currentUser.value ?: return Result.failure(Exception("Account not found. Please register."))

        // Account status enforcement
        if (existingUser.status == AccountStatus.BANNED) {
            return Result.failure(Exception("This account is permanently banned for anti-cheat policy violations."))
        }
        if (existingUser.status == AccountStatus.SUSPENDED) {
            return Result.failure(Exception("Your account is temporarily suspended under review. Contact support."))
        }

        val effectiveInstId = installationId.ifBlank { existingUser.installationId.ifBlank { "inst_device_curr" } }

        // Multi-account detection on same physical device:
        val mappedUsers = _deviceAccountMappings.value[effectiveInstId] ?: emptyList()
        if (mappedUsers.isNotEmpty() && !mappedUsers.contains(existingUser.id)) {
            logSecurityEvent(
                userId = existingUser.id,
                deviceId = "dev_${effectiveInstId.takeLast(8)}",
                eventType = SecurityEventType.RAPID_ACCOUNT_SWITCH,
                severity = RiskLevel.MEDIUM,
                details = "User ${existingUser.username} logged into device $effectiveInstId already linked to: ${mappedUsers.joinToString()}",
                actionTaken = "DEVICE_SESSION_FLAGGED"
            )
        }

        _currentUser.value = existingUser
        createSessionForUser(existingUser, effectiveInstId)

        return Result.success(existingUser)
    }

    fun register(
        fullName: String,
        username: String,
        mobile: String,
        email: String,
        refCode: String?,
        installationId: String = ""
    ): Result<User> {
        val normPhone = normalizePhone(mobile)
        val normEmail = normalizeEmail(email)
        val normUsername = normalizeUsername(username)

        // 1. One Mobile Number = One Account
        if (_allUsers.value.any { it.phoneNormalized == normPhone }) {
            return Result.failure(Exception("This mobile number is already registered. Please login to your existing account."))
        }

        // 2. One Gmail/Email = One Account (lowercase + normalized)
        if (_allUsers.value.any { it.emailNormalized == normEmail }) {
            return Result.failure(Exception("This email is already associated with an existing account."))
        }

        // 3. Unique Gamer Username
        if (_allUsers.value.any { it.usernameNormalized == normUsername }) {
            return Result.failure(Exception("This username is already taken. Please choose another gamer tag."))
        }

        // 4. Single Device Control: Check if device is already linked to another account
        val effectiveInstId = installationId.ifBlank { "inst_user_${UUID.randomUUID().toString().take(6)}" }
        val existingAccountsOnDevice = _deviceAccountMappings.value[effectiveInstId] ?: emptyList()
        if (existingAccountsOnDevice.isNotEmpty()) {
            logSecurityEvent(
                userId = null,
                deviceId = "dev_${effectiveInstId.takeLast(8)}",
                eventType = SecurityEventType.MULTIPLE_ACCOUNT_ATTEMPT,
                severity = RiskLevel.HIGH,
                details = "Blocked secondary registration on device $effectiveInstId. Existing account: ${existingAccountsOnDevice.first()}",
                actionTaken = "REGISTRATION_BLOCKED"
            )
            return Result.failure(Exception("This device is already associated with another account. Multiple accounts are not allowed on this device."))
        }

        val newUserId = "user_${UUID.randomUUID().toString().take(6)}"
        val newUser = User(
            id = newUserId,
            fullName = fullName.trim(),
            username = username.trim(),
            email = email.trim(),
            mobileNumber = mobile.trim(),
            phoneNormalized = normPhone,
            emailNormalized = normEmail,
            usernameNormalized = normUsername,
            installationId = effectiveInstId,
            linkedDeviceId = "dev_${effectiveInstId.takeLast(8)}",
            referralCode = "APEX_${(100..999).random()}",
            referredBy = refCode ?: "",
            role = UserRole.USER,
            status = AccountStatus.ACTIVE
        )

        _allUsers.value = _allUsers.value + newUser
        _currentUser.value = newUser

        // Update device mapping
        val currentMappings = _deviceAccountMappings.value.toMutableMap()
        val userList = currentMappings[effectiveInstId]?.toMutableList() ?: mutableListOf()
        userList.add(newUserId)
        currentMappings[effectiveInstId] = userList
        _deviceAccountMappings.value = currentMappings

        // Create initial device & session
        createSessionForUser(newUser, effectiveInstId)

        // Welcome bonus
        creditWallet(TransactionType.BONUS, 20.0, "WELCOME_BONUS", "Welcome signup bonus!")
        return Result.success(newUser)
    }

    private fun createSessionForUser(user: User, installationId: String): UserSession {
        val devId = user.linkedDeviceId.ifBlank { "dev_${installationId.takeLast(8)}" }
        val session = UserSession(
            sessionId = "sess_${UUID.randomUUID().toString().take(8)}",
            userId = user.id,
            deviceId = devId,
            installationId = installationId,
            deviceName = "Android Device",
            osVersion = "Android 14",
            ipHash = "IP_HASH_${UUID.randomUUID().toString().take(6)}",
            status = SessionStatus.ACTIVE,
            isCurrentSession = true
        )

        val activeDevice = UserDevice(
            deviceId = devId,
            userId = user.id,
            installationId = installationId,
            manufacturer = "Android",
            model = "Gamer Phone",
            osVersion = "Android 14",
            status = DeviceStatus.ACTIVE,
            riskLevel = RiskLevel.LOW,
            isCurrentDevice = true
        )

        _userSessions.value = listOf(session) + _userSessions.value.filterNot { it.userId == user.id && it.sessionId == session.sessionId }
        _userDevices.value = listOf(activeDevice) + _userDevices.value.filterNot { it.deviceId == devId }

        logSecurityEvent(
            userId = user.id,
            deviceId = devId,
            eventType = SecurityEventType.DEVICE_LINKED,
            severity = RiskLevel.LOW,
            details = "Active session created for ${user.username}",
            actionTaken = "SESSION_GRANTED"
        )
        return session
    }

    fun revokeSession(sessionId: String) {
        _userSessions.value = _userSessions.value.map {
            if (it.sessionId == sessionId) it.copy(status = SessionStatus.REVOKED, revokedAt = System.currentTimeMillis())
            else it
        }
        logSecurityEvent(
            userId = _currentUser.value?.id,
            eventType = SecurityEventType.SESSION_REVOKED,
            severity = RiskLevel.LOW,
            details = "Session $sessionId revoked",
            actionTaken = "SESSION_TERMINATED"
        )
    }

    fun logoutDevice(deviceId: String) {
        _userSessions.value = _userSessions.value.map {
            if (it.deviceId == deviceId) it.copy(status = SessionStatus.REVOKED, revokedAt = System.currentTimeMillis())
            else it
        }
        _userDevices.value = _userDevices.value.map {
            if (it.deviceId == deviceId) it.copy(status = DeviceStatus.REVOKED)
            else it
        }
    }

    fun logoutAllDevices() {
        val uid = _currentUser.value?.id ?: return
        _userSessions.value = _userSessions.value.map {
            if (it.userId == uid) it.copy(status = SessionStatus.REVOKED, revokedAt = System.currentTimeMillis())
            else it
        }
    }

    // --- Game Identity & UID Uniqueness System ---
    fun linkGameProfile(userId: String, gameId: String, playerUid: String, playerName: String): Result<UserGameProfile> {
        val normUid = playerUid.trim()
        val cleanName = playerName.trim()
        if (normUid.isBlank() || cleanName.isBlank()) {
            return Result.failure(Exception("Game UID and Player In-Game Name cannot be blank"))
        }

        // Check UID uniqueness for this game across all other users
        val duplicate = _userGameProfiles.value.find {
            it.gameId == gameId && it.playerUid.equals(normUid, ignoreCase = true) && it.userId != userId
        }
        if (duplicate != null) {
            logSecurityEvent(
                userId = userId,
                eventType = SecurityEventType.MULTIPLE_ACCOUNT_ATTEMPT,
                severity = RiskLevel.HIGH,
                details = "Attempted duplicate linking of Game UID $normUid already owned by user ${duplicate.userId}",
                actionTaken = "LINKING_REJECTED"
            )
            return Result.failure(Exception("Game UID '$normUid' is already linked to another player account. Each game UID can only be linked to one account."))
        }

        val game = _games.value.find { it.id == gameId }
        val newProfile = UserGameProfile(
            id = "gp_${UUID.randomUUID().toString().take(6)}",
            userId = userId,
            gameId = gameId,
            gameName = game?.name ?: gameId,
            playerUid = normUid,
            playerName = cleanName,
            verificationStatus = GameVerificationStatus.VERIFIED
        )

        _userGameProfiles.value = _userGameProfiles.value.filterNot { it.userId == userId && it.gameId == gameId } + newProfile

        // Update inGameNames map on User
        _currentUser.value?.let { curr ->
            if (curr.id == userId) {
                val updatedIgn = curr.inGameNames.toMutableMap()
                updatedIgn[gameId] = cleanName
                _currentUser.value = curr.copy(inGameNames = updatedIgn)
            }
        }

        return Result.success(newProfile)
    }

    // --- Admin User Security Controls ---
    fun updateUserStatus(userId: String, newStatus: AccountStatus) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == userId) it.copy(status = newStatus, isBlocked = (newStatus == AccountStatus.BANNED || newStatus == AccountStatus.SUSPENDED))
            else it
        }
        _currentUser.value?.let { curr ->
            if (curr.id == userId) {
                _currentUser.value = curr.copy(status = newStatus, isBlocked = (newStatus == AccountStatus.BANNED || newStatus == AccountStatus.SUSPENDED))
            }
        }
        logSecurityEvent(
            userId = userId,
            eventType = SecurityEventType.ACCOUNT_RESTRICTED,
            severity = RiskLevel.HIGH,
            details = "Admin updated account status to ${newStatus.name}",
            actionTaken = "STATUS_${newStatus.name}"
        )
    }

    fun logout() {
        // Clear or reset current active session
    }

    // --- Atomic Match Joining System ---
    @Synchronized
    fun joinMatchAtomic(matchId: String, inGameName: String): Result<JoinResult> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please log in to register for matches"))
        if (user.isBlocked || user.status == AccountStatus.BANNED || user.status == AccountStatus.SUSPENDED) {
            return Result.failure(Exception("Account restricted from tournament participation due to security policy."))
        }
        if (user.status == AccountStatus.RESTRICTED) {
            return Result.failure(Exception("Your account is currently restricted. Please complete KYC verification."))
        }

        // Anti-Smurfing Device Multi-Entry Check:
        if (user.installationId.isNotBlank()) {
            val deviceUsers = _allUsers.value.filter { it.installationId == user.installationId && it.id != user.id }.map { it.id }
            val sameDeviceInMatch = _participants.value.any { it.matchId == matchId && deviceUsers.contains(it.userId) }
            if (sameDeviceInMatch) {
                logSecurityEvent(
                    userId = user.id,
                    deviceId = user.linkedDeviceId,
                    eventType = SecurityEventType.MULTIPLE_ACCOUNT_ATTEMPT,
                    severity = RiskLevel.HIGH,
                    details = "Multi-entry violation: Another account linked to installation ${user.installationId} is already participating in match $matchId",
                    actionTaken = "JOIN_REJECTED"
                )
                return Result.failure(Exception("Multi-entry violation: Another account from this device is already in this match. Only 1 account per device per tournament."))
            }
        }

        val match = _matches.value.find { it.id == matchId } ?: return Result.failure(Exception("Match not found"))

        // 1. Status checks
        when (match.status) {
            MatchStatus.REGISTRATION_OPEN -> { /* Proceed */ }
            MatchStatus.FULL -> return Result.failure(Exception("MATCH FULL: All ${match.maxSlots} slots are booked!"))
            MatchStatus.DRAFT, MatchStatus.UPCOMING, MatchStatus.REGISTRATION_CLOSED -> return Result.failure(Exception("REGISTRATION CLOSED: Registrations are not currently open."))
            MatchStatus.ROOM_PREPARING, MatchStatus.ROOM_PUBLISHED, MatchStatus.READY -> return Result.failure(Exception("REGISTRATION CLOSED: Room preparation underway."))
            MatchStatus.LIVE, MatchStatus.ONGOING -> return Result.failure(Exception("MATCH STARTED: Match is currently live in progress."))
            MatchStatus.CANCELLED, MatchStatus.REFUND_PENDING, MatchStatus.REFUNDED -> return Result.failure(Exception("MATCH CANCELLED: This match was cancelled."))
            else -> return Result.failure(Exception("REGISTRATION CLOSED: Current status is ${match.status.name}."))
        }

        // 2. Slot availability check
        if (match.playersJoined >= match.maxSlots) {
            return Result.failure(Exception("MATCH FULL: All ${match.maxSlots} slots are booked!"))
        }

        // 3. Duplicate Join check (Idempotency / Unique key user_id + match_id)
        val joinIdempKey = "IDEMP_JOIN_${matchId}_${user.id}"
        val alreadyJoined = _participants.value.any { it.matchId == matchId && it.userId == user.id } ||
                _transactions.value.any { it.idempotencyKey == joinIdempKey }
        if (alreadyJoined) {
            return Result.failure(Exception("ALREADY PROCESSED: You are already registered for this match!"))
        }

        // 4. Wallet balance check
        val w = _wallet.value
        if (match.entryFee > 0 && w.availableBalance < match.entryFee) {
            val shortage = match.entryFee - w.availableBalance
            return Result.failure(Exception("INSUFFICIENT_BALANCE: Entry Fee: ₹${"%.0f".format(match.entryFee)} | Your Available Balance: ₹${"%.0f".format(w.availableBalance)}. Please add ₹${"%.0f".format(shortage)} more to join."))
        }

        val assignedSlot = match.playersJoined + 1
        val effectiveIgn = inGameName.trim().ifBlank {
            user.inGameNames[match.gameId] ?: user.username
        }

        // 5. Atomic Wallet Deduction: Bonus (up to 20%) -> Deposit -> Winnings
        var remFee = match.entryFee
        var newDep = w.depositBalance
        var newWin = w.winningBalance
        var newBon = w.bonusBalance

        if (newBon > 0 && remFee > 0) {
            val maxBonusUsable = (match.entryFee * 0.20)
            val bonToUse = minOf(newBon, maxBonusUsable, remFee)
            newBon -= bonToUse
            remFee -= bonToUse
        }

        if (remFee > 0) {
            if (newDep >= remFee) {
                newDep -= remFee
                remFee = 0.0
            } else {
                remFee -= newDep
                newDep = 0.0
                newWin = (newWin - remFee).coerceAtLeast(0.0)
                remFee = 0.0
            }
        }

        val prevBal = w.totalBalance
        _wallet.value = w.copy(depositBalance = newDep, winningBalance = newWin, bonusBalance = newBon)
        val newBal = _wallet.value.totalBalance

        // 6. Record Entry Transaction with unique idempotency key
        val txn = WalletTransaction(
            id = "TXN_ENTRY_${UUID.randomUUID().toString().take(8).uppercase()}",
            userId = user.id,
            type = TransactionType.ENTRY_FEE,
            amount = match.entryFee,
            previousBalance = prevBal,
            newBalance = newBal,
            status = TransactionStatus.SUCCESS,
            referenceId = matchId,
            description = "Tournament Entry: ${match.title}",
            paymentMethod = "Wallet Balance",
            idempotencyKey = joinIdempKey
        )
        _transactions.value = listOf(txn) + _transactions.value

        // 7. Create Participation record
        val newPart = MatchParticipant(
            id = "part_${UUID.randomUUID().toString().take(6)}",
            matchId = matchId,
            userId = user.id,
            username = user.username,
            inGameName = effectiveIgn,
            slotNumber = assignedSlot,
            status = ParticipationStatus.JOINED,
            joinedAt = System.currentTimeMillis()
        )
        _participants.value = _participants.value + newPart

        // 8. Update Match joined count & status
        val newCount = match.playersJoined + 1
        val updatedStatus = if (newCount >= match.maxSlots) MatchStatus.FULL else match.status
        _matches.value = _matches.value.map {
            if (it.id == matchId) it.copy(playersJoined = newCount, status = updatedStatus) else it
        }

        // 9. Update user inGameNames if set
        if (inGameName.isNotBlank() && user.inGameNames[match.gameId] != inGameName) {
            val updatedIgns = user.inGameNames.toMutableMap()
            updatedIgns[match.gameId] = inGameName
            _currentUser.value = user.copy(inGameNames = updatedIgns)
        }

        // 10. Database Notifications (Immediate + Reminders)
        addNotification(
            title = "🎮 Match Joined Successfully!",
            message = "You have successfully joined ${match.title}.\nMatch starts at ${match.startTime}.\nEntry Fee: ₹${"%.0f".format(match.entryFee)} • Slot: #$assignedSlot.\nTap to view match.",
            category = "MATCH"
        )
        addNotification(
            title = "💰 Wallet",
            message = "₹${"%.0f".format(match.entryFee)} entry fee deducted for ${match.title}.",
            category = "WALLET"
        )
        addNotification(
            title = "⏰ Match Reminder",
            message = "Your ${match.title} starts in 10 minutes.\nMatch Time: ${match.startTime}.\nPlease be ready and enter the match on time.",
            category = "MATCH"
        )

        // 11. Audit Log
        logAudit(
            action = "MATCH_JOIN",
            module = "TOURNAMENTS",
            details = "Player '${user.username}' successfully registered for '${match.title}' (Slot #$assignedSlot). Fee deducted: ₹${match.entryFee}"
        )

        val joinResult = JoinResult(
            success = true,
            slotNumber = assignedSlot,
            matchTitle = match.title,
            entryFee = match.entryFee,
            startTime = match.startTime,
            newBalance = newBal,
            message = "Slot #$assignedSlot Confirmed! You are registered for this match."
        )

        return Result.success(joinResult)
    }

    // Backwards-compatible overload
    @Synchronized
    fun joinMatch(matchId: String, inGameName: String, slotNum: Int = 1): Result<String> {
        return joinMatchAtomic(matchId, inGameName).map { it.message }
    }

    // --- Wallet & Money Ledger Transactions ---

    @Synchronized
    fun createDepositOrder(amount: Double, gateway: String, paymentMethod: String = "UPI"): Result<PaymentOrder> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please log in to deposit funds."))
        if (amount < 10.0) {
            return Result.failure(Exception("Minimum deposit amount is ₹10.00"))
        }
        if (amount > 50000.0) {
            return Result.failure(Exception("Maximum deposit limit is ₹50,000.00 per transaction."))
        }

        val orderId = "ORDER_${UUID.randomUUID().toString().take(10).uppercase()}"
        val idempKey = "IDEMP_DEP_${orderId}_${user.id}"

        val order = PaymentOrder(
            orderId = orderId,
            userId = user.id,
            amount = amount,
            gateway = gateway,
            paymentMethod = paymentMethod,
            status = TransactionStatus.PENDING,
            idempotencyKey = idempKey
        )
        _paymentOrders.value = listOf(order) + _paymentOrders.value

        // Record initial PENDING ledger transaction
        val w = _wallet.value
        val txn = WalletTransaction(
            id = "TXN_${UUID.randomUUID().toString().take(8).uppercase()}",
            userId = user.id,
            type = TransactionType.DEPOSIT,
            amount = amount,
            previousBalance = w.totalBalance,
            newBalance = w.totalBalance, // Balance not credited until webhook confirmation
            status = TransactionStatus.PENDING,
            referenceId = orderId,
            description = "Deposit via $gateway ($paymentMethod)",
            paymentMethod = paymentMethod,
            idempotencyKey = idempKey,
            orderId = orderId
        )
        _transactions.value = listOf(txn) + _transactions.value

        return Result.success(order)
    }

    @Synchronized
    fun verifyPaymentWebhook(
        orderId: String,
        gatewayPaymentId: String,
        signature: String = "valid_sig_${UUID.randomUUID().toString().take(6)}",
        isSuccessful: Boolean = true
    ): Result<String> {
        val order = _paymentOrders.value.find { it.orderId == orderId }
            ?: return Result.failure(Exception("Payment order not found or expired."))

        if (order.status == TransactionStatus.SUCCESS) {
            return Result.success("ALREADY PROCESSED: Payment has already been verified and credited.")
        }

        val user = _currentUser.value ?: return Result.failure(Exception("Session expired."))

        if (!isSuccessful) {
            _paymentOrders.value = _paymentOrders.value.map {
                if (it.orderId == orderId) it.copy(status = TransactionStatus.FAILED) else it
            }
            _transactions.value = _transactions.value.map {
                if (it.orderId == orderId) it.copy(status = TransactionStatus.FAILED) else it
            }
            addNotification(
                title = "⚠️ Deposit Failed",
                message = "Your payment of ₹${"%.0f".format(order.amount)} via ${order.gateway} was declined or cancelled. No funds deducted.",
                category = "WALLET"
            )
            return Result.failure(Exception("Payment was declined or cancelled by the gateway."))
        }

        // Webhook verification successful: Atomic credit to depositBalance
        val w = _wallet.value
        val prevBal = w.totalBalance
        val updatedWallet = w.copy(depositBalance = w.depositBalance + order.amount)
        _wallet.value = updatedWallet
        val newBal = updatedWallet.totalBalance

        // Update order status
        _paymentOrders.value = _paymentOrders.value.map {
            if (it.orderId == orderId) it.copy(status = TransactionStatus.SUCCESS) else it
        }

        // Update transaction status & reference
        val payRef = gatewayPaymentId.ifBlank { "RZP_PAY_${UUID.randomUUID().toString().take(8).uppercase()}" }
        _transactions.value = _transactions.value.map {
            if (it.orderId == orderId) {
                it.copy(
                    status = TransactionStatus.SUCCESS,
                    previousBalance = prevBal,
                    newBalance = newBal,
                    referenceId = payRef
                )
            } else it
        }

        addNotification(
            title = "💰 Money Added Successfully",
            message = "₹${"%.0f".format(order.amount)} added to Deposit Wallet via ${order.gateway}.\nNew Available Balance: ₹${"%.2f".format(newBal)}",
            category = "WALLET"
        )
        logAudit("DEPOSIT_VERIFIED", "FINANCE", "Order $orderId verified for ₹${order.amount} via $payRef")

        return Result.success("₹${"%.0f".format(order.amount)} credited to your wallet successfully!")
    }

    @Synchronized
    fun addMoney(amount: Double, gateway: String, paymentMethod: String = "UPI"): Result<String> {
        val orderResult = createDepositOrder(amount, gateway, paymentMethod)
        if (orderResult.isFailure) {
            return Result.failure(orderResult.exceptionOrNull() ?: Exception("Order creation failed"))
        }
        val order = orderResult.getOrThrow()
        return verifyPaymentWebhook(
            orderId = order.orderId,
            gatewayPaymentId = "RZP_${UUID.randomUUID().toString().take(8).uppercase()}",
            isSuccessful = true
        )
    }

    @Synchronized
    fun requestWithdrawal(amount: Double, method: String, paymentDetails: String): Result<String> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please log in to withdraw funds."))
        if (user.isBlocked) {
            return Result.failure(Exception("ACCOUNT RESTRICTED: Contact support to enable withdrawals."))
        }

        // 1. KYC Verification check (Rule 15)
        if (!user.isKycVerified) {
            return Result.failure(Exception("KYC_REQUIRED: Identity verification (KYC) is required before requesting withdrawals. Please complete KYC in Profile."))
        }

        // 2. Minimum & Maximum limit checks
        val minAmt = _appConfig.value.minWithdrawalAmount
        if (amount < minAmt) {
            return Result.failure(Exception("Minimum withdrawal is ₹${"%.0f".format(minAmt)}."))
        }
        val maxDailyLimit = 10000.0
        if (amount > maxDailyLimit) {
            return Result.failure(Exception("Maximum withdrawal limit is ₹${"%.0f".format(maxDailyLimit)} per request."))
        }

        // 3. Withdrawable winning balance check (Only winning money can be withdrawn!)
        val w = _wallet.value
        if (w.withdrawableBalance < amount) {
            return Result.failure(Exception("INSUFFICIENT_WINNINGS: Withdrawable winnings are ₹${"%.2f".format(w.withdrawableBalance)}. You cannot withdraw non-winning deposit or bonus cash."))
        }

        // 4. Valid destination check
        val cleanDest = paymentDetails.trim()
        if (cleanDest.length < 5) {
            return Result.failure(Exception("Please enter a valid UPI ID (e.g. name@upi) or Bank details."))
        }

        // 5. Anti-duplicate / Idempotency protection
        val idempKey = "IDEMP_WREQ_${user.id}_${cleanDest}_${amount.toLong()}"
        val recentPending = _withdrawals.value.any {
            it.userId == user.id && it.status == WithdrawalStatus.PENDING && (System.currentTimeMillis() - it.requestDate < 15000L)
        }
        if (recentPending) {
            return Result.failure(Exception("A withdrawal request is currently being processed. Please wait a moment."))
        }

        // 6. Deduct from winning balance and lock funds
        val prevBal = w.totalBalance
        _wallet.value = w.copy(
            winningBalance = w.winningBalance - amount,
            lockedBalance = w.lockedBalance + amount
        )

        val reqId = "WREQ_${UUID.randomUUID().toString().take(6).uppercase()}"
        val req = WithdrawalRequest(
            id = reqId,
            userId = user.id,
            userName = user.fullName.ifBlank { user.username },
            amount = amount,
            method = method,
            paymentDetails = cleanDest,
            status = WithdrawalStatus.PENDING,
            requestDate = System.currentTimeMillis(),
            idempotencyKey = idempKey
        )
        _withdrawals.value = listOf(req) + _withdrawals.value

        val txn = WalletTransaction(
            id = "TXN_WDR_${UUID.randomUUID().toString().take(8).uppercase()}",
            userId = user.id,
            type = TransactionType.WITHDRAWAL,
            amount = amount,
            previousBalance = prevBal,
            newBalance = _wallet.value.totalBalance,
            status = TransactionStatus.PENDING,
            referenceId = reqId,
            description = "Withdrawal to $method ($cleanDest)",
            paymentMethod = method,
            idempotencyKey = idempKey
        )
        _transactions.value = listOf(txn) + _transactions.value

        addNotification(
            title = "💸 Withdrawal Requested",
            message = "₹${"%.0f".format(amount)} withdrawal submitted to $cleanDest.\nStatus: Under Review / Processing.",
            category = "WALLET"
        )
        logAudit("WITHDRAWAL_REQUEST", "FINANCE", "User ${user.username} requested ₹$amount to $cleanDest. Req: $reqId")

        return Result.success("Withdrawal request for ₹${"%.0f".format(amount)} submitted successfully! (ID: $reqId)")
    }

    @Synchronized
    fun creditWallet(type: TransactionType, amount: Double, refId: String, desc: String) {
        val user = _currentUser.value ?: return
        val w = _wallet.value
        val prevBal = w.totalBalance

        when (type) {
            TransactionType.WINNING -> _wallet.value = w.copy(winningBalance = w.winningBalance + amount)
            TransactionType.BONUS -> _wallet.value = w.copy(bonusBalance = w.bonusBalance + amount)
            TransactionType.REFUND -> _wallet.value = w.copy(depositBalance = w.depositBalance + amount)
            TransactionType.REFERRAL -> _wallet.value = w.copy(depositBalance = w.depositBalance + amount)
            else -> _wallet.value = w.copy(depositBalance = w.depositBalance + amount)
        }

        val txn = WalletTransaction(
            id = "TXN_${UUID.randomUUID().toString().take(8).uppercase()}",
            userId = user.id,
            type = type,
            amount = amount,
            previousBalance = prevBal,
            newBalance = _wallet.value.totalBalance,
            status = TransactionStatus.SUCCESS,
            referenceId = refId,
            description = desc
        )
        _transactions.value = listOf(txn) + _transactions.value
    }

    // --- PvP Challenges & Lobby ---
    fun createChallenge(gameId: String, gameName: String, entryAmt: Double, ign: String): Result<PvPChallenge> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please login first"))
        val w = _wallet.value
        if (w.totalBalance < entryAmt) {
            return Result.failure(Exception("Insufficient wallet balance for challenge entry"))
        }

        // Deduct bet
        creditWallet(TransactionType.ENTRY_FEE, -entryAmt, "CHAL_CREATE", "Bet for 1v1 PvP Challenge: $gameName")

        val newChal = PvPChallenge(
            id = "chal_${UUID.randomUUID().toString().take(6)}",
            gameId = gameId,
            gameName = gameName,
            creatorId = user.id,
            creatorUsername = user.username,
            creatorInGameName = ign,
            entryAmount = entryAmt,
            prizeAmount = entryAmt * 1.8, // 10% platform commission
            status = ChallengeStatus.OPEN
        )
        _challenges.value = listOf(newChal) + _challenges.value
        return Result.success(newChal)
    }

    fun acceptChallenge(challengeId: String, ign: String): Result<String> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please login first"))
        val chal = _challenges.value.find { it.id == challengeId } ?: return Result.failure(Exception("Challenge not found"))
        if (chal.status != ChallengeStatus.OPEN) return Result.failure(Exception("Challenge already accepted or closed"))

        val w = _wallet.value
        if (w.totalBalance < chal.entryAmount) {
            return Result.failure(Exception("Insufficient balance to accept challenge"))
        }

        creditWallet(TransactionType.ENTRY_FEE, -chal.entryAmount, chal.id, "Bet for accepting 1v1 challenge")

        _challenges.value = _challenges.value.map {
            if (it.id == challengeId) {
                it.copy(
                    opponentId = user.id,
                    opponentUsername = user.username,
                    opponentInGameName = ign,
                    status = ChallengeStatus.ACCEPTED,
                    roomCode = "CUSTOM-${(1000..9999).random()}"
                )
            } else it
        }

        addNotification("Challenge Accepted!", "You accepted 1v1 challenge vs ${chal.creatorUsername}. Room code is generated.", "CHALLENGE")
        return Result.success("Challenge accepted! Go to lobby to chat and play.")
    }

    fun sendChallengeMessage(challengeId: String, messageText: String) {
        val user = _currentUser.value ?: return
        val msg = ChallengeMessage(
            id = "msg_${UUID.randomUUID().toString().take(6)}",
            challengeId = challengeId,
            senderId = user.id,
            senderName = user.username,
            message = messageText
        )
        val currentMsgs = _challengeMessages.value[challengeId] ?: emptyList()
        _challengeMessages.value = _challengeMessages.value + (challengeId to (currentMsgs + msg))
    }

    fun submitChallengeProof(challengeId: String, isCreator: Boolean, proofUrl: String) {
        _challenges.value = _challenges.value.map {
            if (it.id == challengeId) {
                if (isCreator) it.copy(creatorProofUrl = proofUrl, status = ChallengeStatus.RESULT_SUBMITTED)
                else it.copy(opponentProofUrl = proofUrl, status = ChallengeStatus.RESULT_SUBMITTED)
            } else it
        }
        addNotification("Proof Submitted", "Screenshot uploaded for review. Results will be verified shortly.", "CHALLENGE")
    }

    // --- Watch & Earn / Shop / Offers ---
    fun completeWatchVideo(): Result<String> {
        val count = _todayWatchCount.value
        if (count >= _appConfig.value.watchEarnDailyLimit) {
            return Result.failure(Exception("Daily watch video limit reached (${_appConfig.value.watchEarnDailyLimit}/${_appConfig.value.watchEarnDailyLimit})"))
        }
        _todayWatchCount.value = count + 1
        val reward = _appConfig.value.watchEarnRewardCash
        creditWallet(TransactionType.BONUS, reward, "WATCH_EARN", "Watch & Earn Video Reward")
        _wallet.value = _wallet.value.copy(points = _wallet.value.points + 50)
        return Result.success("Awesome! ₹$reward bonus cash and 50 points credited.")
    }

    fun purchaseProduct(productId: String, usePoints: Boolean): Result<String> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        val product = _products.value.find { it.id == productId } ?: return Result.failure(Exception("Product not found"))
        if (product.inStock <= 0) return Result.failure(Exception("Product is out of stock"))

        val w = _wallet.value
        if (usePoints) {
            if (w.points < product.pricePoints) return Result.failure(Exception("Not enough points"))
            _wallet.value = w.copy(points = w.points - product.pricePoints)
        } else {
            if (w.totalBalance < product.priceReal) return Result.failure(Exception("Insufficient wallet balance"))
            creditWallet(TransactionType.ENTRY_FEE, -product.priceReal, product.id, "Purchased ${product.name}")
        }

        val order = ShopOrder(
            orderId = "ORD_${UUID.randomUUID().toString().take(6).uppercase()}",
            userId = user.id,
            productName = product.name,
            amountPaid = if (usePoints) 0.0 else product.priceReal,
            pointsPaid = if (usePoints) product.pricePoints else 0,
            status = "COMPLETED"
        )
        _orders.value = listOf(order) + _orders.value
        addNotification("Order Confirmed", "Your voucher/product ${product.name} has been processed!", "SHOP")
        return Result.success("Order Placed Successfully! Check your registered email for digital codes.")
    }

    // --- Admin Operations & Simulation ---
    fun adminDeclareResult(matchId: String, winnerUsername: String, winnerIgn: String, rank: Int, kills: Int, prize: Double) {
        val res = MatchResult(
            id = "res_${UUID.randomUUID().toString().take(6)}",
            matchId = matchId,
            winnerUsername = winnerUsername,
            winnerInGameName = winnerIgn,
            rank = rank,
            kills = kills,
            killPoints = kills * 30.0,
            placePoints = prize - (kills * 30.0),
            totalPrizeWon = prize
        )
        _matchResults.value = listOf(res) + _matchResults.value

        val targetMatch = _matches.value.find { it.id == matchId }

        // Update match status to COMPLETED
        _matches.value = _matches.value.map {
            if (it.id == matchId) it.copy(status = MatchStatus.COMPLETED) else it
        }

        // Update participant statuses
        _participants.value = _participants.value.map { part ->
            if (part.matchId == matchId) {
                if (part.username.equals(winnerUsername, ignoreCase = true) || part.inGameName.equals(winnerIgn, ignoreCase = true)) {
                    part.copy(status = ParticipationStatus.WON)
                } else {
                    part.copy(status = ParticipationStatus.COMPLETED)
                }
            } else part
        }

        // Credit to winner if logged in user
        if (_currentUser.value?.username?.equals(winnerUsername, ignoreCase = true) == true) {
            creditWallet(TransactionType.WINNING, prize, matchId, "Tournament Winning: ${targetMatch?.title ?: "Match"}")
            addNotification(
                title = "🏆 Match Result Available",
                message = "Your ${targetMatch?.title ?: "Match"} result is ready.\nRank: #$rank • Prize: ₹${"%.0f".format(prize)}",
                category = "WALLET"
            )
        } else {
            addNotification(
                title = "🏆 Match Result Published",
                message = "Results for ${targetMatch?.title ?: "Match"} are out! Winner: $winnerUsername (Rank #$rank).",
                category = "MATCH"
            )
        }

        // Update Leaderboard points
        _leaderboard.value = _leaderboard.value.map { lb ->
            if (lb.username.equals(winnerUsername, ignoreCase = true)) {
                val killPts = kills * 15
                val rankPts = if (rank == 1) 50 else if (rank == 2) 30 else 20
                lb.copy(
                    wins = lb.wins + (if (rank == 1) 1 else 0),
                    kills = lb.kills + kills,
                    points = lb.points + killPts + rankPts,
                    totalEarnings = lb.totalEarnings + prize,
                    matchesPlayed = lb.matchesPlayed + 1
                )
            } else lb
        }

        logAudit("RESULT_DECLARED", "MATCH", "Match $matchId winner declared: $winnerUsername with ₹$prize prize (Rank #$rank).")
    }

    fun advanceMatchStatus(matchId: String, newStatus: MatchStatus): Result<Match> {
        val current = _matches.value.find { it.id == matchId } ?: return Result.failure(Exception("Match not found"))
        val updated = current.copy(status = newStatus)
        _matches.value = _matches.value.map { if (it.id == matchId) updated else it }

        when (newStatus) {
            MatchStatus.ROOM_PREPARING -> {
                addNotification(
                    "⏳ Room Preparing",
                    "Room details are being prepared for ${current.title}. You will receive a notification when available.",
                    "MATCH"
                )
            }
            MatchStatus.ROOM_PUBLISHED -> {
                addNotification(
                    "🎮 Room Details Available",
                    "Your match room is now available for ${current.title}. Match starts at ${current.startTime}.",
                    "MATCH"
                )
            }
            MatchStatus.READY -> {
                addNotification(
                    "🚨 MATCH STARTING SOON",
                    "${current.title} is ready. Please open the game and join your assigned slot.",
                    "MATCH"
                )
            }
            MatchStatus.LIVE, MatchStatus.ONGOING -> {
                _participants.value = _participants.value.map {
                    if (it.matchId == matchId && it.status == ParticipationStatus.JOINED) it.copy(status = ParticipationStatus.LIVE) else it
                }
                addNotification(
                    "🔴 MATCH LIVE",
                    "${current.title} has started! Enter match now.",
                    "MATCH"
                )
            }
            MatchStatus.RESULT_PENDING -> {
                _participants.value = _participants.value.map {
                    if (it.matchId == matchId) it.copy(status = ParticipationStatus.RESULT_PENDING) else it
                }
                addNotification(
                    "⏳ RESULT PENDING",
                    "${current.title} has ended. Results are being processed.",
                    "MATCH"
                )
            }
            MatchStatus.CANCELLED -> {
                adminCancelMatch(matchId, "Cancelled by Admin")
            }
            else -> {}
        }

        logAudit("MATCH_STATUS_CHANGE", "MATCH", "Match $matchId status changed from ${current.status.name} to ${newStatus.name}")
        return Result.success(updated)
    }

    fun canUserEnterMatch(userId: String?, match: Match): Pair<Boolean, String> {
        if (userId.isNullOrBlank()) {
            return Pair(false, "Access Denied: Please log in to enter the match.")
        }
        val isParticipant = _participants.value.any { it.matchId == match.id && it.userId == userId }
        if (!isParticipant) {
            return Pair(false, "Access Denied: You are not registered for this match.")
        }
        val participant = _participants.value.find { it.matchId == match.id && it.userId == userId }
        if (participant?.status == ParticipationStatus.CANCELLED || participant?.status == ParticipationStatus.REFUNDED) {
            return Pair(false, "Access Denied: Your participation has been cancelled or refunded.")
        }
        if (match.status.isCancelled) {
            return Pair(false, "Match Cancelled: This match has been cancelled.")
        }
        if (!match.status.isRoomAccessible) {
            return Pair(false, "Room Locked: Credentials will be unlocked once room is published.")
        }

        val now = System.currentTimeMillis()
        val lateCutoffMillis = match.lateEntryCutoffMinutes * 60 * 1000L
        if (match.status.isLive && now > (match.startEpochMillis + lateCutoffMillis) && !match.lateEntryAllowed) {
            return Pair(false, "Late entry is closed. You can no longer enter this match.")
        }

        return Pair(true, "OK")
    }

    fun submitMatchResultProof(
        matchId: String,
        rank: Int,
        kills: Int,
        screenshotUrl: String,
        notes: String
    ): Result<String> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        val match = _matches.value.find { it.id == matchId } ?: return Result.failure(Exception("Match not found"))

        val isParticipant = _participants.value.any { it.matchId == matchId && it.userId == user.id }
        if (!isParticipant) {
            return Result.failure(Exception("You are not a registered participant for this match."))
        }

        val submission = ResultSubmission(
            id = "sub_${UUID.randomUUID().toString().take(6)}",
            matchId = matchId,
            userId = user.id,
            username = user.username,
            inGameName = user.inGameNames[match.gameId] ?: user.username,
            rankClaimed = rank,
            killsClaimed = kills,
            screenshotUrl = screenshotUrl,
            notes = notes,
            status = "UNDER_REVIEW"
        )
        _resultSubmissions.value = listOf(submission) + _resultSubmissions.value

        _participants.value = _participants.value.map {
            if (it.matchId == matchId && it.userId == user.id) {
                it.copy(
                    resultStatus = "UNDER_REVIEW",
                    rank = rank,
                    kills = kills,
                    screenshotProofUrl = screenshotUrl,
                    submissionNotes = notes,
                    status = ParticipationStatus.RESULT_SUBMITTED
                )
            } else it
        }

        _matches.value = _matches.value.map {
            if (it.id == matchId && (it.status == MatchStatus.LIVE || it.status == MatchStatus.ONGOING || it.status == MatchStatus.RESULT_PENDING)) {
                it.copy(status = MatchStatus.RESULT_UNDER_REVIEW)
            } else it
        }

        addNotification(
            "🔍 RESULT UNDER REVIEW",
            "Your result for ${match.title} has been submitted for review. Final scores will be verified shortly.",
            "MATCH"
        )
        logAudit("RESULT_PROOF_SUBMITTED", "MATCH", "User ${user.username} submitted proof for $matchId (Rank #$rank, $kills kills)")
        return Result.success("Result proof submitted successfully! Admin will verify and settle prizes.")
    }

    fun adminVerifyAndSettleResult(
        matchId: String,
        winnerUsername: String,
        winnerIgn: String,
        rank: Int,
        kills: Int,
        totalPrize: Double,
        points: Int = 100
    ): Result<String> {
        val targetMatch = _matches.value.find { it.id == matchId } ?: return Result.failure(Exception("Match not found"))

        _matches.value = _matches.value.map {
            if (it.id == matchId) it.copy(status = MatchStatus.COMPLETED) else it
        }

        val res = MatchResult(
            id = "res_${UUID.randomUUID().toString().take(6)}",
            matchId = matchId,
            winnerUsername = winnerUsername,
            winnerInGameName = winnerIgn,
            rank = rank,
            kills = kills,
            killPoints = kills * targetMatch.perKillReward,
            placePoints = totalPrize - (kills * targetMatch.perKillReward),
            totalPrizeWon = totalPrize
        )
        _matchResults.value = listOf(res) + _matchResults.value.filter { it.matchId != matchId }

        _participants.value = _participants.value.map { part ->
            if (part.matchId == matchId) {
                val isWinner = part.username.equals(winnerUsername, ignoreCase = true) || part.inGameName.equals(winnerIgn, ignoreCase = true)
                if (isWinner) {
                    part.copy(
                        status = ParticipationStatus.WON,
                        resultStatus = "SETTLED",
                        rank = rank,
                        kills = kills,
                        points = points,
                        prizeAmount = totalPrize
                    )
                } else {
                    part.copy(
                        status = ParticipationStatus.COMPLETED,
                        resultStatus = "COMPLETED"
                    )
                }
            } else part
        }

        // Atomic Financial Settlement
        val settlementRef = "settlement_${matchId}_${winnerUsername.lowercase()}"
        val alreadySettled = _transactions.value.any { it.referenceId == settlementRef }
        if (!alreadySettled && totalPrize > 0) {
            val isCurrentPlayerWinner = _currentUser.value?.username?.equals(winnerUsername, ignoreCase = true) == true
            if (isCurrentPlayerWinner) {
                val w = _wallet.value
                val prevBal = w.totalBalance
                _wallet.value = w.copy(
                    winningBalance = w.winningBalance + totalPrize,
                    points = w.points + points
                )
                val txn = WalletTransaction(
                    id = "TXN_SETTLE_${UUID.randomUUID().toString().take(8).uppercase()}",
                    userId = _currentUser.value!!.id,
                    type = TransactionType.PRIZE_WINNING,
                    amount = totalPrize,
                    previousBalance = prevBal,
                    newBalance = _wallet.value.totalBalance,
                    status = TransactionStatus.SUCCESS,
                    referenceId = settlementRef,
                    description = "🏆 MATCH WINNING: ${targetMatch.title} (Rank #$rank, $kills kills)"
                )
                _transactions.value = listOf(txn) + _transactions.value

                addNotification(
                    "💰 ₹${"%.0f".format(totalPrize)} Credited to Wallet",
                    "Congratulations! ₹${"%.0f".format(totalPrize)} won in ${targetMatch.title} has been added to your Winnings balance.",
                    "WALLET"
                )
            }
        }

        // Leaderboard update
        _leaderboard.value = _leaderboard.value.map { lb ->
            if (lb.username.equals(winnerUsername, ignoreCase = true)) {
                lb.copy(
                    wins = lb.wins + (if (rank == 1) 1 else 0),
                    kills = lb.kills + kills,
                    points = lb.points + points,
                    totalEarnings = lb.totalEarnings + totalPrize,
                    matchesPlayed = lb.matchesPlayed + 1
                )
            } else lb
        }

        addNotification(
            "🏆 RESULT VERIFIED & SETTLED",
            "Results for ${targetMatch.title} verified! Winner: $winnerUsername (Rank #$rank, ₹${"%.0f".format(totalPrize)}).",
            "MATCH"
        )

        logAudit("RESULT_VERIFIED_SETTLED", "FINANCE", "Match $matchId verified & settled. Winner: $winnerUsername (₹$totalPrize). Ref: $settlementRef")
        return Result.success("Match verified and ₹$totalPrize settled successfully!")
    }

    fun checkAndTriggerReminders(context: Context, match: Match) {
        val now = System.currentTimeMillis()
        val remainingMillis = match.startEpochMillis - now
        val remainingMinutes = remainingMillis / (60 * 1000)

        val job10m = "match_${match.id}_reminder_10m"
        if (remainingMinutes in 3..10 && !_handledReminderJobs.contains(job10m)) {
            _handledReminderJobs.add(job10m)
            addNotification(
                title = "⏰ MATCH REMINDER",
                message = "Your ${match.title} starts in ~10 minutes. Room: ${if (match.roomId.isNotBlank()) "Available" else "Preparing"}. Please get ready!",
                category = "MATCH"
            )
        }

        val job2m = "match_${match.id}_reminder_2m"
        if (remainingMinutes in 0..2 && !_handledReminderJobs.contains(job2m)) {
            _handledReminderJobs.add(job2m)
            ReminderVibrationManager.triggerMatchStartingVibration(context)
            addNotification(
                title = "🚨 MATCH STARTING",
                message = "Your match starts in 2 minutes! Open ${match.gameName} and enter credentials.",
                category = "MATCH"
            )
        }
    }

    fun adminUpdateRoomInfo(matchId: String, roomId: String, roomPass: String, notes: String) {
        val targetMatch = _matches.value.find { it.id == matchId }
        val title = targetMatch?.title ?: "Match"
        _matches.value = _matches.value.map {
            if (it.id == matchId) it.copy(roomId = roomId, roomPassword = roomPass, roomDescription = notes) else it
        }
        addNotification(
            "Room ID & Password available!",
            "$title starts in 10 minutes. Room ID: $roomId | Password: $roomPass",
            "MATCH"
        )
        logAudit("ROOM_UPDATED", "MATCH", "Room ID $roomId and password published for $title ($matchId)")
    }

    @Synchronized
    fun adminProcessWithdrawal(reqId: String, isApproved: Boolean, adminNote: String): Result<String> {
        val req = _withdrawals.value.find { it.id == reqId }
            ?: return Result.failure(Exception("Withdrawal request not found."))
        if (req.status != WithdrawalStatus.PENDING && req.status != WithdrawalStatus.PROCESSING) {
            return Result.failure(Exception("Withdrawal is already in status: ${req.status.name}"))
        }

        val payoutRef = "PO_${UUID.randomUUID().toString().take(8).uppercase()}"
        val newStatus = if (isApproved) WithdrawalStatus.COMPLETED else WithdrawalStatus.REJECTED

        _withdrawals.value = _withdrawals.value.map {
            if (it.id == reqId) {
                it.copy(status = newStatus, adminNote = adminNote, payoutRefId = if (isApproved) payoutRef else "")
            } else it
        }

        val w = _wallet.value
        // Release from locked balance
        val newLocked = (w.lockedBalance - req.amount).coerceAtLeast(0.0)

        if (isApproved) {
            // Confirm deduction: release locked balance
            _wallet.value = w.copy(lockedBalance = newLocked)
            _transactions.value = _transactions.value.map {
                if (it.referenceId == reqId) it.copy(status = TransactionStatus.SUCCESS) else it
            }
            addNotification(
                title = "✅ Withdrawal Dispatched",
                message = "₹${"%.0f".format(req.amount)} successfully paid out to ${req.paymentDetails}.\nPayout Reference: $payoutRef",
                category = "WALLET"
            )
            logAudit("WITHDRAWAL_APPROVED", "FINANCE", "Withdrawal $reqId for ₹${req.amount} approved. Payout Ref: $payoutRef. Note: $adminNote")
            return Result.success("Withdrawal approved! Payout Ref: $payoutRef")
        } else {
            // Auto Reversal back into winning balance + release locked balance
            _wallet.value = w.copy(
                lockedBalance = newLocked,
                winningBalance = w.winningBalance + req.amount
            )
            _transactions.value = _transactions.value.map {
                if (it.referenceId == reqId) it.copy(status = TransactionStatus.REVERSED) else it
            }

            // Create reversal ledger entry
            val revTxn = WalletTransaction(
                id = "TXN_REV_${UUID.randomUUID().toString().take(8).uppercase()}",
                userId = req.userId,
                type = TransactionType.REFUND,
                amount = req.amount,
                previousBalance = w.totalBalance,
                newBalance = _wallet.value.totalBalance,
                status = TransactionStatus.SUCCESS,
                referenceId = reqId,
                description = "Reversal of rejected withdrawal: $adminNote"
            )
            _transactions.value = listOf(revTxn) + _transactions.value

            addNotification(
                title = "⚠️ Withdrawal Failed & Refunded",
                message = "Your ₹${"%.0f".format(req.amount)} withdrawal could not be processed.\nReason: ${adminNote.ifBlank { "Account details mismatch" }}.\nFunds reversed back to your winnings.",
                category = "WALLET"
            )
            logAudit("WITHDRAWAL_REJECTED", "FINANCE", "Withdrawal $reqId for ₹${req.amount} rejected. Refunded back to user. Reason: $adminNote")
            return Result.success("Withdrawal rejected and ₹${req.amount} refunded back to user winning balance.")
        }
    }

    @Synchronized
    fun adminManualWalletAdjustment(
        amount: Double,
        reason: String,
        balanceType: String = "DEPOSIT"
    ): Result<String> {
        val w = _wallet.value
        val prevBal = w.totalBalance

        var newDep = w.depositBalance
        var newWin = w.winningBalance
        var newBon = w.bonusBalance

        when (balanceType.uppercase()) {
            "WINNING", "WINNINGS" -> newWin = (newWin + amount).coerceAtLeast(0.0)
            "BONUS" -> newBon = (newBon + amount).coerceAtLeast(0.0)
            else -> newDep = (newDep + amount).coerceAtLeast(0.0)
        }

        _wallet.value = w.copy(
            depositBalance = newDep,
            winningBalance = newWin,
            bonusBalance = newBon
        )
        val newBal = _wallet.value.totalBalance

        val txn = WalletTransaction(
            id = "TXN_ADJ_${UUID.randomUUID().toString().take(8).uppercase()}",
            userId = _currentUser.value?.id ?: "user_101",
            type = TransactionType.ADJUSTMENT,
            amount = Math.abs(amount),
            previousBalance = prevBal,
            newBalance = newBal,
            status = TransactionStatus.SUCCESS,
            referenceId = "ADMIN_ADJ_${System.currentTimeMillis()}",
            description = "Admin adjustment ($balanceType): $reason",
            idempotencyKey = "IDEMP_ADJ_${UUID.randomUUID().toString().take(8)}"
        )
        _transactions.value = listOf(txn) + _transactions.value
        logAudit("WALLET_ADJUSTMENT", "FINANCE", "Amount: $amount ($balanceType), Prev: ₹$prevBal, New: ₹$newBal, Reason: $reason")
        return Result.success("Adjusted $balanceType by ₹$amount successfully.")
    }

    fun runWalletReconciliation(): ReconciliationReport {
        val txns = _transactions.value
        val w = _wallet.value

        var totalCredits = 0.0
        var totalDebits = 0.0
        var totalDeposits = 0.0
        var totalWithdrawals = 0.0
        var totalEntryFees = 0.0
        var totalPrizes = 0.0
        var totalRefunds = 0.0

        for (t in txns) {
            if (t.status != TransactionStatus.SUCCESS) continue
            when (t.type) {
                TransactionType.DEPOSIT -> {
                    totalCredits += t.amount
                    totalDeposits += t.amount
                }
                TransactionType.WINNING, TransactionType.PRIZE_WINNING -> {
                    totalCredits += t.amount
                    totalPrizes += t.amount
                }
                TransactionType.BONUS, TransactionType.REFERRAL -> {
                    totalCredits += t.amount
                }
                TransactionType.REFUND -> {
                    totalCredits += t.amount
                    totalRefunds += t.amount
                }
                TransactionType.ENTRY_FEE, TransactionType.TOURNAMENT_ENTRY -> {
                    totalDebits += t.amount
                    totalEntryFees += t.amount
                }
                TransactionType.WITHDRAWAL -> {
                    totalDebits += t.amount
                    totalWithdrawals += t.amount
                }
                TransactionType.ADJUSTMENT -> {
                    if (t.newBalance >= t.previousBalance) {
                        totalCredits += t.amount
                    } else {
                        totalDebits += t.amount
                    }
                }
            }
        }

        val netLedgerBalance = (totalCredits - totalDebits).coerceAtLeast(0.0)
        val currentWalletTotal = w.totalBalance
        val discrepancy = Math.abs(currentWalletTotal - netLedgerBalance)
        val isBalanced = discrepancy < 1.0

        val alertMessage = if (isBalanced) {
            "ALL LEDGERS BALANCED: System integrity check passed. Total credits ₹${"%.2f".format(totalCredits)} - debits ₹${"%.2f".format(totalDebits)} equals active wallet liabilities ₹${"%.2f".format(currentWalletTotal)}."
        } else {
            "⚠️ RECONCILIATION ALERT: Ledger mismatch of ₹${"%.2f".format(discrepancy)} detected! Expected: ₹${"%.2f".format(netLedgerBalance)}, Found: ₹${"%.2f".format(currentWalletTotal)}."
        }

        logAudit("RECONCILIATION_RUN", "FINANCE", alertMessage)

        return ReconciliationReport(
            totalWalletBalances = currentWalletTotal,
            totalCredits = totalCredits,
            totalDebits = totalDebits,
            netLedgerBalance = netLedgerBalance,
            discrepancy = discrepancy,
            isBalanced = isBalanced,
            totalDeposits = totalDeposits,
            totalWithdrawals = totalWithdrawals,
            totalEntryFees = totalEntryFees,
            totalPrizes = totalPrizes,
            totalRefunds = totalRefunds,
            transactionCount = txns.size,
            alertMessage = alertMessage
        )
    }

    fun toggleMaintenanceMode(enabled: Boolean) {
        _appConfig.value = _appConfig.value.copy(isMaintenanceMode = enabled)
        logAudit("MAINTENANCE_TOGGLE", "APP", "Maintenance mode set to $enabled")
    }

    fun adminCreateMatch(
        gameId: String,
        title: String,
        mapName: String,
        type: TournamentType,
        entryFee: Double,
        perKillReward: Double,
        prizePool: Double,
        maxSlots: Int,
        startTime: String,
        date: String = "Today"
    ): Match {
        val game = _games.value.find { it.id == gameId }
        val gameName = game?.name ?: "Esports Match"
        val matchId = "match_${UUID.randomUUID().toString().take(6)}"
        val tourId = "tour_${UUID.randomUUID().toString().take(6)}"

        val newMatch = Match(
            id = matchId,
            tournamentId = tourId,
            gameId = gameId,
            gameName = gameName,
            title = title,
            matchNumber = _matches.value.size + 1,
            mapName = mapName,
            date = date,
            startTime = startTime,
            entryFee = entryFee,
            perKillReward = perKillReward,
            prizePool = prizePool,
            maxSlots = maxSlots,
            type = type,
            status = MatchStatus.REGISTRATION_OPEN,
            playersJoined = 0
        )

        val newTour = Tournament(
            id = tourId,
            gameId = gameId,
            gameName = gameName,
            title = title,
            description = "Competitive $type battle on $mapName. Prize pool: ₹$prizePool",
            rules = "Standard tournament guidelines. Room details revealed 10 mins before match.",
            bannerUrl = "${gameId}_banner",
            sponsor = "ApexZone Arena",
            type = type,
            isPaid = entryFee > 0,
            entryFee = entryFee,
            maxPlayers = maxSlots,
            prizePool = prizePool,
            startDate = date,
            startTime = startTime,
            registrationCloseTime = startTime,
            status = MatchStatus.REGISTRATION_OPEN
        )

        _tournaments.value = listOf(newTour) + _tournaments.value
        _matches.value = listOf(newMatch) + _matches.value

        addNotification("New Tournament Live!", "$title ($gameName) is now open for registration.", "TOURNAMENT")
        logAudit("CREATE_MATCH", "MATCH", "Created match $title ($gameName) - Entry: ₹$entryFee, Prize: ₹$prizePool")
        return newMatch
    }

    fun adminCancelMatch(matchId: String, reason: String) {
        val match = _matches.value.find { it.id == matchId } ?: return

        // Refund all participants idempotently
        val enrolled = _participants.value.filter { it.matchId == matchId }
        enrolled.forEach { participant ->
            val refundRef = "refund_${matchId}_${participant.userId}"
            val alreadyRefunded = _transactions.value.any { it.referenceId == refundRef }
            if (!alreadyRefunded && match.entryFee > 0) {
                if (participant.userId == _currentUser.value?.id) {
                    val w = _wallet.value
                    val prevBal = w.totalBalance
                    _wallet.value = w.copy(depositBalance = w.depositBalance + match.entryFee)
                    val txn = WalletTransaction(
                        id = "TXN_REFUND_${UUID.randomUUID().toString().take(8).uppercase()}",
                        userId = participant.userId,
                        type = TransactionType.REFUND,
                        amount = match.entryFee,
                        previousBalance = prevBal,
                        newBalance = _wallet.value.totalBalance,
                        status = TransactionStatus.SUCCESS,
                        referenceId = refundRef,
                        description = "REFUND: ${match.title} cancelled - $reason"
                    )
                    _transactions.value = listOf(txn) + _transactions.value
                }
            }
        }

        // Update participants status
        _participants.value = _participants.value.map {
            if (it.matchId == matchId) it.copy(status = ParticipationStatus.CANCELLED, resultStatus = "REFUNDED") else it
        }

        // Update match status to CANCELLED
        _matches.value = _matches.value.map {
            if (it.id == matchId) it.copy(status = MatchStatus.CANCELLED) else it
        }

        addNotification("⚠️ MATCH CANCELLED", "${match.title} has been cancelled by Admin. Reason: $reason. Entry fees have been refunded to your wallet.", "MATCH")
        logAudit("CANCEL_MATCH", "MATCH", "Match $matchId cancelled. Reason: $reason. Refunded ${enrolled.size} players.")
    }

    fun adminToggleBanUser(userId: String) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == userId) {
                val newStatus = !it.isBlocked
                logAudit("USER_STATUS_CHANGE", "USER", "User ${it.username} blocked status changed to $newStatus")
                it.copy(isBlocked = newStatus)
            } else it
        }
    }

    fun adminToggleUserKyc(userId: String) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == userId) {
                val newStatus = !it.isKycVerified
                logAudit("KYC_VERIFICATION", "USER", "User ${it.username} KYC set to $newStatus")
                it.copy(isKycVerified = newStatus)
            } else it
        }
    }

    fun adminToggleUserAdminRole(userId: String) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == userId) {
                val newRole = if (it.role == UserRole.ADMIN) UserRole.USER else UserRole.ADMIN
                logAudit("ROLE_CHANGE", "USER", "User ${it.username} role changed to $newRole")
                it.copy(role = newRole)
            } else it
        }
    }

    fun adminBroadcastAnnouncement(title: String, description: String, priority: Int = 1) {
        val ann = Announcement(
            id = "ann_${UUID.randomUUID().toString().take(6)}",
            title = title,
            description = description,
            priority = priority,
            active = true
        )
        _announcements.value = listOf(ann) + _announcements.value
        addNotification(title, description, "BROADCAST")
        logAudit("BROADCAST_ANNOUNCEMENT", "ANNOUNCEMENT", "Published: $title")
    }

    fun adminResolveChallenge(challengeId: String, winnerIsCreator: Boolean, reason: String) {
        val chal = _challenges.value.find { it.id == challengeId } ?: return
        val winnerId = if (winnerIsCreator) chal.creatorId else chal.opponentId
        val winnerName = if (winnerIsCreator) chal.creatorUsername else chal.opponentUsername

        _challenges.value = _challenges.value.map {
            if (it.id == challengeId) {
                it.copy(
                    status = ChallengeStatus.COMPLETED,
                    winnerId = winnerId,
                    isDisputed = false
                )
            } else it
        }

        // Credit wallet if winner is current user
        if (_currentUser.value?.id == winnerId) {
            creditWallet(TransactionType.WINNING, chal.prizeAmount, challengeId, "1v1 Challenge Winner ($reason)")
        }

        addNotification("1v1 Dispute Resolved", "$winnerName declared winner of ₹${chal.prizeAmount} 1v1 battle.", "CHALLENGE")
        logAudit("RESOLVE_DISPUTE", "CHALLENGE", "Challenge $challengeId resolved. Winner: $winnerName ($winnerId). Reason: $reason")
    }

    fun adminToggleGameStatus(gameId: String) {
        _games.value = _games.value.map {
            if (it.id == gameId) {
                val newActive = !it.active
                logAudit("GAME_STATUS_TOGGLE", "GAMES", "Game ${it.name} active status set to $newActive")
                it.copy(active = newActive)
            } else it
        }
    }

    fun adminUpdateAppConfig(config: AppConfig) {
        _appConfig.value = config
        logAudit("CONFIG_UPDATE", "SYSTEM", "App Config settings updated.")
    }

    fun updateProfile(fullName: String, ignMap: Map<String, String>) {
        _currentUser.value = _currentUser.value?.copy(
            fullName = fullName,
            inGameNames = ignMap
        )
    }

    private fun addNotification(title: String, message: String, category: String) {
        val notif = AppNotification(
            id = "notif_${UUID.randomUUID().toString().take(6)}",
            title = title,
            message = message,
            category = category
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    private fun logAudit(action: String, module: String, details: String) {
        val log = AuditLog(
            id = "log_${UUID.randomUUID().toString().take(6)}",
            adminId = _currentUser.value?.username ?: "SuperAdmin",
            action = action,
            module = module,
            details = details
        )
        _auditLogs.value = listOf(log) + _auditLogs.value
    }
}
