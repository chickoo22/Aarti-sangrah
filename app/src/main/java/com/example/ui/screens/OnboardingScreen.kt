package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.app.viewmodel.AartiViewModel
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val titleMarathi: String,
    val titleHindi: String,
    val titleEnglish: String,
    val subtitleMarathi: String,
    val subtitleHindi: String,
    val subtitleEnglish: String,
    val highlights: List<Pair<String, String>>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: AartiViewModel,
    onFinishOnboarding: () -> Unit
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })

    val pages = remember {
        listOf(
            OnboardingPageData(
                titleMarathi = "अखंड भक्ती व प्रामाणिक संग्रह",
                titleHindi = "प्रामाणिक आरती एवं चालीसा संग्रह",
                titleEnglish = "Authentic Aarti & Chalisa Collection",
                subtitleMarathi = "श्री गणेश, शिव, हनुमान, विठ्ठल, दुर्गा, शनी व सरस्वती देवतांच्या पारंपारिक आरत्या व चालीसा मराठी, हिंदी व इंग्रजी भाषेमध्ये.",
                subtitleHindi = "श्री गणेश, शिव, हनुमान, विष्णु, दुर्गा व शनि देव के संपूर्ण पाठ एवं चालीसा सहज हिंदी व संस्कृत रूप में उपलब्ध.",
                subtitleEnglish = "Complete collection of traditional Hindu prayers, daily Aartis and Chalisas with lyrics in Marathi, Hindi & English.",
                highlights = listOf(
                    "🕉️ ७ मुख्य देवता" to "सर्व प्रमुख देवतांचे प्रामाणिक पाठ",
                    "📖 त्रिभाषिक" to "मराठी, हिंदी व इंग्रजी भाषांतर",
                    "⭐ आवडीचे संग्रह" to "नित्य पठणासाठी जलद बुकमार्क"
                )
            ),
            OnboardingPageData(
                titleMarathi = "ऑफलाइन नित्य पूजा व सुलभ वाचन",
                titleHindi = "ऑफलाइन नित्य पाठ एवं सहज वाचन",
                titleEnglish = "100% Offline & Clean Reader",
                subtitleMarathi = "पूजेच्या वेळी कोणत्याही इंटरनेट किंवा व्यत्ययाशिवाय सहज पठण करा. फॉन्ट आकार लहान-मोठा करण्याची पूर्ण सोय.",
                subtitleHindi = "पूजा के समय बिना किसी रुकावट के १००% ऑफलाइन पाठ। आसान फॉन्ट साइज एडजस्टमेंट एवं डार्क मोड सुविधा।",
                subtitleEnglish = "Zero internet needed during daily puja. Instant search, customizable font size, and distraction-free layout.",
                highlights = listOf(
                    "⚡ १००% ऑफलाइन" to "इंटरनेट नसले तरी सर्व आरत्या उपलब्ध",
                    "🔍 झटपट शोध" to "नाव किंवा ओळीने त्वरित शोधा",
                    "🔤 फॉन्ट नियंत्रण" to "वाचनासाठी सुलभ अक्षर आकार"
                )
            ),
            OnboardingPageData(
                titleMarathi = "नित्य पूजा स्मरण व भाषा निवड",
                titleHindi = "दैनिक पूजा स्मरण एवं भाषा चयन",
                titleEnglish = "Daily Prayer Reminders & Language",
                subtitleMarathi = "सकाळ-संध्याकाळ नित्य आरतीचे स्मरण आणि मंगळवार-शनिवार विशेष हनुमान चालीसा अलार्म. आपली भाषा निवडून सुरुवात करा.",
                subtitleHindi = "प्रातः एवं संध्या काल नित्य आरती रिमाइंडर तथा मंगलवार-शनिवार विशेष पाठ अलार्म। अपनी भाषा चुनें।",
                subtitleEnglish = "Morning & evening local prayer alarms and special Tuesday/Saturday reminders. Select your preferred language:",
                highlights = listOf(
                    "⏰ सकाळ स्मरण" to "प्रातः ६:०० वाजता शुभ स्मरण",
                    "🔔 संध्याकाळ स्मरण" to "संध्याकाळी ७:०० वाजता दीप प्रज्वलन",
                    "🚩 विशेष दिवस" to "मंगळवार/शनिवार हनुमान चालीसा"
                )
            )
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "मंत्रमया • MANTRAMAYA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Skip button (visible on pages 0 and 1)
                if (pagerState.currentPage < 2) {
                    TextButton(
                        onClick = {
                            viewModel.completeOnboarding()
                            onFinishOnboarding()
                        },
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = when (currentLang) {
                                "hi" -> "छोड़ें"
                                "mr" -> "वगळा"
                                else -> "Skip"
                            },
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pager Indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    repeat(3) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(if (isSelected) 28.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }

                // Main CTA Button
                val isLastPage = pagerState.currentPage == 2
                Button(
                    onClick = {
                        if (isLastPage) {
                            viewModel.completeOnboarding()
                            onFinishOnboarding()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLastPage) {
                                when (currentLang) {
                                    "hi" -> "आरंभ करें (Get Started)"
                                    "mr" -> "सुरू करा (Get Started)"
                                    else -> "Get Started"
                                }
                            } else {
                                when (currentLang) {
                                    "hi" -> "आगे बढ़ें (Next)"
                                    "mr" -> "पुढे जा (Next)"
                                    else -> "Next"
                                }
                            },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isLastPage) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Hero Section for each page
                when (pageIndex) {
                    0 -> {
                        // Page 0: Featured artwork
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_onboarding_divine_1790772760877),
                                contentDescription = "Sacred Devotion Diya & Bells",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    1 -> {
                        // Page 1: Features card
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_deities_divine_art_1790419718200),
                                contentDescription = "Sacred Deities Aarti Art",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    2 -> {
                        // Page 2: Language Preference & Reminders
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_spiritual_banner_1790413550928),
                                contentDescription = "Spiritual Temple Bells Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Titles
                Text(
                    text = when (currentLang) {
                        "hi" -> page.titleHindi
                        "mr" -> page.titleMarathi
                        else -> page.titleEnglish
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = when (currentLang) {
                        "hi" -> page.subtitleHindi
                        "mr" -> page.subtitleMarathi
                        else -> page.subtitleEnglish
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Special interactive Language Selection on Page 3
                if (pageIndex == 2) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "नित्य वाचनासाठी भाषा निवडा (Select Language):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("mr", "मराठी", "Marathi"),
                                Triple("hi", "हिंदी", "Hindi"),
                                Triple("en", "English", "English")
                            ).forEach { (code, labelNative, labelSub) ->
                                val isSelected = currentLang == code
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(58.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { viewModel.setLanguage(code) }
                                        .testTag("onboarding_lang_$code")
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Text(
                                            text = labelNative,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = labelSub,
                                            fontSize = 11.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Highlight items for pages 0 and 1
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        page.highlights.forEach { (badge, detail) ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = badge,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = detail,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
