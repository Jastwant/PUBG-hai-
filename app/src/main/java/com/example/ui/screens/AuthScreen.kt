package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.OtpChallenge
import com.example.data.models.OtpPurpose
import com.example.data.repository.EsportsRepository
import com.example.security.DeviceSecurityManager
import com.example.ui.components.NeonGradientButton
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    var isRegisterMode by remember { mutableStateOf(false) }
    var isOtpLoginMode by remember { mutableStateOf(false) }
    var isForgotMode by remember { mutableStateOf(false) }

    // Form inputs
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var mobileInput by remember { mutableStateOf("") }
    var loginIdentifier by remember { mutableStateOf("pro.striker@apexzone.gg") }
    var password by remember { mutableStateOf("apex12345") }
    var referralCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // OTP verification dialog state
    var showOtpDialog by remember { mutableStateOf(false) }
    var currentChallenge by remember { mutableStateOf<OtpChallenge?>(null) }
    var enteredOtpCode by remember { mutableStateOf("") }
    var otpResendCountdown by remember { mutableStateOf(0) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Countdown timer for OTP resend
    LaunchedEffect(otpResendCountdown) {
        if (otpResendCountdown > 0) {
            delay(1000)
            otpResendCountdown--
        }
    }

    val instId = remember { DeviceSecurityManager.getInstallationId(context) }

    Scaffold(
        containerColor = DarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Logo & Security Header
            Surface(
                shape = CircleShape,
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(2.dp, CyberCyan),
                modifier = Modifier.size(68.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Logo",
                        tint = CyberCyan,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = when {
                    isForgotMode -> "ACCOUNT RECOVERY"
                    isRegisterMode -> "CREATE PRO GAMER ACCOUNT"
                    isOtpLoginMode -> "INSTANT OTP SIGN IN"
                    else -> "WELCOME BACK"
                },
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Text(
                text = when {
                    isForgotMode -> "Verify via registered mobile/email to reset your credentials."
                    isRegisterMode -> "1 Account per Mobile & Device • Verified Esports Arena"
                    isOtpLoginMode -> "Secure passwordless login with one-time verification code."
                    else -> "Sign in to enter tournament lobbies and manage your winnings."
                },
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Feedback banners
            if (errorMessage != null) {
                Surface(
                    color = CrimsonRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = CrimsonRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (successMessage != null) {
                Surface(
                    color = EmeraldGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = successMessage ?: "",
                            color = EmeraldGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Forms
            if (isForgotMode) {
                OutlinedTextField(
                    value = loginIdentifier,
                    onValueChange = { loginIdentifier = it },
                    label = { Text("Registered Email or Mobile") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CyberCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
                NeonGradientButton(
                    text = "REQUEST RESET OTP",
                    onClick = {
                        errorMessage = null
                        val res = EsportsRepository.requestOtp(loginIdentifier, OtpPurpose.PASSWORD_RESET)
                        if (res.isSuccess) {
                            currentChallenge = res.getOrNull()
                            showOtpDialog = true
                            otpResendCountdown = 60
                        } else {
                            errorMessage = res.exceptionOrNull()?.message
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                TextButton(onClick = { isForgotMode = false }) {
                    Text("Back to Sign In", color = CyberCyan, fontWeight = FontWeight.Bold)
                }
            } else if (isRegisterMode) {
                // REGISTRATION FORM
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyberCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Gamer Username (Unique Tag)") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = CyberCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = mobileInput,
                    onValueChange = { mobileInput = it },
                    label = { Text("Mobile Number (10 Digits)") },
                    placeholder = { Text("9876543210") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CyberCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("gamer@domain.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyberCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Create Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = referralCode,
                    onValueChange = { referralCode = it },
                    label = { Text("Referral Code (Optional - get ₹25 bonus)") },
                    leadingIcon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = CyberGold) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                NeonGradientButton(
                    text = "VERIFY MOBILE & COMPLETE REGISTRATION",
                    onClick = {
                        errorMessage = null
                        if (fullName.isBlank() || username.isBlank() || mobileInput.isBlank() || emailInput.isBlank()) {
                            errorMessage = "All fields (Full Name, Username, Mobile, Email) are mandatory."
                            return@NeonGradientButton
                        }
                        if (mobileInput.filter { it.isDigit() }.length < 10) {
                            errorMessage = "Please enter a valid 10-digit Indian mobile number."
                            return@NeonGradientButton
                        }
                        if (!emailInput.contains("@") || !emailInput.contains(".")) {
                            errorMessage = "Please enter a valid email address."
                            return@NeonGradientButton
                        }

                        // Send OTP
                        val req = EsportsRepository.requestOtp(mobileInput, OtpPurpose.REGISTER)
                        if (req.isSuccess) {
                            currentChallenge = req.getOrNull()
                            showOtpDialog = true
                            otpResendCountdown = 60
                        } else {
                            errorMessage = req.exceptionOrNull()?.message
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.clickable { isRegisterMode = false },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already registered? ", color = TextSecondary, fontSize = 14.sp)
                    Text("Sign In", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            } else if (isOtpLoginMode) {
                // INSTANT OTP LOGIN FORM
                OutlinedTextField(
                    value = loginIdentifier,
                    onValueChange = { loginIdentifier = it },
                    label = { Text("Mobile Number or Registered Email") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CyberCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                NeonGradientButton(
                    text = "SEND 6-DIGIT OTP",
                    onClick = {
                        errorMessage = null
                        val req = EsportsRepository.requestOtp(loginIdentifier, OtpPurpose.LOGIN)
                        if (req.isSuccess) {
                            currentChallenge = req.getOrNull()
                            showOtpDialog = true
                            otpResendCountdown = 60
                        } else {
                            errorMessage = req.exceptionOrNull()?.message
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { isOtpLoginMode = false }) {
                    Text("Use Password Login Instead", color = CyberCyan, fontSize = 13.sp)
                }
            } else {
                // PASSWORD LOGIN FORM
                OutlinedTextField(
                    value = loginIdentifier,
                    onValueChange = { loginIdentifier = it },
                    label = { Text("Email or Mobile Number") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyberCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { isOtpLoginMode = true }) {
                        Text("Sign In with OTP", color = CyberCyan, fontSize = 12.sp)
                    }
                    TextButton(onClick = { isForgotMode = true }) {
                        Text("Forgot Password?", color = TextSecondary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                NeonGradientButton(
                    text = "ENTER TOURNAMENT ARENA",
                    onClick = {
                        errorMessage = null
                        val loginResult = EsportsRepository.login(loginIdentifier, password, instId)
                        if (loginResult.isSuccess) {
                            Toast.makeText(context, "Welcome back, ${loginResult.getOrNull()?.username}!", Toast.LENGTH_SHORT).show()
                            onAuthSuccess()
                        } else {
                            errorMessage = loginResult.exceptionOrNull()?.message
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Social / Quick Account Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = DarkBorder)
                    Text(" QUICK DEMO PLAYERS ", color = TextMuted, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = DarkBorder)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val r = EsportsRepository.login("pro.striker@apexzone.gg", "apex12345", instId)
                            if (r.isSuccess) onAuthSuccess()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkSurface)
                    ) {
                        Text("ApexStriker", fontSize = 11.sp, color = CyberCyan)
                    }
                    OutlinedButton(
                        onClick = {
                            val r = EsportsRepository.login("viper.pro@gmail.com", "pass123", instId)
                            if (r.isSuccess) onAuthSuccess()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkSurface)
                    ) {
                        Text("Viper_Pro", fontSize = 11.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.clickable { isRegisterMode = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Don't have an account? ", color = TextSecondary, fontSize = 14.sp)
                    Text("Register Now", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Security & Single Account Policy Footer Badge
            Surface(
                color = DarkElevated,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ANTI-CHEAT & SINGLE DEVICE POLICY ACTIVE",
                            color = EmeraldGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "1 Mobile • 1 Account • Hardware Integrity Verified",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // OTP Dialog
        if (showOtpDialog && currentChallenge != null) {
            val challenge = currentChallenge!!
            AlertDialog(
                onDismissRequest = { showOtpDialog = false },
                containerColor = DarkCard,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Security OTP Verification", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                },
                text = {
                    Column {
                        Text(
                            "A 6-digit one-time code was generated for ${challenge.identifier}:",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Sandbox simulation box
                        Surface(
                            color = CyberCyan.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "Sandbox Dispatch Code: ${challenge.displayCode}",
                                    color = CyberCyan,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Hash: ${challenge.otpHash.take(22)}... (Expires in 5m)",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = enteredOtpCode,
                            onValueChange = { enteredOtpCode = it.take(6) },
                            label = { Text("Enter 6-Digit Code") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (otpResendCountdown > 0) "Resend in ${otpResendCountdown}s" else "Didn't receive code?",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            if (otpResendCountdown == 0) {
                                TextButton(
                                    onClick = {
                                        val req = EsportsRepository.requestOtp(challenge.identifier, challenge.purpose)
                                        if (req.isSuccess) {
                                            currentChallenge = req.getOrNull()
                                            otpResendCountdown = 60
                                        } else {
                                            errorMessage = req.exceptionOrNull()?.message
                                        }
                                    }
                                ) {
                                    Text("Resend Code", color = CyberCyan, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val verifyRes = EsportsRepository.verifyOtp(challenge.challengeId, enteredOtpCode)
                            if (verifyRes.isSuccess) {
                                showOtpDialog = false
                                enteredOtpCode = ""

                                if (isRegisterMode) {
                                    val regRes = EsportsRepository.register(
                                        fullName = fullName,
                                        username = username,
                                        mobile = mobileInput,
                                        email = emailInput,
                                        refCode = referralCode,
                                        installationId = instId
                                    )
                                    if (regRes.isSuccess) {
                                        Toast.makeText(context, "Account verified and created successfully!", Toast.LENGTH_SHORT).show()
                                        onAuthSuccess()
                                    } else {
                                        errorMessage = regRes.exceptionOrNull()?.message
                                    }
                                } else if (isOtpLoginMode) {
                                    val logRes = EsportsRepository.login(challenge.identifier, "otp_verified", instId)
                                    if (logRes.isSuccess) {
                                        Toast.makeText(context, "OTP verified. Logged in!", Toast.LENGTH_SHORT).show()
                                        onAuthSuccess()
                                    } else {
                                        errorMessage = logRes.exceptionOrNull()?.message
                                    }
                                } else {
                                    successMessage = "Password reset verified. You can now login."
                                    isForgotMode = false
                                }
                            } else {
                                errorMessage = verifyRes.exceptionOrNull()?.message
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("VERIFY CODE", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showOtpDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}
