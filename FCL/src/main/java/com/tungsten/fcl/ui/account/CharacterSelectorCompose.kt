package com.tungsten.fcl.ui.account

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.tungsten.fcl.R
import com.tungsten.fcl.game.TexturesLoader
import com.tungsten.fclcore.auth.yggdrasil.GameProfile
import com.tungsten.fclcore.auth.yggdrasil.YggdrasilService
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import com.tungsten.fcllibrary.component.view.FCLImageView
import java.util.function.Consumer

class CharacterSelectorComposeState {
    var profiles by mutableStateOf<List<GameProfile>>(emptyList())
        private set
    var service: YggdrasilService? = null
        private set

    fun setProfiles(service: YggdrasilService, profiles: List<GameProfile>) {
        this.service = service
        this.profiles = profiles.toList()
    }
}

object CharacterSelectorCompose {
    @JvmStatic
    fun createView(
        context: Context,
        state: CharacterSelectorComposeState,
        onSelect: Consumer<GameProfile>,
        onCancel: Runnable
    ): ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            FCLComposeTheme {
                CharacterSelectorContent(state, onSelect, onCancel)
            }
        }
    }
}

@Composable
private fun CharacterSelectorContent(
    state: CharacterSelectorComposeState,
    onSelect: Consumer<GameProfile>,
    onCancel: Runnable
) {
    val context = LocalContext.current
    val service = state.service
    Column(Modifier.fillMaxWidth().padding(10.dp)) {
        Text(context.getString(R.string.account_select_character), style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp).padding(top = 6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(state.profiles, key = { it.id }) { profile ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect.accept(profile) },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (service != null) {
                            AndroidView(
                                factory = { viewContext ->
                                    FCLImageView(viewContext).apply {
                                        imageProperty().bind(
                                            TexturesLoader.avatarBinding(
                                                service,
                                                profile.id,
                                                (30 * viewContext.resources.displayMetrics.density).toInt()
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Text(profile.name, modifier = Modifier.padding(start = 8.dp), maxLines = 1)
                    }
                }
            }
        }
        TextButton(onClick = onCancel::run, modifier = Modifier.align(Alignment.End)) {
            Text(context.getString(R.string.dialog_negative))
        }
    }
}
