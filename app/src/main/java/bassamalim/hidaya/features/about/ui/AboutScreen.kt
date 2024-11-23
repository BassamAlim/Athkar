package bassamalim.hidaya.features.about.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bassamalim.hidaya.R
import bassamalim.hidaya.core.models.Source
import bassamalim.hidaya.core.ui.components.MyHorizontalDivider
import bassamalim.hidaya.core.ui.components.MyRectangleButton
import bassamalim.hidaya.core.ui.components.MyScaffold
import bassamalim.hidaya.core.ui.components.MyText

@Composable
fun AboutScreen(viewModel: AboutViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as Activity

    MyScaffold(title = stringResource(R.string.about)) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 5.dp)
        ) {
            ThankYouMessage(onTitleClick = viewModel::onTitleClick)

            SourcesList(state.sources)

            HiddenArea(
                isDevModeOn = state.isDevModeEnabled,
                lastDailyUpdate = state.lastDailyUpdate,
                onRebuildDatabaseClick = { viewModel.onRebuildDatabaseClick(activity) }
            )
        }
    }

    // show a toast when the database is rebuilt
    if (state.shouldShowRebuilt != 0) {
        DatabaseRebuiltToast(shouldShowRebuilt = state.shouldShowRebuilt)
    }
}

@Composable
private fun ColumnScope.ThankYouMessage(onTitleClick: () -> Unit) {
    MyText(
        text = stringResource(R.string.thanks),
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(top = 15.dp, bottom = 20.dp)
            .align(Alignment.CenterHorizontally)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onTitleClick
            )
    )
}

@Composable
private fun ColumnScope.SourcesList(sources: List<Source>) {
    Column(
        Modifier
            .weight(1F)
            .verticalScroll(rememberScrollState())
    ) {
        MyText(
            text = stringResource(R.string.sources),
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 15.dp, bottom = 10.dp)
                .align(Alignment.CenterHorizontally)
        )

        sources.forEach { source ->
            Source(source)
            if (source != sources.last()) MyHorizontalDivider()
        }
    }
}

@Composable
private fun Source(source: Source) {
    val annotatedString = buildAnnotatedString {
        append("${source.title}: ")
        withLink(
            LinkAnnotation.Url(
                url = source.url,
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            )
        ) {
            append(source.sourceName)
        }
    }

    MyText(
        text = annotatedString,
        modifier = Modifier.padding(10.dp),
        fontSize = 20.sp,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun ColumnScope.HiddenArea(
    isDevModeOn: Boolean,
    lastDailyUpdate: String,
    onRebuildDatabaseClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = isDevModeOn,
        enter = expandVertically()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // rebuild database button
            MyRectangleButton(
                text = stringResource(R.string.rebuild_database),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onRebuildDatabaseClick
            )

            // last daily update text
            MyText(
                text = lastDailyUpdate,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            )
        }
    }
}

@Composable
private fun DatabaseRebuiltToast(shouldShowRebuilt: Int) {
    val ctx = LocalContext.current
    LaunchedEffect(key1 = shouldShowRebuilt) {
        Toast.makeText(
            ctx,
            ctx.getString(R.string.database_rebuilt),
            Toast.LENGTH_SHORT
        ).show()
    }
}