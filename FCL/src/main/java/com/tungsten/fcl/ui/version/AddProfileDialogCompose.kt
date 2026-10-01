package com.tungsten.fcl.ui.version

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme

class AddProfileDialogState {
    var name by mutableStateOf("")
    var path by mutableStateOf("")

    fun setPath(value: String) {
        path = value
    }
}

object AddProfileDialogCompose {
    @JvmStatic
    fun createView(
        context: Context,
        state: AddProfileDialogState,
        onChoosePath: Runnable,
        onCreateProfile: Runnable,
        onDismiss: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                AddProfileContent(state, onChoosePath::run, onCreateProfile::run, onDismiss::run)
            }
        }
    }
}

@Composable
private fun AddProfileContent(
    state: AddProfileDialogState,
    onChoosePath: () -> Unit,
    onCreateProfile: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = context.getString(R.string.version_new_profile),
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = state.name,
            onValueChange = { state.name = it },
            label = { Text(context.getString(R.string.profile_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = state.path.ifBlank { context.getString(R.string.profile_path) },
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            IconButton(onClick = onChoosePath) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_edit_24),
                    contentDescription = context.getString(R.string.profile_path)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onDismiss) {
                Text(context.getString(R.string.dialog_negative))
            }
            Button(onClick = onCreateProfile, shape = RoundedCornerShape(4.dp)) {
                Text(context.getString(R.string.dialog_positive))
            }
        }
    }
}
