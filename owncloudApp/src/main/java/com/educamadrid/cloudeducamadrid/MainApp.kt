/*
 * ownCloud Android client application
 *
 * @author masensio
 * @author David A. Velasco
 * @author David González Verdugo
 * @author Christian Schabesberger
 * Copyright (C) 2020 ownCloud GmbH.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.educamadrid.cloudeducamadrid

// Educamadrid: Imports necesarios para sistema de alertas

// import androidx.appcompat.app.AlertDialog
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.content.DialogInterface
//import android.graphics.Color
//import android.util.Log
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.view.ContextThemeWrapper
//import com.google.android.material.internal.ContextUtils.getActivity
//import com.owncloud.android.services.AlertService
//import com.owncloud.android.ui.preview.PreviewVideoError
//import com.owncloud.android.utils.Alert
//import org.threeten.bp.LocalDateTime
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory

import android.app.Activity
import android.app.Application
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.educamadrid.cloudeducamadrid.data.preferences.datasources.implementation.SharedPreferencesProviderImpl
import com.educamadrid.cloudeducamadrid.datamodel.ThumbnailsCacheManager
import com.educamadrid.cloudeducamadrid.db.PreferenceManager
import com.educamadrid.cloudeducamadrid.dependecyinjection.commonModule
import com.educamadrid.cloudeducamadrid.dependecyinjection.localDataSourceModule
import com.educamadrid.cloudeducamadrid.dependecyinjection.remoteDataSourceModule
import com.educamadrid.cloudeducamadrid.dependecyinjection.repositoryModule
import com.educamadrid.cloudeducamadrid.dependecyinjection.useCaseModule
import com.educamadrid.cloudeducamadrid.dependecyinjection.viewModelModule
import com.educamadrid.cloudeducamadrid.extensions.createNotificationChannel
import com.educamadrid.cloudeducamadrid.lib.common.OwnCloudClient
import com.educamadrid.cloudeducamadrid.lib.common.SingleSessionManager
import com.educamadrid.cloudeducamadrid.presentation.ui.migration.StorageMigrationActivity
import com.educamadrid.cloudeducamadrid.presentation.ui.security.BiometricActivity
import com.educamadrid.cloudeducamadrid.presentation.ui.security.BiometricManager
import com.educamadrid.cloudeducamadrid.presentation.ui.security.PassCodeActivity
import com.educamadrid.cloudeducamadrid.presentation.ui.security.PassCodeManager
import com.educamadrid.cloudeducamadrid.presentation.ui.security.PatternActivity
import com.educamadrid.cloudeducamadrid.presentation.ui.security.PatternManager
import com.educamadrid.cloudeducamadrid.presentation.ui.settings.fragments.SettingsLogsFragment.Companion.PREFERENCE_ENABLE_LOGGING
import com.educamadrid.cloudeducamadrid.providers.LogsProvider
import com.educamadrid.cloudeducamadrid.ui.activity.WhatsNewActivity
import com.educamadrid.cloudeducamadrid.utils.DOWNLOAD_NOTIFICATION_CHANNEL_ID
import com.educamadrid.cloudeducamadrid.utils.FILE_SYNC_CONFLICT_CHANNEL_ID
import com.educamadrid.cloudeducamadrid.utils.FILE_SYNC_NOTIFICATION_CHANNEL_ID
import com.educamadrid.cloudeducamadrid.utils.MEDIA_SERVICE_NOTIFICATION_CHANNEL_ID
import com.educamadrid.cloudeducamadrid.utils.UPLOAD_NOTIFICATION_CHANNEL_ID
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import timber.log.Timber

/**
 * Main Application of the project
 *
 *
 * Contains methods to build the "static" strings. These strings were before constants in different
 * classes
 */
class MainApp : Application() {
    // EducaMadrid
//    protected val URL_ALERT = "https://jsonkeeper.com/" // Pruebas

    override fun onCreate() {
        super.onCreate()
       //EducaMadrid
//        loadDatos()

        appContext = applicationContext

        startLogsIfEnabled()

        OwnCloudClient.setContext(appContext)

        createNotificationChannels()

        SingleSessionManager.setUserAgent(userAgent)

        // initialise thumbnails cache on background thread
        ThumbnailsCacheManager.InitDiskCacheTask().execute()

        // register global protection with pass code, pattern lock and biometric lock
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Timber.d("${activity.javaClass.simpleName} onCreate(Bundle) starting")
                val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val passCodeEnabled = preferences.getBoolean(PassCodeActivity.PREFERENCE_SET_PASSCODE, false)
                val patternCodeEnabled = preferences.getBoolean(PatternActivity.PREFERENCE_SET_PATTERN, false)
                if (!enabledLogging) {
                    // To enable biometric you need to enable passCode or pattern, so no need to add check to if
                    if (passCodeEnabled || patternCodeEnabled) {
                        activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    } else {
                        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                } // else, let it go, or taking screenshots & testing will not be possible

                // If there's any lock protection, don't show wizard at this point, show it when lock activities
                // have finished
                if (activity !is PassCodeActivity &&
                    activity !is PatternActivity &&
                    activity !is BiometricActivity
                ) {
                    StorageMigrationActivity.runIfNeeded(activity)
                    WhatsNewActivity.runIfNeeded(activity)
                }

                PreferenceManager.migrateFingerprintToBiometricKey(applicationContext)
                PreferenceManager.deleteOldSettingsPreferences(applicationContext)
            }

            override fun onActivityStarted(activity: Activity) {
                Timber.v("${activity.javaClass.simpleName} onStart() starting")
                PassCodeManager.onActivityStarted(activity)
                PatternManager.onActivityStarted(activity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    BiometricManager.onActivityStarted(activity)
                }
            }

            override fun onActivityResumed(activity: Activity) {
                Timber.v("${activity.javaClass.simpleName} onResume() starting")
            }

            override fun onActivityPaused(activity: Activity) {
                Timber.v("${activity.javaClass.simpleName} onPause() ending")
            }

            override fun onActivityStopped(activity: Activity) {
                Timber.v("${activity.javaClass.simpleName} onStop() ending")
                PassCodeManager.onActivityStopped(activity)
                PatternManager.onActivityStopped(activity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    BiometricManager.onActivityStopped(activity)
                }
                if (activity is PassCodeActivity ||
                    activity is PatternActivity ||
                    activity is BiometricActivity
                ) {
                    WhatsNewActivity.runIfNeeded(activity)
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Timber.v("${activity.javaClass.simpleName} onSaveInstanceState(Bundle) starting")
            }

            override fun onActivityDestroyed(activity: Activity) {
                Timber.v("${activity.javaClass.simpleName} onDestroy() ending")
            }
        })

        initDependencyInjection()
    }

    /**
     * EducaMadrid
     * method loadDatos
     * Method that read the JSON and pass the object @see Alert method @see launchAlert()
     */
//    private fun loadDatos() {
//        // Building retrofit
//        val retrofit = Retrofit.Builder()
//            .baseUrl(URL_ALERT)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val alertService = retrofit.create(AlertService::class.java)
//
//        // Building call
//        val callAsync = alertService.alert
//
//        // calling the API
//        callAsync.enqueue(object : Callback<Alert?> {
//            override fun onResponse(call: Call<Alert?>, response: Response<Alert?>) {
//                // API response is successful
//                if (response.isSuccessful) {
//                    Log.d("RESPUESTA", "Holaaaaaaaaa");
//                    val alert = response.body()
//                    if (alert!!.getmContent() != "") { // No print alert with content = ""
//                        if (alert.ismActive()) { // No print alert with active = false
//                            for (app in alert.getmApps()) {
//                                if (app.contains("Plataforma") || app.contains("appCloud")) {
//                                    if (alert.getmFrom() != null && alert.getmTo() != null) {
//                                        // No print alert if LocalDateTime.now is not between alert.from and alert.to
//                                        if (!LocalDateTime.now().isBefore(alert.getmFrom()) && !LocalDateTime.now().isAfter(
//                                                alert.getmTo()
//                                            )
//                                        ) {
//                                            launchAlert(alert)
//                                            //showAlertDialog(alert)
//                                        }
//                                    } else {
//
//                                        launchAlert(alert)
//                                        //showAlertDialog(alert)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    Toast.makeText(applicationContext, "Ha habido un error llamando a la API", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<Alert?>, t: Throwable) {
//                // API response is failure
//                Toast.makeText(applicationContext, "Ha habido un error llamando a la API", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

    /**
     * method launchAlert
     * @param alert
     * Method that print alert
     */
//    private fun launchAlert(alert: Alert?) {
//        //Toast.makeText(applicationContext, "Va todo chachi", Toast.LENGTH_SHORT).show()
//        val builder = AlertDialog.Builder(ContextThemeWrapper(this@MainApp, R.style.Theme_ownCloud_Toolbar))
//
//        // Create Title Alert
//        //val titleView = TextView(applicationContext)
//        //titleView.text = alert!!.getmTitle()
//        //titleView.setPadding(20, 30, 20, 30)
//        //titleView.textSize = 20f
//        // Depending on the type, the title has a different color (type is default or news)
//        /*if (alert.getmType() == "default") {
//            titleView.setBackgroundColor(Color.parseColor("#FFFFBB33")) // Yellow
//        } else {
//            titleView.setBackgroundColor(Color.RED) // Red
//        }*/
//        //titleView.setTextColor(Color.WHITE)
//        //val view = LayoutInflater.from(this).inflate(R.layout.alert_dialog_educamadrid, null)
//
//        //builder.setView(view)
//        //builder.setCancelable(false)
//        //builder.setCustomTitle(titleView)
//        if (alert != null) {
//            builder.setMessage(alert.getmContent())
//        }
//        if (alert != null) {
//            if (alert.ismClosable()) { // Check closable in JSON
//                //builder.setPositiveButton(R.string.btnOk, (dialog, which) -> dialog.dismiss());
//                builder.setPositiveButton("Ok") { dialog, which -> dialog.dismiss() }
//            }
//        }
//        /*val dialog: Dialog = builder.create()
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.show()*/
//        builder.create().show()
//    }
//
//
//    /*@SuppressLint("RestrictedApi")
//    open fun showAlertDialog(alert: Alert) {
//        /*androidx.appcompat.app.AlertDialog.Builder(this@MainApp
//        )
//            .setMessage("Lo que sea")
//            //.setPositiveButton("Ok")
//            .setCancelable(false)
//            .show()*/
//
//        AlertDialog.Builder(ContextThemeWrapper(R.style.Theme_ownCloud_Toolbar)
//            .setMessage("Lo que sea")
//            //.setPositiveButton("Ok")
//            .setCancelable(false)
//            .show()
//    }*/


    // EducaMadrid


    private fun startLogsIfEnabled() {
        val preferenceProvider = SharedPreferencesProviderImpl(applicationContext)

        if (BuildConfig.DEBUG) {
            val alreadySet = preferenceProvider.containsPreference(PREFERENCE_ENABLE_LOGGING)
            if (!alreadySet) {
                preferenceProvider.putBoolean(PREFERENCE_ENABLE_LOGGING, true)
            }
        }

        enabledLogging = preferenceProvider.getBoolean(PREFERENCE_ENABLE_LOGGING, false)

        if (enabledLogging) {
            LogsProvider(applicationContext).startLogging()
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        createNotificationChannel(
            id = DOWNLOAD_NOTIFICATION_CHANNEL_ID,
            name = getString(R.string.download_notification_channel_name),
            description = getString(R.string.download_notification_channel_description),
            importance = IMPORTANCE_LOW
        )

        createNotificationChannel(
            id = UPLOAD_NOTIFICATION_CHANNEL_ID,
            name = getString(R.string.upload_notification_channel_name),
            description = getString(R.string.upload_notification_channel_description),
            importance = IMPORTANCE_LOW
        )

        createNotificationChannel(
            id = MEDIA_SERVICE_NOTIFICATION_CHANNEL_ID,
            name = getString(R.string.media_service_notification_channel_name),
            description = getString(R.string.media_service_notification_channel_description),
            importance = IMPORTANCE_LOW
        )

        createNotificationChannel(
            id = FILE_SYNC_CONFLICT_CHANNEL_ID,
            name = getString(R.string.file_sync_notification_channel_name),
            description = getString(R.string.file_sync_notification_channel_description),
            importance = IMPORTANCE_LOW
        )

        createNotificationChannel(
            id = FILE_SYNC_NOTIFICATION_CHANNEL_ID,
            name = getString(R.string.file_sync_notification_channel_name),
            description = getString(R.string.file_sync_notification_channel_description),
            importance = IMPORTANCE_LOW
        )
    }

    companion object {
        private const val BETA_VERSION = "beta"

        lateinit var appContext: Context
            private set
        var enabledLogging: Boolean = false
            private set

        /**
         * Next methods give access in code to some constants that need to be defined in string resources to be referred
         * in AndroidManifest.xml file or other xml resource files; or that need to be easy to modify in build time.
         */

        val accountType: String
            get() = appContext.resources.getString(R.string.account_type)

        val versionCode: Int
            get() {
                return try {
                    val thisPackageName = appContext.packageName
                    appContext.packageManager.getPackageInfo(thisPackageName, 0).versionCode
                } catch (e: PackageManager.NameNotFoundException) {
                    0
                }

            }

        val authority: String
            get() = appContext.resources.getString(R.string.authority)

        val authTokenType: String
            get() = appContext.resources.getString(R.string.authority)

        val dataFolder: String
            get() = appContext.resources.getString(R.string.data_folder)

        // user agent
        // Mozilla/5.0 (Android) ownCloud-android/1.7.0
        val userAgent: String
            get() {
                val appString = appContext.resources.getString(R.string.user_agent)
                val packageName = appContext.packageName
                var version = ""

                val pInfo: PackageInfo?
                try {
                    pInfo = appContext.packageManager.getPackageInfo(packageName, 0)
                    if (pInfo != null) {
                        version = pInfo.versionName
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    Timber.e(e, "Trying to get packageName")
                }

                return String.format(appString, version)
            }

        val isBeta: Boolean
            get() {
                var isBeta = false
                try {
                    val packageName = appContext.packageName
                    val packageInfo = appContext.packageManager.getPackageInfo(packageName, 0)
                    val versionName = packageInfo.versionName
                    if (versionName.contains(BETA_VERSION)) {
                        isBeta = true
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    Timber.e(e)
                }

                return isBeta
            }

        fun initDependencyInjection() {
            stopKoin()
            startKoin {
                androidContext(appContext)
                modules(
                    listOf(
                        commonModule,
                        viewModelModule,
                        useCaseModule,
                        repositoryModule,
                        localDataSourceModule,
                        remoteDataSourceModule
                    )
                )
            }
        }
    }
}
