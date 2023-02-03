package bassamalim.hidaya.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import bassamalim.hidaya.enums.Theme
import bassamalim.hidaya.utils.PrefUtils

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val dimensions: AppDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current
}

@Composable
fun AppTheme(
    theme: Theme = PrefUtils.getTheme(PrefUtils.getPreferences(LocalContext.current)),
    typography: AppTypography = AppTheme.typography,
    dimensions: AppDimensions = AppTheme.dimensions,
    content: @Composable () -> Unit
) {

    val colors = when (theme) {
        Theme.DARK -> darkColors()
        Theme.NIGHT -> nightColors()
        else -> lightColors()
    }

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalDimensions provides dimensions,
        LocalTypography provides typography
    ) {
        content()
    }
}
