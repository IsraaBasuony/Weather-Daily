package com.iti.a4cast.util

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.TextView
import com.iti.a4cast.R
import com.iti.a4cast.ui.settings.SettingsSharedPref
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class HomeUtils {

    companion object {
        fun getWeatherIcon(imageString: String): Int {
            val imageInInteger: Int
            when (imageString) {
                "01d" -> imageInInteger = R.drawable.icon_01d
                "01n" -> imageInInteger = R.drawable.icon_01n
                "02d" -> imageInInteger = R.drawable.icon_02d
                "02n" -> imageInInteger = R.drawable.icon_02n
                "03n" -> imageInInteger = R.drawable.icon_03n
                "03d" -> imageInInteger = R.drawable.icon_03d
                "04d" -> imageInInteger = R.drawable.icon_04d
                "04n" -> imageInInteger = R.drawable.icon_04n
                "09d" -> imageInInteger = R.drawable.icon_09d
                "09n" -> imageInInteger = R.drawable.icon_09n
                "10d" -> imageInInteger = R.drawable.icon_10d
                "10n" -> imageInInteger = R.drawable.icon_10n
                "11d" -> imageInInteger = R.drawable.icon_11d
                "11n" -> imageInInteger = R.drawable.icon_11n
                "13d" -> imageInInteger = R.drawable.icon_13d
                "13n" -> imageInInteger = R.drawable.icon_13n
                "50d" -> imageInInteger = R.drawable.icon_50d
                "50n" -> imageInInteger = R.drawable.icon_50n
                else -> imageInInteger = R.drawable.icon_03n
            }
            return imageInInteger
        }

        fun getWeatherImage(imageString: String): Int {
            val imageInInteger: Int
            when (imageString) {
                "01d" -> imageInInteger = R.drawable.icon_01
                "01n" -> imageInInteger = R.drawable.icon_01
                "02d" -> imageInInteger = R.drawable.icon_2
                "02n" -> imageInInteger = R.drawable.icon_2
                "03n" -> imageInInteger = R.drawable.icon_2
                "03d" -> imageInInteger = R.drawable.icon_2
                "04d" -> imageInInteger = R.drawable.icon_2
                "04n" -> imageInInteger = R.drawable.icon_2
                "09d" -> imageInInteger = R.drawable.icon_9
                "09n" -> imageInInteger = R.drawable.icon_9
                "10d" -> imageInInteger = R.drawable.icon_10
                "10n" -> imageInInteger = R.drawable.icon_10
                "11d" -> imageInInteger = R.drawable.icon_11
                "11n" -> imageInInteger = R.drawable.icon_11
                "13d" -> imageInInteger = R.drawable.icon_13
                "13n" -> imageInInteger = R.drawable.icon_13
                "50d" -> imageInInteger = R.drawable.icon_50
                "50n" -> imageInInteger = R.drawable.icon_50
                else -> imageInInteger = R.drawable.icon_2
            }
            return imageInInteger
        }

        fun timeStampToHour(dt: Long, lang: String): String {
            var date = Date(dt * 1000)
            var dateFormat: DateFormat = SimpleDateFormat("h:mm aa", Locale(lang))
            return dateFormat.format(date)
        }

        fun timeStampMonth(dt: Long, lang: String): String {
            val date = Date(dt * 1000)
            val dateFormat: DateFormat = SimpleDateFormat("EEE, dd MMM", Locale(lang))
            return dateFormat.format(date)
        }

        fun getTimeFormat(timeInMilliSecond: Long, language: String): String {
            val date = Date(timeInMilliSecond)
            val convertFormat =
                SimpleDateFormat("hh:mm a", Locale(language))
            return convertFormat.format(date).toString()
        }

        fun getADateFormat(timeInMilliSecond: Long, language: String): String {
            val pattern = "dd MMMM"
            val simpleDateFormat = SimpleDateFormat(pattern, Locale(language))
            return simpleDateFormat.format(Date(timeInMilliSecond))
        }


        fun getDayFormat(dt: Long, lang: String): String {
            val date = Date(dt * 1000)
            val dateFormat: DateFormat = SimpleDateFormat("EEE", Locale(lang))
            return dateFormat.format(date)
        }


        fun changeLanguage(language: String, context: Context) {
            val newLocale = Locale(language)
            Locale.setDefault(newLocale)
            val configuration = Configuration()
            configuration.setLocale(newLocale)
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        }


        fun getLocationAddress(
            context: Context,
            lat: Double,
            long: Double,
            onResult: (Address?) -> Unit
        ) {
            var address: Address?
            val geocoder = Geocoder(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, long, 1) {
                    address = it[0]
                    onResult(address)
                }
            } else {
                val addressList = geocoder.getFromLocation(lat, long, 1)
                if (!addressList.isNullOrEmpty()) {
                    address = geocoder.getFromLocation(lat, long, 1)?.get(0)
                    onResult(address)
                } else {
                    onResult(null)
                }

            }
        }

        fun getAddressFormat(address: Address?): String {
            return address?.let {
                if (it.subAdminArea != null) {
                    "${it.adminArea}, ${it.subAdminArea}"
                } else {
                    "${it.adminArea}, ${it.countryName}"
                }
            } ?: "unKnown"
        }

        fun checkForInternet(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false

                val activeNetwork =
                    connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            } else {
                @Suppress("DEPRECATION") val networkInfo =
                    connectivityManager.activeNetworkInfo ?: return false
                @Suppress("DEPRECATION") return networkInfo.isConnected
            }
        }
    }
}


fun TextView.setWindSpeed(windSpeed: Double, application: Application) {
    val preferences = SettingsSharedPref.getInstance(application)
    when (preferences.getWindSpeedPref()) {
        SettingsSharedPref.METER_PER_SECOND -> text =
            buildString {
                append(windSpeed.roundToInt().toString())
                append(application.getString(R.string.meter_per_second))
            }

        SettingsSharedPref.MILE_PER_HOUR -> text = buildString {
            append(getWindSpeedInMilesPerHour(windSpeed).roundToInt().toString())
            append(application.getString(R.string.mile_per_hour))
        }
    }
}

fun TextView.setTemp(temp: Int, context: Context) {
    val symbol: String
    val preferences = SettingsSharedPref.getInstance(context)
    val theTemp = when (preferences.getTempPref()) {
        SettingsSharedPref.CELSIUS -> {
            symbol = context.getString(R.string.c_symbol)
            temp
        }

        SettingsSharedPref.FAHRENHEIT -> {
            symbol = context.getString(R.string.f_symbol)
            (((temp) * 9.0 / 5) + 32).roundToInt()
        }

        SettingsSharedPref.KELVIN -> {
            symbol = context.getString(R.string.k_symbol)
            (temp) + 273
        }

        else -> {
            symbol = context.getString(R.string.c_symbol)
            temp
        }
    }
    text = buildString {
        append(theTemp)
        append(" ")
        append(symbol)
    }


}

fun getWindSpeedInMilesPerHour(windSpeed: Double): Double {
    return windSpeed * 2.23694
}