/**
 * ownCloud Android client application
 *
 * @author David González Verdugo
 * @author Abel García de Prada
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

package com.educamadrid.cloudeduca.dependecyinjection

import com.educamadrid.cloudeduca.datamodel.UploadsStorageManager
import com.educamadrid.cloudeduca.presentation.manager.AvatarManager
import com.educamadrid.cloudeduca.providers.AccountProvider
import com.educamadrid.cloudeduca.providers.ContextProvider
import com.educamadrid.cloudeduca.providers.CoroutinesDispatcherProvider
import com.educamadrid.cloudeduca.providers.LogsProvider
import com.educamadrid.cloudeduca.providers.OCContextProvider
import com.educamadrid.cloudeduca.providers.WorkManagerProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonModule = module {

    single { AvatarManager() }
    single { CoroutinesDispatcherProvider() }
    factory<ContextProvider> { OCContextProvider(androidContext()) }
    single { LogsProvider(get()) }
    single { WorkManagerProvider(androidContext()) }
    single { AccountProvider(androidContext()) }
    single { UploadsStorageManager(androidApplication().contentResolver) }
}
