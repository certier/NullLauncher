package com.tungsten.fcl.ui.main

import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.util.LinkifyCompat
import com.mio.skin.SkinRenderer
import com.mio.skin.SkinViewer
import com.tungsten.fcl.R
import com.tungsten.fcllibrary.component.theme.ThemeEngine
import java.util.function.Consumer

class MainUIComposeState {
    var announcement: Announcement? by mutableStateOf(null)
        private set

    fun setAnnouncement(value: Announcement?) {
        announcement = value
    }
}

object MainUICompose {
    @JvmStatic
    fun setContent(
        view: ComposeView,
        state: MainUIComposeState,
        renderer: SkinRenderer,
        onSkinViewerCreated: Consumer<SkinViewer>,
        onDoubleClick: Runnable,
        onHide: Runnable
    ) {
        view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        view.setContent {
            MainUIContent(state.announcement, renderer, onSkinViewerCreated, onDoubleClick, onHide)
        }
    }
}

@Composable
private fun MainUIContent(
    announcement: Announcement?,
    renderer: SkinRenderer,
    onSkinViewerCreated: Consumer<SkinViewer>,
    onDoubleClick: Runnable,
    onHide: Runnable
) {
    val context = LocalContext.current
    val themeData by ThemeEngine.theme.collectAsState()
    val isNightMode = ThemeEngine.isNightMode(context)
    val primary = Color(themeData?.getColor() ?: 0xFF777777.toInt())
    val secondary = Color(themeData?.getColor2() ?: 0xFF4F6367.toInt())
    val onPrimary = Color(themeData?.autoTint ?: android.graphics.Color.WHITE)
    val colorScheme = if (isNightMode) {
        darkColorScheme(primary = primary, onPrimary = onPrimary, secondary = secondary)
    } else {
        lightColorScheme(primary = primary, onPrimary = onPrimary, secondary = secondary)
    }

    MaterialTheme(colorScheme = colorScheme) {
        Box(Modifier.fillMaxSize().padding(10.dp)) {
            if (themeData?.closeSkinModel != true) {
                AndroidView(
                    factory = { viewContext ->
                        SkinViewer(viewContext).apply {
                            setRenderer(renderer, 5f)
                            onDoubleClick = SkinViewer.OnDoubleClickListener { onDoubleClick.run() }
                            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                                override fun onViewAttachedToWindow(view: View) {
                                    if (!ThemeEngine.getTheme().closeSkinModel) onResume()
                                }

                                override fun onViewDetachedFromWindow(view: View) {
                                    onPause()
                                }
                            })
                            onSkinViewerCreated.accept(this)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(0.8f)
                        .align(Alignment.CenterEnd)
                )
            }

            Column(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.4f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.width(4.dp).height(26.dp)
                            .background(colorScheme.primary)
                    )
                    Text(
                        text = context.getString(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        color = colorScheme.onSurface,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                if (announcement != null) {
                    Card(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.primary)
                    ) {
                        Column(Modifier.fillMaxSize()) {
                            Text(
                                text = context.getString(
                                    R.string.announcement,
                                    announcement.getDisplayTitle(context)
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.onPrimary,
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            HorizontalDivider()
                            Text(
                                text = linkifiedText(announcement.getDisplayContent(context)),
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .verticalScroll(rememberScrollState()).padding(10.dp),
                                color = colorScheme.onPrimary
                            )
                            HorizontalDivider()
                            Text(
                                text = context.getString(R.string.update_date, announcement.getDate()),
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                color = colorScheme.onPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                    Button(
                        onClick = onHide::run,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(context.getString(R.string.button_hide))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun linkifiedText(text: String): AnnotatedString {
    val spannable = SpannableString(text)
    LinkifyCompat.addLinks(spannable, Linkify.WEB_URLS)
    val links = spannable.getSpans(0, spannable.length, URLSpan::class.java)
        .sortedBy(spannable::getSpanStart)
    return buildAnnotatedString {
        var cursor = 0
        links.forEach { link ->
            val start = spannable.getSpanStart(link)
            val end = spannable.getSpanEnd(link)
            if (start < cursor) return@forEach
            append(spannable.subSequence(cursor, start).toString())
            withLink(
                LinkAnnotation.Url(
                    link.url,
                    TextLinkStyles(SpanStyle(color = Color(0xFF77FF00)))
                )
            ) {
                append(spannable.subSequence(start, end).toString())
            }
            cursor = end
        }
        append(spannable.subSequence(cursor, spannable.length).toString())
    }
}