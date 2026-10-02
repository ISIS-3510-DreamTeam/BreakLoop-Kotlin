package com.dreamteam.breakloop.ui.account.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamteam.breakloop.R
import com.dreamteam.breakloop.ui.components.BreakLoopButton
import com.dreamteam.breakloop.ui.components.BreakLoopCard
import com.dreamteam.breakloop.ui.components.BreakLoopTextField
import com.dreamteam.breakloop.ui.components.MonoTitle
import com.dreamteam.breakloop.ui.theme.BreakLoopTheme
import com.dreamteam.breakloop.ui.theme.ColorPalette

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AuthContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onToggleMode = viewModel::toggleMode,
        onSubmit = viewModel::submit,
        onShowRecover = viewModel::showRecover,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onShowLogin = viewModel::showLogin,
        modifier = modifier,

    )
}

@Composable
fun AuthContent(state: AuthUiState, onEmailChange: (String) -> Unit,
                onPasswordChange: (String) -> Unit, onToggleMode: () -> Unit,
                onSubmit: () -> Unit, onShowRecover: () -> Unit,
                onConfirmPasswordChange: (String) -> Unit, onShowLogin: () -> Unit,
                modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorPalette.Neutral.Snow)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.breakloop_logo),
            contentDescription = "BreakLoop logo",
            modifier = Modifier.size(240.dp),
        )
        val title = when(state.mode) {
            AuthMode.SIGNUP -> "SIGN UP"
            AuthMode.LOGIN -> "LOG IN"
            AuthMode.RECOVER -> "RECOVER PASSWORD"
        }
        Text(title, style = MonoTitle, color = ColorPalette.Neutral.t1000)
        Spacer(Modifier.height(16.dp))
        BreakLoopCard {
            if (state.mode == AuthMode.RECOVER && state.resetEmailSent) {
                Text("CHECK YOUR EMAIL", style = MonoTitle, color = ColorPalette.Neutral.t1000)
                Text("If an account exists for that email, we've sent a link to reset your password.")
            } else {
                BreakLoopTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = "E-mail",
                    placeholder = "you@mail.com",
                    error = state.emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )

                    if (state.mode != AuthMode.RECOVER) {
                        BreakLoopTextField(
                            value = state.password,
                            onValueChange = onPasswordChange,
                            label = "Password",
                            placeholder = "Password",
                            error = state.passwordError,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        )
                    }

                    if (state.mode == AuthMode.LOGIN) {
                        TextButton(onClick = onShowRecover, enabled = !state.isLoading) {
                            val toggleText = "Forgot your password?"
                            Text(toggleText)
                        }
                    }

                    if (state.mode == AuthMode.SIGNUP) {
                        BreakLoopTextField(
                            value = state.confirmPassword,
                            onValueChange = onConfirmPasswordChange,
                            label = "Confirm Password",
                            placeholder = "Password",
                            error = state.confirmPasswordError,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        )
                    }

                    //error msg
                    state.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    var buttonEnabled = false
                    if ((state.mode == AuthMode.LOGIN) || (state.mode == AuthMode.SIGNUP)) {
                        buttonEnabled = !state.isLoading &&
                                state.emailError == null &&
                                state.passwordError == null &&
                                state.email.isNotBlank() &&
                                state.password.isNotBlank()
                    } else {
                        buttonEnabled = !state.isLoading && state.emailError == null && state.email.isNotBlank()
                    }

                    Spacer(Modifier.height(16.dp))

                val buttonText = when (state.mode) {
                    AuthMode.LOGIN -> "Enter"
                    AuthMode.SIGNUP -> "Register"
                    AuthMode.RECOVER -> "Recover Password"
                }

                BreakLoopButton(text = buttonText, onClick = onSubmit, loading = state.isLoading)
                }
            }
        if (state.mode != AuthMode.RECOVER){
        TextButton(onClick = onToggleMode, enabled = !state.isLoading) {
            val toggleText = when (state.mode) {
                AuthMode.LOGIN -> "Don't have an account? Sign Up"
                AuthMode.SIGNUP -> "Already have an account? Login"
                AuthMode.RECOVER -> ""
            }
            Text(toggleText)
        }
        } else {
            TextButton(onClick = onShowLogin, enabled = !state.isLoading) {
                val toggleText = "Back to Login"
                Text(toggleText)
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun AuthEmptyPreview() {
    BreakLoopTheme {
        AuthContent(
            state = AuthUiState(),
            onEmailChange = {}, onPasswordChange = {},
            onSubmit = {}, onToggleMode = {},
            onShowRecover = {}, onConfirmPasswordChange = {},
            onShowLogin = {},
        )
    }
}