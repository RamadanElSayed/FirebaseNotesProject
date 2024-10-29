package com.instant.firebasenotesproject.components

import android.app.TimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun TimePickerComponent(
    onConfirm: (Int, Int) -> Unit, // Callback to send hour and minute
    onDismiss: () -> Unit          // Callback for dismiss
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            // Call the onConfirm callback with the selected hour and minute
            onConfirm(hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true // Use 24-hour format
    ).apply {
        setOnCancelListener { onDismiss() }
        setOnDismissListener { onDismiss() }
        show()
    }
}
