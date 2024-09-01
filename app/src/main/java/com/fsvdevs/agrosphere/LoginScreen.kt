package com.fsvdevs.agrosphere

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fsvdevs.agrosphere.ui.theme.AgroSphereTheme
import com.fsvdevs.agrosphere.ui.theme.AppTypography

@Composable
fun loginScreen(navController: NavController) {
    AgroSphereTheme(isSystemInDarkTheme()) {
        val fontScale = LocalConfiguration.current.fontScale
        val paddingValue = if (fontScale > 1.2f) 16.dp else 8.dp

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var usernameError by remember { mutableStateOf<String?>(null) }
        var passwordError by remember { mutableStateOf<String?>(null) }

        var modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()

        Scaffold (
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(2.dp)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValue),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.agrosphere),
                                contentDescription = "AgroSphere Logo",
                                modifier = Modifier.size(108.dp)
                            )
                            Spacer(modifier = Modifier.padding(vertical = paddingValue))
                            // Title
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                                        append("AGRO")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        append("SPHERE")
                                    }
                                },
                                style = AppTypography.headlineLarge,
                                modifier = Modifier
                                    .padding(horizontal = paddingValue)
                            )
                            // Subtitle
                            Text(
                                text = stringResource(id = R.string.login_subtitle),
                                style = AppTypography.bodyLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .widthIn(max = 300.dp)
                                    .fillMaxWidth(0.8f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = paddingValue))
                    // Login Form
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = if (username.length > 20) {
                                "Username cannot exceed 20 characters"
                            } else null
                        },
                        label = { Text(stringResource(id = R.string.username_string)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp),
                        isError = usernameError != null,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Account circle icon"
                            )
                        },
                        trailingIcon = {
                            if (username.isNotEmpty()) {
                                IconButton(onClick = { username = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear username"
                                    )
                                }
                            }
                        },
                        supportingText = {
                            if (usernameError != null) {
                                Text(
                                    text = usernameError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = AppTypography.bodySmall
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.padding(vertical = paddingValue))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = if (password.length < 8 || password.length > 16) {
                                "Password must be between 8 and 16 characters"
                            } else null
                        },
                        label = { Text(stringResource(id = R.string.password_string)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp),
                        isError = passwordError != null,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock icon"
                            )
                        },
                        trailingIcon = {
                            if (password.isNotEmpty()) {
                                IconButton(onClick = { password = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear password"
                                    )
                                }
                            }
                        },
                        supportingText = {
                            if (passwordError != null) {
                                Text(
                                    text = passwordError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = AppTypography.bodySmall
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.padding(vertical = paddingValue))
                    // Buttons for Login and Sign Up
                    Row {
                        Button(onClick = {
                            navController.navigate(Routes.homeScreen)
                        }) {
                            Text(text = stringResource(id = R.string.log_in_string))
                        }
                        Spacer(modifier = Modifier.padding(horizontal = paddingValue))
                        OutlinedButton(onClick = {}) {
                            Text(text = stringResource(id = R.string.sign_up_string))
                        }
                    }
                }
                Text(
                    text = stringResource(id = R.string.login_display_dev_text),
                    style = AppTypography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = paddingValue)
                        .windowInsetsPadding(WindowInsets.ime)
                )
            }
        }
    }
}