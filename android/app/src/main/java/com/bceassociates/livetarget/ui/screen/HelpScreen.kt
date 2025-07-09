//
//  HelpScreen.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bceassociates.livetarget.R
import com.bceassociates.livetarget.ui.theme.LiveTargetTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.help)) },
                    actions = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
                        }
                    },
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    item {
                        HelpSection(
                            title = stringResource(R.string.help_what_is_title),
                            content = {
                                Text(
                                    text = stringResource(R.string.help_what_is_description),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                
                                Text(
                                    text = stringResource(R.string.help_feature_parity),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 8.dp),
                                )
                                
                                Text(
                                    text = stringResource(R.string.help_what_is_use_cases),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 12.dp),
                                )
                                
                                Column(
                                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                                ) {
                                    Text("• ${stringResource(R.string.help_use_case_practice)}")
                                    Text("• ${stringResource(R.string.help_use_case_accuracy)}")
                                    Text("• ${stringResource(R.string.help_use_case_analysis)}")
                                    Text("• ${stringResource(R.string.help_use_case_monitoring)}")
                                }
                            },
                        )
                    }
                    
                    item {
                        HelpSection(
                            title = stringResource(R.string.help_how_to_use_title),
                            content = {
                                val steps = listOf(
                                    stringResource(R.string.help_step_1),
                                    stringResource(R.string.help_step_2),
                                    stringResource(R.string.help_step_3),
                                    stringResource(R.string.help_step_4),
                                    stringResource(R.string.help_step_5),
                                )
                                
                                steps.forEachIndexed { index, step ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Text(
                                            text = "${index + 1}.",
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(end = 8.dp),
                                        )
                                        Text(
                                            text = step,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                }
                            },
                        )
                    }
                    
                    item {
                        HelpSection(
                            title = stringResource(R.string.help_settings_title),
                            content = {
                                SettingGroup(
                                    title = stringResource(R.string.help_colors_title),
                                    items = listOf(
                                        stringResource(R.string.help_circle_color),
                                        stringResource(R.string.help_number_color),
                                    ),
                                )
                                
                                SettingGroup(
                                    title = stringResource(R.string.help_detection_title),
                                    items = listOf(
                                        stringResource(R.string.help_check_frequency),
                                        stringResource(R.string.help_bullet_caliber),
                                        stringResource(R.string.help_caliber_note),
                                    ),
                                    modifier = Modifier.padding(top = 16.dp),
                                )
                                
                                SettingGroup(
                                    title = stringResource(R.string.help_watch_title),
                                    items = listOf(
                                        stringResource(R.string.help_watch_integration),
                                        stringResource(R.string.help_watch_status),
                                        stringResource(R.string.help_watch_notifications),
                                    ),
                                    modifier = Modifier.padding(top = 16.dp),
                                )
                            },
                        )
                    }
                    
                    item {
                        HelpSection(
                            title = stringResource(R.string.help_controls_title),
                            content = {
                                SettingGroup(
                                    title = stringResource(R.string.help_main_controls_title),
                                    items = listOf(
                                        stringResource(R.string.help_start_stop),
                                        stringResource(R.string.help_clear),
                                        stringResource(R.string.help_save),
                                        stringResource(R.string.help_settings_button),
                                    ),
                                )
                                
                                SettingGroup(
                                    title = stringResource(R.string.help_watch_status_icons),
                                    items = listOf(
                                        stringResource(R.string.help_watch_green),
                                        stringResource(R.string.help_watch_red),
                                        stringResource(R.string.help_watch_gray),
                                    ),
                                    modifier = Modifier.padding(top = 16.dp),
                                )
                                
                                SettingGroup(
                                    title = stringResource(R.string.help_zoom_controls_title),
                                    items = listOf(
                                        stringResource(R.string.help_zoom_controls),
                                        stringResource(R.string.help_zoom_range),
                                        stringResource(R.string.help_zoom_detail),
                                    ),
                                    modifier = Modifier.padding(top = 16.dp),
                                )
                            },
                        )
                    }
                    
                    item {
                        HelpSection(
                            title = stringResource(R.string.help_tips_title),
                            content = {
                                SettingGroup(
                                    title = stringResource(R.string.help_camera_positioning),
                                    items = listOf(
                                        stringResource(R.string.help_mount_device),
                                        stringResource(R.string.help_target_fills_view),
                                        stringResource(R.string.help_avoid_backlighting),
                                        stringResource(R.string.help_perpendicular_position),
                                    ),
                                )
                                
                                SettingGroup(
                                    title = stringResource(R.string.help_optimal_settings),
                                    items = listOf(
                                        stringResource(R.string.help_start_2_second),
                                        stringResource(R.string.help_set_caliber),
                                        stringResource(R.string.help_contrasting_colors),
                                        stringResource(R.string.help_lower_frequency),
                                    ),
                                    modifier = Modifier.padding(top = 16.dp),
                                )
                                
                                SettingGroup(
                                    title = stringResource(R.string.help_environment),
                                    items = listOf(
                                        stringResource(R.string.help_consistent_lighting),
                                        stringResource(R.string.help_minimize_movement),
                                        stringResource(R.string.help_start_before_first),
                                        stringResource(R.string.help_clean_background),
                                    ),
                                    modifier = Modifier.padding(top = 16.dp),
                                )
                            },
                        )
                    }
                    
                    item {
                        HelpSection(
                            title = stringResource(R.string.help_troubleshooting_title),
                            content = {
                                SettingGroup(
                                    title = stringResource(R.string.help_detection_issues),
                                    items = listOf(
                                        stringResource(R.string.help_false_detections),
                                        stringResource(R.string.help_missed_impacts),
                                        stringResource(R.string.help_multiple_detections),
                                    ),
                                )
                                
                                SettingGroup(
                                    title = stringResource(R.string.help_performance_issues),
                                    items = listOf(
                                        stringResource(R.string.help_close_camera_apps),
                                        stringResource(R.string.help_restart_app),
                                        stringResource(R.string.help_increase_frequency),
                                    ),
                                    modifier = Modifier.padding(top = 16.dp),
                                )
                                
                                SettingGroup(
                                    title = stringResource(R.string.help_watch_issues),
                                    items = listOf(
                                        stringResource(R.string.help_watch_pair_check),
                                        stringResource(R.string.help_watch_enable_integration),
                                        stringResource(R.string.help_watch_connection_verify),
                                        stringResource(R.string.help_watch_test_start),
                                    ),
                                    modifier = Modifier.padding(top = 16.dp),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HelpSection(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            )
            
            content()
        }
    }
}

@Composable
private fun SettingGroup(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
        )
        
        Column(
            modifier = Modifier.padding(start = 8.dp, top = 8.dp),
        ) {
            items.forEach { item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    LiveTargetTheme {
        HelpScreen(onDismiss = {})
    }
}