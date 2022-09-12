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
package com.educamadrid.cloudeduca

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import com.educamadrid.cloudeduca.data.preferences.datasources.implementation.SharedPreferencesProviderImpl
import com.educamadrid.cloudeduca.datamodel.ThumbnailsCacheManager
import com.educamadrid.cloudeduca.db.PreferenceManager
import com.educamadrid.cloudeduca.dependecyinjection.commonModule
import com.educamadrid.cloudeduca.dependecyinjection.localDataSourceModule
import com.educamadrid.cloudeduca.dependecyinjection.remoteDataSourceModule
import com.educamadrid.cloudeduca.dependecyinjection.repositoryModule
import com.educamadrid.cloudeduca.dependecyinjection.useCaseModule
import com.educamadrid.cloudeduca.dependecyinjection.viewModelModule
import com.educamadrid.cloudeduca.extensions.createNotificationChannel
import com.educamadrid.cloudeduca.lib.common.OwnCloudClient
import com.educamadrid.cloudeduca.lib.common.SingleSessionManager
import com.educamadrid.cloudeduca.presentation.ui.migration.StorageMigrationActivity
import com.educamadrid.cloudeduca.presentation.ui.security.BiometricActivity
import com.educamadrid.cloudeduca.presentation.ui.security.BiometricManager
import com.educamadrid.cloudeduca.presentation.ui.security.PassCodeActivity
import com.educamadrid.cloudeduca.presentation.ui.security.PassCodeManager
import com.educamadrid.cloudeduca.presentation.ui.security.PatternActivity
import com.educamadrid.cloudeduca.presentation.ui.security.PatternManager
import com.educamadrid.cloudeduca.presentation.ui.settings.fragments.SettingsLogsFragment.Companion.PREFERENCE_ENABLE_LOGGING
import com.educamadrid.cloudeduca.providers.LogsProvider
import com.educamadrid.cloudeduca.services.AlertService
import com.educamadrid.cloudeduca.ui.activity.WhatsNewActivity
import com.educamadrid.cloudeduca.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import timber.log.Timber
import kotlin.concurrent.thread

/**
 * Main Application of the project
 *
 *
 * Contains methods to build the "static" strings. These strings were before constants in different
 * classes
 */
class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()

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
//                loadDatos()
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
