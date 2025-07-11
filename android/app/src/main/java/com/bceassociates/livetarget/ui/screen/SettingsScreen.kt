//
//  SettingsScreen.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.ui.screen

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bceassociates.livetarget.R
import com.bceassociates.livetarget.data.AmmoType
import com.bceassociates.livetarget.data.CaliberData
import com.bceassociates.livetarget.data.OverlayPosition
import com.bceassociates.livetarget.ui.theme.LiveTargetTheme
import com.bceassociates.livetarget.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onDismiss: () -> Unit,
    viewModel: MainViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    // Get app version info
    val packageInfo: PackageInfo? = try {
        context.packageManager.getPackageInfo(context.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
    
    val appVersion = packageInfo?.let { "${it.versionName} (${it.longVersionCode})" } ?: "0.91"
    val buildDate = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date()) // Current build date
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text(stringResource(R.string.settings)) },
            navigationIcon = {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
        )
        
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Colors Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.colors),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    
                    // Circle Color
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(R.string.circle_color))
                        ColorPreview(color = Color(android.graphics.Color.parseColor("#${uiState.circleColor}")))
                    }
                    
                    // Number Color
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(R.string.number_color))
                        ColorPreview(color = Color(android.graphics.Color.parseColor("#${uiState.numberColor}")))
                    }
                }
            }
            
            // Detection Settings Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.change_detection),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    
                    // Check Frequency
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(stringResource(R.string.check_frequency))
                            Text("${String.format("%.1f", uiState.checkInterval)}s")
                        }
                        
                        Slider(
                            value = uiState.checkInterval.toFloat(),
                            onValueChange = { viewModel.setCheckInterval(it.toDouble()) },
                            valueRange = 0.5f..10.0f,
                            steps = 19, // 0.5 step increments
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    
                    // Bullet Caliber
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.bullet_caliber),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        var expanded by remember { mutableStateOf(false) }
                        val selectedCaliber = uiState.selectedCaliber
                        
                        Box {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedCaliber?.name ?: ".22 Long Rifle",
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown"
                                    )
                                }
                            }
                            
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                Text(
                                    text = "Common Calibers",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                CaliberData.commonCalibers.forEach { caliber ->
                                    DropdownMenuItem(
                                        text = { Text(caliber.name) },
                                        onClick = {
                                            viewModel.setSelectedCaliberName(caliber.name)
                                            expanded = false
                                        }
                                    )
                                }
                                
                                Text(
                                    text = "Rimfire",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                CaliberData.rimfireCalibers.forEach { caliber ->
                                    DropdownMenuItem(
                                        text = { Text(caliber.name) },
                                        onClick = {
                                            viewModel.setSelectedCaliberName(caliber.name)
                                            expanded = false
                                        }
                                    )
                                }
                                
                                Text(
                                    text = "Pistol Calibers",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                CaliberData.pistolCalibers.forEach { caliber ->
                                    DropdownMenuItem(
                                        text = { Text(caliber.name) },
                                        onClick = {
                                            viewModel.setSelectedCaliberName(caliber.name)
                                            expanded = false
                                        }
                                    )
                                }
                                
                                Text(
                                    text = "Rifle Calibers",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                CaliberData.rifleCalibers.forEach { caliber ->
                                    DropdownMenuItem(
                                        text = { Text(caliber.name) },
                                        onClick = {
                                            viewModel.setSelectedCaliberName(caliber.name)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Show caliber details
                        if (selectedCaliber != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Diameter: ${String.format("%.3f", selectedCaliber.diameterInches)}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "(${selectedCaliber.pixelSize}×${selectedCaliber.pixelSize} pixels)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            // Saved Image Overlay Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Saved Image Overlay",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    
                    // Overlay Enable Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Enable Overlay")
                        Switch(
                            checked = uiState.overlayEnabled,
                            onCheckedChange = { viewModel.setOverlayEnabled(it) },
                        )
                    }
                    
                    // Overlay Position Dropdown
                    if (uiState.overlayEnabled) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Text(
                                text = "Overlay Position",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            var positionExpanded by remember { mutableStateOf(false) }
                            val currentPosition = try {
                                OverlayPosition.valueOf(uiState.overlayPosition)
                            } catch (e: Exception) {
                                OverlayPosition.TOP_LEFT
                            }
                            
                            Box {
                                OutlinedButton(
                                    onClick = { positionExpanded = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = currentPosition.displayName,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = "Dropdown"
                                        )
                                    }
                                }
                                
                                DropdownMenu(
                                    expanded = positionExpanded,
                                    onDismissRequest = { positionExpanded = false }
                                ) {
                                    OverlayPosition.values().forEach { position ->
                                        DropdownMenuItem(
                                            text = { Text(position.displayName) },
                                            onClick = {
                                                viewModel.setOverlayPosition(position.name)
                                                positionExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Bullet Weight
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Text(
                                text = "Bullet Weight",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            OutlinedTextField(
                                value = uiState.bulletWeight.toString(),
                                onValueChange = { newValue ->
                                    newValue.toDoubleOrNull()?.let { weight ->
                                        viewModel.setBulletWeight(weight)
                                    }
                                },
                                label = { Text("Weight in grains") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Ammo Type
                        val currentAmmoType = try {
                            AmmoType.valueOf(uiState.ammoType)
                        } catch (e: Exception) {
                            AmmoType.FACTORY
                        }
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Text(
                                text = "Ammo Type",
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            var ammoTypeExpanded by remember { mutableStateOf(false) }
                            
                            Box {
                                OutlinedButton(
                                    onClick = { ammoTypeExpanded = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = currentAmmoType.displayName,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = "Dropdown"
                                        )
                                    }
                                }
                                
                                DropdownMenu(
                                    expanded = ammoTypeExpanded,
                                    onDismissRequest = { ammoTypeExpanded = false }
                                ) {
                                    AmmoType.values().forEach { ammoType ->
                                        DropdownMenuItem(
                                            text = { Text(ammoType.displayName) },
                                            onClick = {
                                                viewModel.setAmmoType(ammoType.name)
                                                ammoTypeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Conditional fields based on ammo type
                        if (currentAmmoType == AmmoType.FACTORY) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            ) {
                                Text(
                                    text = "Factory Ammo Name",
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                
                                OutlinedTextField(
                                    value = uiState.factoryAmmoName,
                                    onValueChange = { viewModel.setFactoryAmmoName(it) },
                                    label = { Text("e.g., Federal Premium") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            ) {
                                Text(
                                    text = "Powder",
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                
                                OutlinedTextField(
                                    value = uiState.handloadPowder,
                                    onValueChange = { viewModel.setHandloadPowder(it) },
                                    label = { Text("e.g., H4350") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            ) {
                                Text(
                                    text = "Powder Charge",
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                
                                OutlinedTextField(
                                    value = uiState.handloadCharge.toString(),
                                    onValueChange = { newValue ->
                                        newValue.toDoubleOrNull()?.let { charge ->
                                            viewModel.setHandloadCharge(charge)
                                        }
                                    },
                                    label = { Text("Charge in grains") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
            
            // Samsung Watch Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Samsung Galaxy Watch",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    
                    // Watch Integration Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Watch Integration")
                        Switch(
                            checked = uiState.watchIntegrationEnabled,
                            onCheckedChange = { viewModel.setWatchIntegrationEnabled(it) },
                        )
                    }
                    
                    // Watch Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Watch Status:",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = when {
                                uiState.isWatchPaired -> "Paired"
                                else -> "Not Paired"
                            },
                            fontWeight = FontWeight.Medium,
                            color = when {
                                uiState.isWatchPaired -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                    
                    // Connection Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Connection:",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = when (uiState.watchConnectionStatus) {
                                com.bceassociates.livetarget.watch.WatchConnectionStatus.CONNECTED -> "Connected"
                                com.bceassociates.livetarget.watch.WatchConnectionStatus.DISCONNECTED -> "Disconnected"
                                com.bceassociates.livetarget.watch.WatchConnectionStatus.ERROR -> "Error"
                                com.bceassociates.livetarget.watch.WatchConnectionStatus.UNKNOWN -> "Unknown"
                            },
                            fontWeight = FontWeight.Medium,
                            color = when (uiState.watchConnectionStatus) {
                                com.bceassociates.livetarget.watch.WatchConnectionStatus.CONNECTED -> MaterialTheme.colorScheme.primary
                                com.bceassociates.livetarget.watch.WatchConnectionStatus.ERROR -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                    
                    // Help text
                    if (!uiState.isWatchPaired) {
                        Text(
                            text = "Pair a Samsung Galaxy Watch to receive impact notifications and alerts.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
            
            // About Section
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.about),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    
                    // App Name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "App Name",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    
                    // Version
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(R.string.app_version),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = appVersion,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    
                    // Build Date
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(R.string.build_date),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = buildDate,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    
                    // Copyright
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.copyright),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                        Text(
                            text = stringResource(R.string.copyright_text),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPreview(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .padding(2.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = color),
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    LiveTargetTheme {
        SettingsScreen(onDismiss = {})
    }
}
