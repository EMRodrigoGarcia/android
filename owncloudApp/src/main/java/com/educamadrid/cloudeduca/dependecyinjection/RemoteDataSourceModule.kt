/**
 * ownCloud Android client application
 *
 * @author David González Verdugo
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

import com.educamadrid.cloudeduca.MainApp
import com.educamadrid.cloudeduca.R
import com.educamadrid.cloudeduca.authentication.AccountUtils
import com.educamadrid.cloudeduca.data.ClientManager
import com.educamadrid.cloudeduca.data.authentication.datasources.RemoteAuthenticationDataSource
import com.educamadrid.cloudeduca.data.authentication.datasources.implementation.OCRemoteAuthenticationDataSource
import com.educamadrid.cloudeduca.data.capabilities.datasources.RemoteCapabilitiesDataSource
import com.educamadrid.cloudeduca.data.capabilities.datasources.implementation.OCRemoteCapabilitiesDataSource
import com.educamadrid.cloudeduca.data.capabilities.datasources.mapper.RemoteCapabilityMapper
import com.educamadrid.cloudeduca.data.files.datasources.RemoteFileDataSource
import com.educamadrid.cloudeduca.data.files.datasources.implementation.OCRemoteFileDataSource
import com.educamadrid.cloudeduca.data.oauth.datasource.RemoteOAuthDataSource
import com.educamadrid.cloudeduca.data.oauth.datasource.impl.RemoteOAuthDataSourceImpl
import com.educamadrid.cloudeduca.data.server.datasources.RemoteServerInfoDataSource
import com.educamadrid.cloudeduca.data.server.datasources.implementation.OCRemoteServerInfoDataSource
import com.educamadrid.cloudeduca.data.sharing.sharees.datasources.RemoteShareeDataSource
import com.educamadrid.cloudeduca.data.sharing.sharees.datasources.implementation.OCRemoteShareeDataSource
import com.educamadrid.cloudeduca.data.sharing.sharees.datasources.mapper.RemoteShareeMapper
import com.educamadrid.cloudeduca.data.sharing.shares.datasources.RemoteShareDataSource
import com.educamadrid.cloudeduca.data.sharing.shares.datasources.implementation.OCRemoteShareDataSource
import com.educamadrid.cloudeduca.data.sharing.shares.datasources.mapper.RemoteShareMapper
import com.educamadrid.cloudeduca.data.user.datasources.RemoteUserDataSource
import com.educamadrid.cloudeduca.data.user.datasources.implementation.OCRemoteUserDataSource
import com.educamadrid.cloudeduca.lib.common.OwnCloudAccount
import com.educamadrid.cloudeduca.lib.common.SingleSessionManager
import com.educamadrid.cloudeduca.lib.resources.files.services.FileService
import com.educamadrid.cloudeduca.lib.resources.files.services.implementation.OCFileService
import com.educamadrid.cloudeduca.lib.resources.oauth.services.OIDCService
import com.educamadrid.cloudeduca.lib.resources.oauth.services.implementation.OCOIDCService
import com.educamadrid.cloudeduca.lib.resources.shares.services.ShareService
import com.educamadrid.cloudeduca.lib.resources.shares.services.ShareeService
import com.educamadrid.cloudeduca.lib.resources.shares.services.implementation.OCShareService
import com.educamadrid.cloudeduca.lib.resources.shares.services.implementation.OCShareeService
import com.educamadrid.cloudeduca.lib.resources.status.services.CapabilityService
import com.educamadrid.cloudeduca.lib.resources.status.services.ServerInfoService
import com.educamadrid.cloudeduca.lib.resources.status.services.implementation.OCCapabilityService
import com.educamadrid.cloudeduca.lib.resources.status.services.implementation.OCServerInfoService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val remoteDataSourceModule = module {
    single { AccountUtils.getCurrentOwnCloudAccount(androidContext()) }
    single { OwnCloudAccount(get(), androidContext()) }
    single { SingleSessionManager.getDefaultSingleton().getClientFor(get(), androidContext()) }

    single { ClientManager(get(), get(), get(), MainApp.accountType) }

    single<CapabilityService> { OCCapabilityService(get()) }
    single<FileService> { OCFileService(get()) }
    single<ServerInfoService> { OCServerInfoService() }
    single<OIDCService> { OCOIDCService() }
    single<ShareService> { OCShareService(get()) }
    single<ShareeService> { OCShareeService(get()) }

    factory<RemoteAuthenticationDataSource> { OCRemoteAuthenticationDataSource(get()) }
    factory<RemoteCapabilitiesDataSource> { OCRemoteCapabilitiesDataSource(get(), get()) }
    factory<RemoteFileDataSource> { OCRemoteFileDataSource(get()) }
    factory<RemoteOAuthDataSource> { RemoteOAuthDataSourceImpl(get(), get()) }
    factory<RemoteServerInfoDataSource> { OCRemoteServerInfoDataSource(get(), get()) }
    factory<RemoteShareDataSource> { OCRemoteShareDataSource(get(), get()) }
    factory<RemoteShareeDataSource> { OCRemoteShareeDataSource(get(), get()) }
    factory<RemoteUserDataSource> {
        OCRemoteUserDataSource(get(), androidContext().resources.getDimension(R.dimen.file_avatar_size).toInt())
    }

    factory { RemoteCapabilityMapper() }
    factory { RemoteShareMapper() }
    factory { RemoteShareeMapper() }
}
