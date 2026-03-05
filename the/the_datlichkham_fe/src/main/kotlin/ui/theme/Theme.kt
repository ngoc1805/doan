package ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PrimaryBlue     = Color(0xFF1565C0)
val PrimaryLight    = Color(0xFF42A5F5)
val AccentTeal      = Color(0xFF00897B)
val AccentGreen     = Color(0xFF43A047)
val SurfaceWhite    = Color(0xFFF5F9FF)
val CardWhite       = Color(0xFFFFFFFF)
val TextDark        = Color(0xFF1A237E)
val TextGray        = Color(0xFF546E7A)
val Success         = Color(0xFF2E7D32)
val SuccessLight    = Color(0xFFE8F5E9)
val Warning         = Color(0xFFF57F17)
val WarningLight    = Color(0xFFFFF8E1)
val ErrorRed        = Color(0xFFC62828)
val ErrorLight      = Color(0xFFFFEBEE)
val DividerColor    = Color(0xFFE3EAF6)
val GradientStart   = Color(0xFF1565C0)
val GradientEnd     = Color(0xFF0288D1)

private val HospitalColors = lightColors(
    primary         = PrimaryBlue,
    primaryVariant  = Color(0xFF0D47A1),
    secondary       = AccentTeal,
    background      = SurfaceWhite,
    surface         = CardWhite,
    onPrimary       = Color.White,
    onSecondary     = Color.White,
    onBackground    = TextDark,
    onSurface       = TextDark,
    error           = ErrorRed,
    onError         = Color.White,
)

private val HospitalTypography = Typography(
    h4 = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    h5 = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    h6 = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    body1 = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 15.sp),
    body2 = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 13.sp, color = TextGray),
    caption = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 11.sp, color = TextGray),
)

@Composable
fun HospitalCardTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = HospitalColors, typography = HospitalTypography, content = content)
}
