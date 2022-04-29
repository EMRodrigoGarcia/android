/**
 * ownCloud Android client application
 *
 * @author David González Verdugo
 * @author Abel García de Prada
 * Copyright (C) 2021 ownCloud GmbH.
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

package com.educamadrid.cloudeduca.dependecyinjection

import android.accounts.AccountManager
import com.educamadrid.cloudeduca.MainApp.Companion.accountType
import com.educamadrid.cloudeduca.MainApp.Companion.dataFolder
import com.educamadrid.cloudeduca.data.OwncloudDatabase
import com.educamadrid.cloudeduca.data.authentication.datasources.LocalAuthenticationDataSource
import com.educamadrid.cloudeduca.data.authentication.datasources.implementation.OCLocalAuthenticationDataSource
import com.educamadrid.cloudeduca.data.folderbackup.datasources.FolderBackupLocalDataSource
import com.educamadrid.cloudeduca.data.folderbackup.datasources.implementation.FolderBackupLocalDataSourceImpl
import com.educamadrid.cloudeduca.data.capabilities.datasources.LocalCapabilitiesDataSource
import com.educamadrid.cloudeduca.data.capabilities.datasources.implementation.OCLocalCapabilitiesDataSource
import com.educamadrid.cloudeduca.data.preferences.datasources.SharedPreferencesProvider
import com.educamadrid.cloudeduca.data.preferences.datasources.implementation.SharedPreferencesProviderImpl
import com.educamadrid.cloudeduca.data.sharing.shares.datasources.LocalShareDataSource
import com.educamadrid.cloudeduca.data.sharing.shares.datasources.implementation.OCLocalShareDataSource
import com.educamadrid.cloudeduca.data.storage.LocalStorageProvider
import com.educamadrid.cloudeduca.data.storage.ScopedStorageProvider
import com.educamadrid.cloudeduca.data.user.datasources.LocalUserDataSource
import com.educamadrid.cloudeduca.data.user.datasources.implementation.OCLocalUserDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localDataSourceModule = module {
    single { AccountManager.get(androidContext()) }

    single { OwncloudDatabase.getDatabase(androidContext()).capabilityDao() }
    single { OwncloudDatabase.getDatabase(androidContext()).shareDao() }
    single { OwncloudDatabase.getDatabase(androidContext()).userDao() }
    single { OwncloudDatabase.getDatabase(androidContext()).folderBackUpDao() }

    single<SharedPreferencesProvider> { SharedPreferencesProviderImpl(get()) }
    single<LocalStorageProvider> { ScopedStorageProvider(dataFolder, androidContext()) }

    factory<LocalAuthenticationDataSource> { OCLocalAuthenticationDataSource(androidContext(), get(), get(), accountType) }
    factory<LocalCapabilitiesDataSource> { OCLocalCapabilitiesDataSource(get()) }
    factory<LocalShareDataSource> { OCLocalShareDataSource(get()) }
    factory<LocalUserDataSource> { OCLocalUserDataSource(get()) }
    factory<FolderBackupLocalDataSource> { FolderBackupLocalDataSourceImpl(get()) }
}
