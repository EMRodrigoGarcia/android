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

package com.educamadrid.cloudeducamadrid.dependecyinjection

import com.educamadrid.cloudeducamadrid.MainApp
import com.educamadrid.cloudeducamadrid.R
import com.educamadrid.cloudeducamadrid.authentication.AccountUtils
import com.educamadrid.cloudeducamadrid.data.ClientManager
import com.educamadrid.cloudeducamadrid.data.authentication.datasources.RemoteAuthenticationDataSource
import com.educamadrid.cloudeducamadrid.data.authentication.datasources.implementation.OCRemoteAuthenticationDataSource
import com.educamadrid.cloudeducamadrid.data.capabilities.datasources.RemoteCapabilitiesDataSource
import com.educamadrid.cloudeducamadrid.data.capabilities.datasources.implementation.OCRemoteCapabilitiesDataSource
import com.educamadrid.cloudeducamadrid.data.capabilities.datasources.mapper.RemoteCapabilityMapper
import com.educamadrid.cloudeducamadrid.data.files.datasources.RemoteFileDataSource
import com.educamadrid.cloudeducamadrid.data.files.datasources.implementation.OCRemoteFileDataSource
import com.educamadrid.cloudeducamadrid.data.oauth.datasource.RemoteOAuthDataSource
import com.educamadrid.cloudeducamadrid.data.oauth.datasource.impl.RemoteOAuthDataSourceImpl
import com.educamadrid.cloudeducamadrid.data.server.datasources.RemoteServerInfoDataSource
import com.educamadrid.cloudeducamadrid.data.server.datasources.implementation.OCRemoteServerInfoDataSource
import com.educamadrid.cloudeducamadrid.data.sharing.sharees.datasources.RemoteShareeDataSource
import com.educamadrid.cloudeducamadrid.data.sharing.sharees.datasources.implementation.OCRemoteShareeDataSource
import com.educamadrid.cloudeducamadrid.data.sharing.sharees.datasources.mapper.RemoteShareeMapper
import com.educamadrid.cloudeducamadrid.data.sharing.shares.datasources.RemoteShareDataSource
import com.educamadrid.cloudeducamadrid.data.sharing.shares.datasources.implementation.OCRemoteShareDataSource
import com.educamadrid.cloudeducamadrid.data.sharing.shares.datasources.mapper.RemoteShareMapper
import com.educamadrid.cloudeducamadrid.data.user.datasources.RemoteUserDataSource
import com.educamadrid.cloudeducamadrid.data.user.datasources.implementation.OCRemoteUserDataSource
import com.educamadrid.cloudeducamadrid.lib.common.OwnCloudAccount
import com.educamadrid.cloudeducamadrid.lib.common.SingleSessionManager
import com.educamadrid.cloudeducamadrid.lib.resources.files.services.FileService
import com.educamadrid.cloudeducamadrid.lib.resources.files.services.implementation.OCFileService
import com.educamadrid.cloudeducamadrid.lib.resources.oauth.services.OIDCService
import com.educamadrid.cloudeducamadrid.lib.resources.oauth.services.implementation.OCOIDCService
import com.educamadrid.cloudeducamadrid.lib.resources.shares.services.ShareService
import com.educamadrid.cloudeducamadrid.lib.resources.shares.services.ShareeService
import com.educamadrid.cloudeducamadrid.lib.resources.shares.services.implementation.OCShareService
import com.educamadrid.cloudeducamadrid.lib.resources.shares.services.implementation.OCShareeService
import com.educamadrid.cloudeducamadrid.lib.resources.status.services.CapabilityService
import com.educamadrid.cloudeducamadrid.lib.resources.status.services.ServerInfoService
import com.educamadrid.cloudeducamadrid.lib.resources.status.services.implementation.OCCapabilityService
import com.educamadrid.cloudeducamadrid.lib.resources.status.services.implementation.OCServerInfoService
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
