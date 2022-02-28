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

import com.educamadrid.cloudeducamadrid.domain.authentication.oauth.OIDCDiscoveryUseCase
import com.educamadrid.cloudeducamadrid.domain.authentication.oauth.RegisterClientUseCase
import com.educamadrid.cloudeducamadrid.domain.authentication.oauth.RequestTokenUseCase
import com.educamadrid.cloudeducamadrid.domain.authentication.usecases.GetBaseUrlUseCase
import com.educamadrid.cloudeducamadrid.domain.authentication.usecases.LoginBasicAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.authentication.usecases.LoginOAuthAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.authentication.usecases.SupportsOAuth2UseCase
import com.educamadrid.cloudeducamadrid.domain.camerauploads.usecases.GetCameraUploadsConfigurationUseCase
import com.educamadrid.cloudeducamadrid.domain.camerauploads.usecases.GetPictureUploadsConfigurationStreamUseCase
import com.educamadrid.cloudeducamadrid.domain.camerauploads.usecases.GetVideoUploadsConfigurationStreamUseCase
import com.educamadrid.cloudeducamadrid.domain.camerauploads.usecases.ResetPictureUploadsUseCase
import com.educamadrid.cloudeducamadrid.domain.camerauploads.usecases.ResetVideoUploadsUseCase
import com.educamadrid.cloudeducamadrid.domain.camerauploads.usecases.SavePictureUploadsConfigurationUseCase
import com.educamadrid.cloudeducamadrid.domain.camerauploads.usecases.SaveVideoUploadsConfigurationUseCase
import com.educamadrid.cloudeducamadrid.domain.capabilities.usecases.GetCapabilitiesAsLiveDataUseCase
import com.educamadrid.cloudeducamadrid.domain.capabilities.usecases.GetStoredCapabilitiesUseCase
import com.educamadrid.cloudeducamadrid.domain.capabilities.usecases.RefreshCapabilitiesFromServerAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.server.usecases.GetServerInfoAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.sharing.sharees.GetShareesAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.sharing.shares.usecases.CreatePrivateShareAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.sharing.shares.usecases.CreatePublicShareAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.sharing.shares.usecases.DeleteShareAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.sharing.shares.usecases.EditPrivateShareAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.sharing.shares.usecases.EditPublicShareAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.sharing.shares.usecases.GetShareAsLiveDataUseCase
import com.educamadrid.cloudeducamadrid.domain.sharing.shares.usecases.GetSharesAsLiveDataUseCase
import com.educamadrid.cloudeducamadrid.domain.sharing.shares.usecases.RefreshSharesFromServerAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.user.usecases.GetStoredQuotaUseCase
import com.educamadrid.cloudeducamadrid.domain.user.usecases.GetUserAvatarAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.user.usecases.GetUserInfoAsyncUseCase
import com.educamadrid.cloudeducamadrid.domain.user.usecases.RefreshUserQuotaFromServerAsyncUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // Authentication
    factory { LoginBasicAsyncUseCase(get()) }
    factory { LoginOAuthAsyncUseCase(get()) }
    factory { SupportsOAuth2UseCase(get()) }
    factory { GetBaseUrlUseCase(get()) }

    // OAuth
    factory { OIDCDiscoveryUseCase(get()) }
    factory { RequestTokenUseCase(get()) }
    factory { RegisterClientUseCase(get()) }

    // Capabilities
    factory { GetCapabilitiesAsLiveDataUseCase(get()) }
    factory { GetStoredCapabilitiesUseCase(get()) }
    factory { RefreshCapabilitiesFromServerAsyncUseCase(get()) }

    // Sharing
    factory { GetShareesAsyncUseCase(get()) }
    factory { GetSharesAsLiveDataUseCase(get()) }
    factory { GetShareAsLiveDataUseCase(get()) }
    factory { RefreshSharesFromServerAsyncUseCase(get()) }
    factory { CreatePrivateShareAsyncUseCase(get()) }
    factory { EditPrivateShareAsyncUseCase(get()) }
    factory { CreatePublicShareAsyncUseCase(get()) }
    factory { EditPublicShareAsyncUseCase(get()) }
    factory { DeleteShareAsyncUseCase(get()) }

    // User
    factory { GetStoredQuotaUseCase(get()) }
    factory { GetUserInfoAsyncUseCase(get()) }
    factory { RefreshUserQuotaFromServerAsyncUseCase(get()) }
    factory { GetUserAvatarAsyncUseCase(get()) }

    // Server
    factory { GetServerInfoAsyncUseCase(get()) }

    // Camera Uploads
    factory { GetCameraUploadsConfigurationUseCase(get()) }
    factory { SavePictureUploadsConfigurationUseCase(get()) }
    factory { SaveVideoUploadsConfigurationUseCase(get()) }
    factory { ResetPictureUploadsUseCase(get()) }
    factory { ResetVideoUploadsUseCase(get()) }
    factory { GetPictureUploadsConfigurationStreamUseCase(get()) }
    factory { GetVideoUploadsConfigurationStreamUseCase(get()) }
}
