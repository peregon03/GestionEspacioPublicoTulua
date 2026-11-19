package co.gov.tulua.espaciopublico.core.designsystem

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val TuluaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // Chips, tooltips
    small      = RoundedCornerShape(8.dp),   // Text fields, small buttons
    medium     = RoundedCornerShape(12.dp),  // Dialogs, bottom sheets
    large      = RoundedCornerShape(16.dp),  // Cards
    extraLarge = RoundedCornerShape(24.dp),  // Bottom nav sheet, modals
)

/** Convenience alias for fully circular components (FABs, avatars). */
val FullShape = CircleShape
