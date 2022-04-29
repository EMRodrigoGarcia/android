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

import com.educamadrid.cloudeduca.data.authentication.repository.OCAuthenticationRepository
import com.educamadrid.cloudeduca.data.capabilities.repository.OCCapabilityRepository
import com.educamadrid.cloudeduca.data.files.repository.OCFileRepository
import com.educamadrid.cloudeduca.data.folderbackup.FolderBackupRepositoryImpl
import com.educamadrid.cloudeduca.data.oauth.OAuthRepositoryImpl
import com.educamadrid.cloudeduca.data.server.repository.OCServerInfoRepository
import com.educamadrid.cloudeduca.data.sharing.sharees.repository.OCShareeRepository
import com.educamadrid.cloudeduca.data.sharing.shares.repository.OCShareRepository
import com.educamadrid.cloudeduca.data.user.repository.OCUserRepository
import com.educamadrid.cloudeduca.domain.authentication.AuthenticationRepository
import com.educamadrid.cloudeduca.domain.authentication.oauth.OAuthRepository
import com.educamadrid.cloudeduca.domain.camerauploads.FolderBackupRepository
import com.educamadrid.cloudeduca.domain.capabilities.CapabilityRepository
import com.educamadrid.cloudeduca.domain.files.FileRepository
import com.educamadrid.cloudeduca.domain.server.ServerInfoRepository
import com.educamadrid.cloudeduca.domain.sharing.sharees.ShareeRepository
import com.educamadrid.cloudeduca.domain.sharing.shares.ShareRepository
import com.educamadrid.cloudeduca.domain.user.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<AuthenticationRepository> { OCAuthenticationRepository(get(), get()) }
    factory<CapabilityRepository> { OCCapabilityRepository(get(), get()) }
    factory<FileRepository> { OCFileRepository(get()) }
    factory<ServerInfoRepository> { OCServerInfoRepository(get()) }
    factory<ShareeRepository> { OCShareeRepository(get()) }
    factory<ShareRepository> { OCShareRepository(get(), get()) }
    factory<UserRepository> { OCUserRepository(get(), get()) }
    factory<OAuthRepository> { OAuthRepositoryImpl(get()) }
    factory<FolderBackupRepository> { FolderBackupRepositoryImpl(get()) }
}
