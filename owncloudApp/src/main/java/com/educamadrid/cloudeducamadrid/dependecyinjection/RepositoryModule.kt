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

import com.educamadrid.cloudeducamadrid.data.authentication.repository.OCAuthenticationRepository
import com.educamadrid.cloudeducamadrid.data.capabilities.repository.OCCapabilityRepository
import com.educamadrid.cloudeducamadrid.data.files.repository.OCFileRepository
import com.educamadrid.cloudeducamadrid.data.folderbackup.FolderBackupRepositoryImpl
import com.educamadrid.cloudeducamadrid.data.oauth.OAuthRepositoryImpl
import com.educamadrid.cloudeducamadrid.data.server.repository.OCServerInfoRepository
import com.educamadrid.cloudeducamadrid.data.sharing.sharees.repository.OCShareeRepository
import com.educamadrid.cloudeducamadrid.data.sharing.shares.repository.OCShareRepository
import com.educamadrid.cloudeducamadrid.data.user.repository.OCUserRepository
import com.educamadrid.cloudeducamadrid.domain.authentication.AuthenticationRepository
import com.educamadrid.cloudeducamadrid.domain.authentication.oauth.OAuthRepository
import com.educamadrid.cloudeducamadrid.domain.camerauploads.FolderBackupRepository
import com.educamadrid.cloudeducamadrid.domain.capabilities.CapabilityRepository
import com.educamadrid.cloudeducamadrid.domain.files.FileRepository
import com.educamadrid.cloudeducamadrid.domain.server.ServerInfoRepository
import com.educamadrid.cloudeducamadrid.domain.sharing.sharees.ShareeRepository
import com.educamadrid.cloudeducamadrid.domain.sharing.shares.ShareRepository
import com.educamadrid.cloudeducamadrid.domain.user.UserRepository
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
