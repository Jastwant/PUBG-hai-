package com.example.data.models

enum class UserRole {
    USER, ADMIN, MODERATOR
}

enum class TournamentType {
    SOLO, DUO, SQUAD, TEAM
}

enum class MatchStatus {
    DRAFT,
    UPCOMING,
    REGISTRATION_OPEN,
    REGISTRATION_CLOSED,
    ROOM_PREPARING,
    ROOM_PUBLISHED,
    READY,
    LIVE,
    RESULT_PENDING,
    RESULT_SUBMITTED,
    RESULT_UNDER_REVIEW,
    RESULT_VERIFIED,
    SETTLED,
    COMPLETED,
    FULL,
    CANCELLED,
    REFUND_PENDING,
    REFUNDED,
    ONGOING // Backward compatibility alias for LIVE
}

val MatchStatus.isLive: Boolean
    get() = this == MatchStatus.LIVE || this == MatchStatus.ONGOING

val MatchStatus.isRegistrationOpen: Boolean
    get() = this == MatchStatus.REGISTRATION_OPEN

val MatchStatus.isRoomAccessible: Boolean
    get() = this == MatchStatus.ROOM_PUBLISHED || this == MatchStatus.READY || this == MatchStatus.LIVE || this == MatchStatus.ONGOING

val MatchStatus.isCompleted: Boolean
    get() = this == MatchStatus.RESULT_VERIFIED || this == MatchStatus.SETTLED || this == MatchStatus.COMPLETED

val MatchStatus.isCancelled: Boolean
    get() = this == MatchStatus.CANCELLED || this == MatchStatus.REFUND_PENDING || this == MatchStatus.REFUNDED

val MatchStatus.canSubmitResult: Boolean
    get() = this == MatchStatus.LIVE || this == MatchStatus.ONGOING || this == MatchStatus.RESULT_PENDING || this == MatchStatus.RESULT_SUBMITTED

enum class ParticipationStatus {
    REGISTERED, JOINED, READY, LIVE, RESULT_PENDING, RESULT_SUBMITTED, COMPLETED, WON, LOST, CANCELLED, REFUNDED
}

enum class ChallengeStatus {
    OPEN, INVITED, ACCEPTED, ONGOING, RESULT_SUBMITTED, UNDER_REVIEW, COMPLETED, CANCELLED, DISPUTED
}

enum class TransactionType {
    DEPOSIT, ENTRY_FEE, TOURNAMENT_ENTRY, WINNING, PRIZE_WINNING, REFUND, BONUS, REFERRAL, WITHDRAWAL, ADJUSTMENT
}

enum class TransactionStatus {
    PENDING, SUCCESS, FAILED, CANCELLED, REJECTED, REVERSED
}

enum class WithdrawalStatus {
    PENDING, PROCESSING, APPROVED, REJECTED, COMPLETED
}

enum class AccountStatus {
    ACTIVE, PENDING_VERIFICATION, RESTRICTED, SUSPENDED, BANNED, DEACTIVATED, DELETED
}

enum class SecurityDecisionAction {
    ALLOW, ALLOW_WITH_RESTRICTIONS, REQUIRE_RECHECK, BLOCK_TEMPORARILY, BLOCK_ACTION, SUSPEND_FOR_REVIEW, DISABLE_DEVELOPER_OPTIONS, FAILED_INTEGRITY
}

data class SecurityDecision(
    val allowed: Boolean,
    val action: SecurityDecisionAction,
    val reason: String? = null,
    val message: String? = null
)

enum class DeviceStatus {
    ACTIVE, REVOKED, BLOCKED, SUSPICIOUS
}

enum class RiskLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class UserDevice(
    val deviceId: String,
    val userId: String,
    val installationId: String,
    val platform: String = "Android",
    val manufacturer: String,
    val model: String,
    val osVersion: String,
    val appVersion: String = "1.0.0",
    val firstSeenAt: Long = System.currentTimeMillis(),
    val lastSeenAt: Long = System.currentTimeMillis(),
    val status: DeviceStatus = DeviceStatus.ACTIVE,
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val developerOptionsEnabled: Boolean = false,
    val usbDebuggingEnabled: Boolean = false,
    val isCurrentDevice: Boolean = false
)

enum class SessionStatus {
    ACTIVE, REVOKED, EXPIRED, SUSPENDED
}

data class UserSession(
    val sessionId: String,
    val userId: String,
    val deviceId: String,
    val installationId: String,
    val platform: String = "Android",
    val deviceName: String,
    val appVersion: String = "1.0.0",
    val osVersion: String,
    val ipHash: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSeenAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000,
    val revokedAt: Long? = null,
    val status: SessionStatus = SessionStatus.ACTIVE,
    val isCurrentSession: Boolean = false
)

enum class GameVerificationStatus {
    PENDING, VERIFIED, REJECTED
}

data class UserGameProfile(
    val id: String,
    val userId: String,
    val gameId: String,
    val gameName: String,
    val playerUid: String,
    val playerName: String,
    val region: String = "India / South Asia",
    val platform: String = "Mobile",
    val verificationStatus: GameVerificationStatus = GameVerificationStatus.VERIFIED,
    val verifiedAt: Long = System.currentTimeMillis(),
    val lastVerifiedAt: Long = System.currentTimeMillis()
)

enum class OtpPurpose {
    REGISTER, LOGIN, PHONE_CHANGE, PASSWORD_RESET
}

data class OtpChallenge(
    val challengeId: String,
    val identifier: String, // Normalized phone or email
    val otpHash: String,
    val displayCode: String, // Exposed in test/sandbox environment
    val purpose: OtpPurpose,
    val attemptCount: Int = 0,
    val maxAttempts: Int = 5,
    val expiresAt: Long,
    val createdAt: Long = System.currentTimeMillis()
)

enum class SecurityEventType {
    DEVELOPER_OPTIONS_DETECTED,
    USB_DEBUGGING_DETECTED,
    MULTIPLE_ACCOUNT_ATTEMPT,
    INTEGRITY_VERDICT_RECEIVED,
    RAPID_ACCOUNT_SWITCH,
    INVALID_OTP,
    DEVICE_LINKED,
    SESSION_REVOKED,
    ACCOUNT_RESTRICTED,
    SUSPICIOUS_NETWORK
}

data class SecurityEvent(
    val eventId: String,
    val userId: String?,
    val deviceId: String,
    val eventType: SecurityEventType,
    val severity: RiskLevel,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String,
    val actionTaken: String
)

data class PlayIntegrityVerdict(
    val appRecognitionVerdict: String = "PLAY_RECOGNIZED",
    val appLicensingVerdict: String = "LICENSED",
    val deviceRecognitionVerdict: List<String> = listOf("MEETS_DEVICE_INTEGRITY", "MEETS_BASIC_INTEGRITY"),
    val appAccessRisk: String = "NO_RISK",
    val isPass: Boolean = true,
    val evaluatedAt: Long = System.currentTimeMillis()
)

data class User(
    val id: String,
    val fullName: String,
    val username: String,
    val email: String,
    val mobileNumber: String,
    val profilePicUrl: String = "",
    val inGameNames: Map<String, String> = emptyMap(), // gameId -> ign
    val referralCode: String = "",
    val referredBy: String = "",
    val role: UserRole = UserRole.USER,
    val isKycVerified: Boolean = false,
    val isBlocked: Boolean = false,
    val status: AccountStatus = AccountStatus.ACTIVE,
    val phoneNormalized: String = "",
    val emailNormalized: String = "",
    val usernameNormalized: String = "",
    val linkedDeviceId: String = "",
    val installationId: String = "",
    val profileCompletion: Int = 85,
    val lastLoginAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Wallet(
    val userId: String,
    val depositBalance: Double = 0.0,
    val winningBalance: Double = 0.0,
    val bonusBalance: Double = 0.0,
    val lockedBalance: Double = 0.0,
    val points: Int = 0
) {
    val availableBalance: Double get() = (depositBalance + winningBalance + bonusBalance - lockedBalance).coerceAtLeast(0.0)
    val totalBalance: Double get() = depositBalance + winningBalance + bonusBalance
    val withdrawableBalance: Double get() = (winningBalance - lockedBalance).coerceAtLeast(0.0)
}

data class WalletTransaction(
    val id: String,
    val userId: String,
    val type: TransactionType,
    val amount: Double,
    val previousBalance: Double,
    val newBalance: Double,
    val status: TransactionStatus,
    val referenceId: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val paymentMethod: String = "",
    val idempotencyKey: String = "",
    val orderId: String = ""
)

data class WithdrawalRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val amount: Double,
    val method: String, // UPI, Bank
    val paymentDetails: String, // upi id or account info
    val status: WithdrawalStatus,
    val requestDate: Long = System.currentTimeMillis(),
    val adminNote: String = "",
    val payoutRefId: String = "",
    val idempotencyKey: String = ""
)

data class PaymentOrder(
    val orderId: String,
    val userId: String,
    val amount: Double,
    val gateway: String,
    val paymentMethod: String,
    val status: TransactionStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val idempotencyKey: String = ""
)

data class ReconciliationReport(
    val timestamp: Long = System.currentTimeMillis(),
    val totalWalletBalances: Double,
    val totalCredits: Double,
    val totalDebits: Double,
    val netLedgerBalance: Double,
    val discrepancy: Double,
    val isBalanced: Boolean,
    val totalDeposits: Double,
    val totalWithdrawals: Double,
    val totalEntryFees: Double,
    val totalPrizes: Double,
    val totalRefunds: Double,
    val transactionCount: Int,
    val alertMessage: String
)

data class Game(
    val id: String,
    val name: String,
    val iconName: String,
    val bannerUrl: String,
    val description: String,
    val rules: String,
    val active: Boolean = true,
    val comingSoon: Boolean = false,
    val sortOrder: Int = 0,
    val platformSupported: String = "Mobile"
)

data class Tournament(
    val id: String,
    val gameId: String,
    val gameName: String,
    val title: String,
    val description: String,
    val rules: String,
    val bannerUrl: String,
    val sponsor: String,
    val type: TournamentType,
    val isPaid: Boolean,
    val entryFee: Double,
    val maxPlayers: Int,
    val prizePool: Double,
    val isPercentagePrize: Boolean = false,
    val prizeDistribution: List<PrizeRank> = emptyList(),
    val startDate: String,
    val startTime: String,
    val registrationCloseTime: String,
    val status: MatchStatus = MatchStatus.REGISTRATION_OPEN,
    val isPinned: Boolean = false,
    val matchesCount: Int = 1
)

data class PrizeRank(
    val rankLabel: String,
    val rewardAmount: Double,
    val percentage: Double = 0.0
)

data class Match(
    val id: String,
    val tournamentId: String,
    val gameId: String,
    val gameName: String,
    val title: String,
    val matchNumber: Int,
    val mapName: String,
    val date: String,
    val startTime: String,
    val entryFee: Double,
    val perKillReward: Double,
    val prizePool: Double,
    val maxSlots: Int,
    val type: TournamentType,
    val status: MatchStatus,
    val roomId: String = "",
    val roomPassword: String = "",
    val roomDescription: String = "",
    val roomReleaseMinutesBefore: Int = 10,
    val spectateUrl: String = "",
    val isPinned: Boolean = false,
    val playersJoined: Int = 0,
    val startEpochMillis: Long = System.currentTimeMillis() + 30 * 60 * 1000,
    val serverTimestamp: Long = System.currentTimeMillis(),
    val lateEntryAllowed: Boolean = true,
    val lateEntryCutoffMinutes: Int = 10,
    val resultMethod: String = "ADMIN"
)

data class MatchParticipant(
    val id: String,
    val matchId: String,
    val tournamentId: String = "",
    val userId: String,
    val username: String,
    val inGameName: String,
    val teamNumber: Int = 1,
    val slotNumber: Int = 1,
    val status: ParticipationStatus = ParticipationStatus.JOINED,
    val entryFee: Double = 0.0,
    val joinedAt: Long = System.currentTimeMillis(),
    val resultStatus: String = "PENDING",
    val rank: Int = 0,
    val kills: Int = 0,
    val points: Int = 0,
    val prizeAmount: Double = 0.0,
    val roomAccess: Boolean = true,
    val screenshotProofUrl: String = "",
    val submissionNotes: String = ""
)

data class ResultSubmission(
    val id: String,
    val matchId: String,
    val userId: String,
    val username: String,
    val inGameName: String,
    val rankClaimed: Int,
    val killsClaimed: Int,
    val screenshotUrl: String,
    val notes: String = "",
    val status: String = "UNDER_REVIEW", // UNDER_REVIEW, APPROVED, REJECTED
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedBy: String = "",
    val reviewedAt: Long = 0L
)

data class JoinResult(
    val success: Boolean,
    val slotNumber: Int = 0,
    val matchTitle: String = "",
    val entryFee: Double = 0.0,
    val startTime: String = "",
    val newBalance: Double = 0.0,
    val message: String = "",
    val errorMessage: String? = null
)

data class MatchResult(
    val id: String,
    val matchId: String,
    val winnerUsername: String,
    val winnerInGameName: String,
    val rank: Int,
    val kills: Int,
    val killPoints: Double,
    val placePoints: Double,
    val totalPrizeWon: Double,
    val proofImageUrl: String = "",
    val declaredAt: Long = System.currentTimeMillis()
)

data class PvPChallenge(
    val id: String,
    val gameId: String,
    val gameName: String,
    val creatorId: String,
    val creatorUsername: String,
    val creatorInGameName: String,
    val opponentId: String = "",
    val opponentUsername: String = "",
    val opponentInGameName: String = "",
    val entryAmount: Double,
    val prizeAmount: Double,
    val status: ChallengeStatus,
    val roomCode: String = "",
    val creatorProofUrl: String = "",
    val opponentProofUrl: String = "",
    val winnerId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isDisputed: Boolean = false
)

data class ChallengeMessage(
    val id: String,
    val challengeId: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val isSystem: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val username: String,
    val inGameName: String,
    val matchesPlayed: Int,
    val wins: Int,
    val kills: Int,
    val points: Int,
    val totalEarnings: Double,
    val avatarUrl: String = ""
)

data class PlayerStats(
    val userId: String,
    val totalMatches: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val winRatePercent: Double = 0.0,
    val totalKills: Int = 0,
    val kdRatio: Double = 0.0,
    val totalPoints: Int = 0,
    val totalWinnings: Double = 0.0,
    val gameWiseStats: Map<String, Int> = emptyMap()
)

data class ShopProduct(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val priceReal: Double,
    val pricePoints: Int,
    val inStock: Int,
    val imageUrl: String,
    val isHot: Boolean = false
)

data class ShopOrder(
    val orderId: String,
    val userId: String,
    val productName: String,
    val amountPaid: Double,
    val pointsPaid: Int,
    val status: String,
    val date: Long = System.currentTimeMillis()
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class Announcement(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String = "",
    val linkUrl: String = "",
    val active: Boolean = true,
    val priority: Int = 1
)

data class Offer(
    val id: String,
    val title: String,
    val description: String,
    val rewardCash: Double,
    val rewardPoints: Int,
    val actionUrl: String,
    val isCompleted: Boolean = false
)

data class AuditLog(
    val id: String,
    val adminId: String,
    val action: String,
    val module: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AppConfig(
    val appName: String = "ApexZone",
    val minVersionCode: Int = 1,
    val currentVersionCode: Int = 1,
    val versionName: String = "1.0.0",
    val isMaintenanceMode: Boolean = false,
    val maintenanceMessage: String = "We are upgrading servers for better tournament performance.",
    val currencySymbol: String = "₹",
    val minWithdrawalAmount: Double = 100.0,
    val referralRewardDeposit: Double = 25.0,
    val watchEarnRewardCash: Double = 2.0,
    val watchEarnDailyLimit: Int = 10,
    val contactEmail: String = "support@apexzone-esports.com",
    val contactTelegram: String = "https://t.me/apexzone_esports",
    val youtubeStreamUrl: String = "https://youtube.com/live/apexzone_tournaments"
)
