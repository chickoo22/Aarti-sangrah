package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.viewmodel.AartiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: AartiViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val reminderTime by viewModel.dailyReminderTime.collectAsState()
    var notificationEnabled by remember { mutableStateOf(true) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationEnabled = isGranted
        if (isGranted) {
            showSuccessMessage = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentLang) {
                            "hi" -> "दैनिक प्रार्थना अनुस्मारक"
                            "mr" -> "दैनिक प्रार्थना स्मरण"
                            else -> "Daily Prayer Reminders"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = when (currentLang) {
                    "hi" -> "अपनी दैनिक पूजा और आरती के लिए समय निर्धारित करें"
                    "mr" -> "तुमच्या दैनिक पूजा आणि आरत्यीसाठी वेळ सेट करा"
                    else -> "Set your daily devotional prayer & aarti reminder"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Enable Daily Notifications", fontWeight = FontWeight.Bold)
                        Switch(
                            checked = notificationEnabled,
                            onCheckedChange = { checked ->
                                notificationEnabled = checked
                                if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            }
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Reminder Time")
                        Text(
                            text = reminderTime,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("06:00 AM", "07:00 AM", "06:00 PM", "07:00 PM").forEach { time ->
                            OutlinedButton(
                                onClick = {
                                    viewModel.setDailyReminderTime(time)
                                    showSuccessMessage = true
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text(text = time, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            if (showSuccessMessage) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "✓ Daily reminder successfully scheduled for $reminderTime!",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
