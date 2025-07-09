//
//  WatchStatusIcon.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bceassociates.livetarget.ui.theme.LiveTargetTheme
import com.bceassociates.livetarget.watch.WatchConnectionStatus

/**
 * Watch status icon that displays the current connection status
 * Similar to iOS WatchStatusIcon but for Android/Samsung watches
 */
@Composable
fun WatchStatusIcon(
    status: WatchConnectionStatus,
    integrationEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Base watch icon
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Watch status",
            tint = getIconColor(status, integrationEnabled),
            modifier = Modifier.size(16.dp),
        )
        
        // Error overlay (circle with diagonal line) - only if integration is enabled
        if (integrationEnabled && (status == WatchConnectionStatus.DISCONNECTED || status == WatchConnectionStatus.ERROR)) {
            // Using a simple red circle with slash overlay
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                contentDescription = "Connection error",
                tint = Color.Red,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Determines the icon color based on connection status and integration setting
 */
private fun getIconColor(status: WatchConnectionStatus, integrationEnabled: Boolean): Color {
    // If integration is disabled, always show grey
    if (!integrationEnabled) {
        return Color.Gray
    }
    
    return when (status) {
        WatchConnectionStatus.CONNECTED -> Color.Green
        WatchConnectionStatus.DISCONNECTED, WatchConnectionStatus.ERROR -> Color.Red
        WatchConnectionStatus.UNKNOWN -> Color.Gray
    }
}

@Preview(showBackground = true)
@Composable
fun WatchStatusIconPreview() {
    LiveTargetTheme {
        Box {
            WatchStatusIcon(
                status = WatchConnectionStatus.CONNECTED,
                integrationEnabled = true,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WatchStatusIconDisconnectedPreview() {
    LiveTargetTheme {
        Box {
            WatchStatusIcon(
                status = WatchConnectionStatus.ERROR,
                integrationEnabled = true,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WatchStatusIconDisabledPreview() {
    LiveTargetTheme {
        Box {
            WatchStatusIcon(
                status = WatchConnectionStatus.CONNECTED,
                integrationEnabled = false,
            )
        }
    }
}