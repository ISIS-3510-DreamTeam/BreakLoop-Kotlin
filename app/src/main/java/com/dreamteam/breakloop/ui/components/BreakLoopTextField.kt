package com.dreamteam.breakloop.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamteam.breakloop.ui.theme.ColorPalette
import com.dreamteam.breakloop.ui.theme.SpaceMono
import androidx.compose.ui.text.TextStyle


val MonoLabel = TextStyle(
    fontFamily = SpaceMono,
    fontWeight = FontWeight.Bold,
    fontSize = 13.sp,
    letterSpacing = 1.sp,
)

val MonoTitle = TextStyle(
    fontFamily = SpaceMono,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    letterSpacing = 1.sp,
)

@Composable
fun BreakLoopTextField(
    value: String,                          // text
    onValueChange: (String) -> Unit,        // on change
    label: String,                          // specific fild
    modifier: Modifier = Modifier,
    placeholder: String = "",               // grey text
    error: String? = null,                  // errors
    leadingIcon: @Composable (() -> Unit)? = null,   // left icon
    trailingIcon: @Composable (() -> Unit)? = null,  // right icon (see password)
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Column(modifier) {
        Text(
            text = label.uppercase(),
            style = MonoLabel,
            color = ColorPalette.Neutral.t1000,
        )
        Spacer(Modifier.height(6.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = ColorPalette.Neutral.t500) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = error != null,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                // fondo
                focusedContainerColor = ColorPalette.SpicyPaprika.t100,
                unfocusedContainerColor = ColorPalette.SpicyPaprika.t100,
                errorContainerColor = ColorPalette.SpicyPaprika.t100,
                // no sombra
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        // error
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
            )
        }
    }
}