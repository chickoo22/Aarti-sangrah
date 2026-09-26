package com.example.ui.ads

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
             capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
             capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
}

@Composable
fun AdMobBanner() {
    val context = LocalContext.current
    var hasInternet by remember { mutableStateOf(isNetworkAvailable(context)) }

    // Periodically or upon composition check network
    LaunchedEffect(Unit) {
        hasInternet = isNetworkAvailable(context)
    }

    // Only render and load AdMob ad if network (wifi or mobile data) is enabled
    if (hasInternet) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            AndroidView(
                factory = { ctx ->
                    AdView(ctx).apply {
                        setAdSize(AdSize.BANNER)
                        // Test Ad Unit ID for development & Play Store policy testing
                        adUnitId = "ca-app-pub-3940256099942544/6300978111"
                        loadAd(AdRequest.Builder().build())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
