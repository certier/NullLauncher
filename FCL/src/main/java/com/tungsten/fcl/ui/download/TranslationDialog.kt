package com.tungsten.fcl.ui.download

import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.tungsten.fcl.util.ModTranslations
import com.tungsten.fclcore.mod.RemoteModRepository
import com.tungsten.fcllibrary.component.dialog.FCLDialog
import com.tungsten.fcllibrary.component.theme.FCLComposeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TranslationDialog(
    context: Context,
    private val repository: RemoteModRepository,
    private val callback: (String) -> Unit
) : FCLDialog(context) {
    private val results = mutableStateListOf<ModTranslations.Mod>()

    init {
        window?.setLayout((500 * context.resources.displayMetrics.density).toInt(), WindowManager.LayoutParams.MATCH_PARENT)
        setContentView(ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                FCLComposeTheme {
                    TranslationContent(
                        results = results,
                        onSearch = ::search,
                        onSelect = { mod ->
                            dismiss()
                            callback(mod.subname().ifEmpty { mod.abbr() })
                        },
                        onDismiss = ::dismiss
                    )
                }
            }
        }, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun search(query: String) {
        lifecycleScope.launch {
            val matches = withContext(Dispatchers.Default) {
                runCatching {
                    ModTranslations.getTranslationsByRepositoryType(repository.type).searchMod(query)
                }.getOrDefault(emptyList())
            }
            results.clear()
            results.addAll(matches)
        }
    }
}

@Composable
private fun TranslationContent(
    results: List<ModTranslations.Mod>,
    onSearch: (String) -> Unit,
    onSelect: (ModTranslations.Mod) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var query by remember { androidx.compose.runtime.mutableStateOf("") }
    Column(Modifier.fillMaxWidth().padding(12.dp)) {
        Text("模组/整合包对应英文查询", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onSearch(it)
            },
            label = { Text(context.getString(com.tungsten.fcl.R.string.search)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(results) { mod ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(mod) },
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "${mod.name()} ${mod.subname()} ${mod.abbr()}",
                        modifier = Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        androidx.compose.material3.TextButton(onClick = onDismiss, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
            Text(context.getString(com.tungsten.fcl.R.string.button_cancel))
        }
    }
}
