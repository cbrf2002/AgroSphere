package com.fsvdevs.agrosphere.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fsvdevs.agrosphere.R
import com.fsvdevs.agrosphere.util.TextLogo

@Composable
fun LoginScreen(
    onGoogleSignIn: () -> Unit,
    onEmailSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit,
    onForgotPassword: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailSignInVisible by remember { mutableStateOf(false) }
    var isSignUpVisible by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val passwordFocusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 64.dp)
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.agrosphere),
                    modifier = Modifier.height(100.dp),
                    contentDescription = "AgroSphere Logo"
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextLogo()

                Text(
                    text = "IoT-Based Automated Greenhouse\nMonitoring System",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .imePadding()
            ) {
                Spacer(Modifier.fillMaxWidth().weight(.2f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isEmailSignInVisible && !isSignUpVisible) {
                        OutlinedButton(
                            onClick = onGoogleSignIn,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row {
                                Image(
                                    painter = painterResource(id = R.drawable.google_logo),
                                    contentDescription = "Google Logo",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Sign in with Google")
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { isEmailSignInVisible = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sign in with Email")
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        TextButton(
                            onClick = { isSignUpVisible = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Create Account")
                        }
                    }

                    if (isEmailSignInVisible || isSignUpVisible) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            placeholder = { Text("Enter your email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(FocusRequester()),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { passwordFocusRequester.requestFocus() }
                            ),
                            trailingIcon = {
                                if (email.isNotBlank()) {
                                    IconButton(onClick = { email = "" }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_clear_24),
                                            contentDescription = "Clear email"
                                        )
                                    }
                                }
                            },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            placeholder = { Text("Enter your password") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible)
                                    painterResource(id = R.drawable.rounded_visibility_24)
                                else
                                    painterResource(id = R.drawable.rounded_visibility_off_24)

                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        painter = image,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(passwordFocusRequester),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        TextButton(
                            onClick = {
                                isEmailSignInVisible = false
                                isSignUpVisible = false
                                email = ""
                                password = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back")
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (isEmailSignInVisible) {
                            Button(
                                onClick = { onEmailSignIn(email, password) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sign in with Email")
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            TextButton(
                                onClick = { onForgotPassword(email) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Forgot Password?")
                            }
                        }

                        if (isSignUpVisible) {
                            Button(
                                onClick = { onSignUp(email, password) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sign Up")
                            }
                        }
                    }
                }
                Spacer(Modifier.fillMaxWidth().weight(.2f))
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "A project developed by Fabian, Sumalinog, and Vicente\nNU Dasmariñas",
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "By signing up, you agree to our Terms of Service and Privacy Policy",
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onGoogleSignIn = {},
        onEmailSignIn = { _, _ -> },
        onSignUp = { _, _ -> },
        onForgotPassword = {}
    )
}