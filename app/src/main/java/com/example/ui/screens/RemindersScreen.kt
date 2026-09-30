package com.example.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.app.viewmodel.AartiViewModel
import com.example.notifications.NotificationHelper
import com.example.notifications.ReminderPreferences
import com.example.ui.components.AdMobBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: AartiViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val prefs = remember { ReminderPreferences(context) }

    var isMorningEnabled by remember { mutableStateOf(prefs.isMorningEnabled) }
    var morningHour by remember { mutableStateOf(prefs.morningHour) }
    var morningMinute by remember { mutableStateOf(prefs.morningMinute) }

    var isEveningEnabled by remember { mutableStateOf(prefs.isEveningEnabled) }
    var eveningHour by remember { mutableStateOf(prefs.eveningHour) }
    var eveningMinute by remember { mutableStateOf(prefs.eveningMinute) }

    var isSpecialDayEnabled by remember { mutableStateOf(prefs.isSpecialDayEnabled) }

    var hasPermission by remember { mutableStateOf(NotificationHelper.hasNotificationPermission(context)) }

    // Dialog state for editing time
    var activeTimeDialogTarget by remember { mutableStateOf<String?>(null) } // "MORNING", "EVENING" or null

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            Toast.makeText(
                context,
                when (currentLang) {
                    "hi" -> "नोटिफिकेशन अनुमति प्राप्त हुई! रिमाइंडर सक्रिय हैं।"
                    "mr" -> "नोटिफिकेशन परवानगी मिळाली! स्मरण सक्रिय झाले."
                    else -> "Notification permission granted! Reminders are active."
                },
                Toast.LENGTH_SHORT
            ).show()
            NotificationHelper.syncAllRemindersFromPreferences(context)
        } else {
            Toast.makeText(
                context,
                when (currentLang) {
                    "hi" -> "सूचना अनुमति अस्वीकृत। कृपया सेटिंग में अनुमति दें।"
                    "mr" -> "सूचना परवानगी नाकारली. कृपया सेटिंग्जमधून अनुमती द्या."
                    else -> "Notification permission denied. Please allow in Settings."
                },
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentLang) {
                            "hi" -> "दैनिक पूजा व आरती रिमाइंडर"
                            "mr" -> "दैनिक पूजा व आरती स्मरण"
                            else -> "Daily Prayer & Aarti Reminders"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingVals ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==================== 1. PERMISSION STATUS BANNER ====================
            item {
                if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = when (currentLang) {
                                        "hi" -> "नोटिफिकेशन अनुमति आवश्यक"
                                        "mr" -> "नोटिफिकेशन परवानगी आवश्यक"
                                        else -> "Notification Permission Needed"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (currentLang) {
                                        "hi" -> "समय पर आरती स्मरण प्राप्त करने के लिए अनुमति दें।"
                                        "mr" -> "वेळेवर नित्य आरती स्मरण मिळवण्यासाठी अनुमती द्या."
                                        else -> "Allow notification permission to receive alerts on time."
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        when (currentLang) {
                                            "hi" -> "अनुमति सक्षम करें"
                                            "mr" -> "परवानगी सक्षम करा"
                                            else -> "Enable Permission"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLang) {
                                    "hi" -> "✅ लोकल नोटिफिकेशन सक्रिय हैं (इंटरनेट की आवश्यकता नहीं)"
                                    "mr" -> "✅ स्थानिक सूचना सक्रिय आहेत (इंटरनेटची गरज नाही)"
                                    else -> "✅ Local Notifications Active (No Backend Required)"
                                },
                                color = Color(0xFF1B5E20),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // ==================== 2. INSTANT TEST NOTIFICATION BUTTON ====================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = when (currentLang) {
                                        "hi" -> "आभी टेस्ट नोटिफिकेशन भेजें"
                                        "mr" -> "आत्ताच टेस्ट नोटिफिकेशन पाठवा"
                                        else -> "Send Test Notification Now"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = when (currentLang) {
                                        "hi" -> "देखें कि आपके फोन पर आरती स्मरण कैसे दिखाई देता है"
                                        "mr" -> "तुमच्या फोनवर आरतीचे स्मरण कसे दिसते ते तपासा"
                                        else -> "See how daily prayer alerts appear on your device"
                                    },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission) {
                                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    val testTitle = when (currentLang) {
                                        "hi" -> "🕉️ शुभ प्रभात! नित्य पूजा व आरती स्मरण"
                                        "mr" -> "🕉️ शुभ प्रभात! नित्य पूजा व आरती स्मरण"
                                        else -> "🕉️ Daily Prayer & Aarti Reminder"
                                    }
                                    val testMsg = when (currentLang) {
                                        "hi" -> "आज का शुभ स्मरण: श्री गणेश आरती, संकटमोचन हनुमान चालीसा व विट्ठल आरती का पाठ करें।"
                                        "mr" -> "आजचे शुभ स्मरण: श्री गणेश आरती, संकटमोचन हनुमान चालीसा व विठ्ठल आरतीचे पठण करा."
                                        else -> "Today's Devotional: Recite Shri Ganesh Aarti, Hanuman Chalisa & Vitthal Aarti."
                                    }
                                    NotificationHelper.sendInstantNotification(
                                        context = context,
                                        title = testTitle,
                                        message = testMsg,
                                        targetAartiId = 4
                                    )
                                    Toast.makeText(
                                        context,
                                        when (currentLang) {
                                            "hi" -> "🔔 टेस्ट नोटिफिकेशन भेजा गया! अपना स्टेटस बार देखें।"
                                            "mr" -> "🔔 टेस्ट नोटिफिकेशन पाठवले! स्टेटस बार तपासा."
                                            else -> "🔔 Test Notification sent! Check your status bar."
                                        },
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentLang) {
                                    "hi" -> "🔔 नोटिफिकेशन टेस्ट करें (Test Notification)"
                                    "mr" -> "🔔 नोटिफिकेशन तपासा (Test Notification)"
                                    else -> "🔔 Trigger Local Notification"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ==================== 3. MORNING POOJA REMINDER ====================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.WbSunny,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = when (currentLang) {
                                            "hi" -> "प्रभात पूजा व आरती"
                                            "mr" -> "प्रभात पूजा व आरती"
                                            else -> "Morning Prayer & Aarti"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = NotificationHelper.formatTime(morningHour, morningMinute),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Switch(
                                checked = isMorningEnabled,
                                onCheckedChange = { checked ->
                                    isMorningEnabled = checked
                                    prefs.isMorningEnabled = checked
                                    NotificationHelper.syncAllRemindersFromPreferences(context)
                                    Toast.makeText(
                                        context,
                                        if (checked) "Morning Reminder enabled for ${NotificationHelper.formatTime(morningHour, morningMinute)}"
                                        else "Morning Reminder disabled",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = when (currentLang) {
                                "hi" -> "प्रत्येक सुबह आपको श्री गणेश आरती व हनुमान चालीसा पाठ का स्मरण दिलाएगा।"
                                "mr" -> "दररोज सकाळी श्री गणेश आरती व हनुमान चालीसा पठणाचे स्मरण करून देईल."
                                else -> "Daily morning reminder to recite Ganesh Aarti and Hanuman Chalisa."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { activeTimeDialogTarget = "MORNING" },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                when (currentLang) {
                                    "hi" -> "समय बदलें (${NotificationHelper.formatTime(morningHour, morningMinute)})"
                                    "mr" -> "वेळ बदला (${NotificationHelper.formatTime(morningHour, morningMinute)})"
                                    else -> "Change Time (${NotificationHelper.formatTime(morningHour, morningMinute)})"
                                },
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // ==================== 4. EVENING AARTI REMINDER ====================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Nightlight,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = when (currentLang) {
                                            "hi" -> "संध्या आरती व दीपपूजन"
                                            "mr" -> "संध्या आरती व दीपपूजन"
                                            else -> "Evening Aarti & Diya"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = NotificationHelper.formatTime(eveningHour, eveningMinute),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            Switch(
                                checked = isEveningEnabled,
                                onCheckedChange = { checked ->
                                    isEveningEnabled = checked
                                    prefs.isEveningEnabled = checked
                                    NotificationHelper.syncAllRemindersFromPreferences(context)
                                    Toast.makeText(
                                        context,
                                        if (checked) "Evening Reminder enabled for ${NotificationHelper.formatTime(eveningHour, eveningMinute)}"
                                        else "Evening Reminder disabled",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = when (currentLang) {
                                "hi" -> "संध्याकाल में माँ दुर्गा व पांडुरंग विट्ठल आरती का स्मरण कराएगा।"
                                "mr" -> "संध्याकाळी घरात दिवा लावताना दुर्गे दुर्घट भारी व विठ्ठल आरतीचे स्मरण करून देईल."
                                else -> "Sunset reminder to light the diya and sing Durga & Vitthal Aarti."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { activeTimeDialogTarget = "EVENING" },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                when (currentLang) {
                                    "hi" -> "समय बदलें (${NotificationHelper.formatTime(eveningHour, eveningMinute)})"
                                    "mr" -> "वेळ बदला (${NotificationHelper.formatTime(eveningHour, eveningMinute)})"
                                    else -> "Change Time (${NotificationHelper.formatTime(eveningHour, eveningMinute)})"
                                },
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // ==================== 5. SPECIAL DAY REMINDER ====================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.SelfImprovement,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = when (currentLang) {
                                            "hi" -> "मंगलवार व शनिवार विशेष"
                                            "mr" -> "मंगळवार व शनिवार विशेष"
                                            else -> "Tuesday & Saturday Special"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = when (currentLang) {
                                            "hi" -> "हनुमान चालीसा पाठ"
                                            "mr" -> "हनुमान चालीसा पाठ"
                                            else -> "Hanuman Chalisa Reminder"
                                        },
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Switch(
                                checked = isSpecialDayEnabled,
                                onCheckedChange = { checked ->
                                    isSpecialDayEnabled = checked
                                    prefs.isSpecialDayEnabled = checked
                                    NotificationHelper.syncAllRemindersFromPreferences(context)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (currentLang) {
                                "hi" -> "प्रत्येक मंगलवार और शनिवार को संकटमोचन हनुमान चालीसा पाठ के लिए विशेष सूचना।"
                                "mr" -> "दर मंगळवारी व शनिवारी संकटमोचन हनुमान चालीसा पठणासाठी खास आठवण."
                                else -> "Special reminder every Tuesday and Saturday to read Shri Hanuman Chalisa."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ==================== 6. LOCAL ARCHITECTURE INFO ====================
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = when (currentLang) {
                                    "hi" -> "✅ 100% स्थानीय सूचना (No Backend)"
                                    "mr" -> "✅ १००% स्थानिक सूचना (No Backend)"
                                    else -> "✅ 100% Local Notifications (No Backend)"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = when (currentLang) {
                                    "hi" -> "सभी अलार्म आपके फोन के अलार्म मैनेजर द्वारा सुरक्षित रूप से संचालित होते हैं। कोई इंटरनेट या बाहरी सर्वर की आवश्यकता नहीं है।"
                                    "mr" -> "सर्व स्मरण अलार्म फोनच्या स्थानिक सिस्टीम द्वारे चालवले जातात. कोणतीही इंटरनेट अथवा बाह्य सर्व्हरची गरज नाही."
                                    else -> "All scheduled reminders are handled locally by Android AlarmManager. No internet, server, or cloud backend required."
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                AdMobBanner()
            }
        }
    }

    // ==================== TIME PICKER DIALOG ====================
    if (activeTimeDialogTarget != null) {
        val isMorning = activeTimeDialogTarget == "MORNING"
        var selectedH by remember { mutableStateOf(if (isMorning) morningHour else eveningHour) }
        var selectedM by remember { mutableStateOf(if (isMorning) morningMinute else eveningMinute) }

        val presetTimes = if (isMorning) {
            listOf(
                Pair(5, 0),
                Pair(5, 30),
                Pair(6, 0),
                Pair(6, 30),
                Pair(7, 0),
                Pair(7, 30),
                Pair(8, 0)
            )
        } else {
            listOf(
                Pair(17, 30),
                Pair(18, 0),
                Pair(18, 30),
                Pair(19, 0),
                Pair(19, 30),
                Pair(20, 0),
                Pair(20, 30)
            )
        }

        Dialog(onDismissRequest = { activeTimeDialogTarget = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isMorning) {
                            when (currentLang) {
                                "hi" -> "प्रभात पूजा का समय चुनें"
                                "mr" -> "प्रभात पूजेची वेळ निवडा"
                                else -> "Select Morning Prayer Time"
                            }
                        } else {
                            when (currentLang) {
                                "hi" -> "संध्या आरती का समय चुनें"
                                "mr" -> "संध्या आरतीची वेळ निवडा"
                                else -> "Select Evening Aarti Time"
                            }
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selected Time Display
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = NotificationHelper.formatTime(selectedH, selectedM),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = when (currentLang) {
                            "hi" -> "त्वरित विकल्प (Quick Presets):"
                            "mr" -> "त्वरित पर्याय (Quick Presets):"
                            else -> "Quick Presets:"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Presets grid/row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        presetTimes.take(4).forEach { (h, m) ->
                            val isSelected = selectedH == h && selectedM == m
                            SuggestionChip(
                                onClick = {
                                    selectedH = h
                                    selectedM = m
                                },
                                label = { Text(NotificationHelper.formatTime(h, m), fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        presetTimes.drop(4).forEach { (h, m) ->
                            val isSelected = selectedH == h && selectedM == m
                            SuggestionChip(
                                onClick = {
                                    selectedH = h
                                    selectedM = m
                                },
                                label = { Text(NotificationHelper.formatTime(h, m), fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { activeTimeDialogTarget = null }) {
                            Text(
                                when (currentLang) {
                                    "hi" -> "रद्द करें"
                                    "mr" -> "रद्द करा"
                                    else -> "Cancel"
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (isMorning) {
                                    morningHour = selectedH
                                    morningMinute = selectedM
                                    prefs.morningHour = selectedH
                                    prefs.morningMinute = selectedM
                                } else {
                                    eveningHour = selectedH
                                    eveningMinute = selectedM
                                    prefs.eveningHour = selectedH
                                    prefs.eveningMinute = selectedM
                                }
                                NotificationHelper.syncAllRemindersFromPreferences(context)
                                Toast.makeText(
                                    context,
                                    "Updated to ${NotificationHelper.formatTime(selectedH, selectedM)}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                activeTimeDialogTarget = null
                            }
                        ) {
                            Text(
                                when (currentLang) {
                                    "hi" -> "सेव करें"
                                    "mr" -> "जतन करा"
                                    else -> "Save Time"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
