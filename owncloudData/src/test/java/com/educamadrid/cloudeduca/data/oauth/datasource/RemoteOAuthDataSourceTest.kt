/**
 * ownCloud Android client application
 *
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

package com.educamadrid.cloudeduca.data.oauth.datasource

import com.educamadrid.cloudeduca.data.ClientManager
import com.educamadrid.cloudeduca.data.oauth.OC_REMOTE_CLIENT_REGISTRATION_PARAMS
import com.educamadrid.cloudeduca.data.oauth.OC_REMOTE_CLIENT_REGISTRATION_RESPONSE
import com.educamadrid.cloudeduca.data.oauth.OC_REMOTE_OIDC_DISCOVERY_RESPONSE
import com.educamadrid.cloudeduca.data.oauth.OC_REMOTE_TOKEN_REQUEST_PARAMS_ACCESS
import com.educamadrid.cloudeduca.data.oauth.OC_REMOTE_TOKEN_RESPONSE
import com.educamadrid.cloudeduca.data.oauth.datasource.impl.RemoteOAuthDataSourceImpl
import com.educamadrid.cloudeduca.lib.common.OwnCloudClient
import com.educamadrid.cloudeduca.lib.common.operations.RemoteOperationResult
import com.educamadrid.cloudeduca.lib.resources.oauth.responses.ClientRegistrationResponse
import com.educamadrid.cloudeduca.lib.resources.oauth.responses.OIDCDiscoveryResponse
import com.educamadrid.cloudeduca.lib.resources.oauth.responses.TokenResponse
import com.educamadrid.cloudeduca.lib.resources.oauth.services.OIDCService
import com.educamadrid.cloudeduca.testutil.OC_BASE_URL
import com.educamadrid.cloudeduca.testutil.oauth.OC_CLIENT_REGISTRATION
import com.educamadrid.cloudeduca.testutil.oauth.OC_CLIENT_REGISTRATION_REQUEST
import com.educamadrid.cloudeduca.testutil.oauth.OC_OIDC_SERVER_CONFIGURATION
import com.educamadrid.cloudeduca.testutil.oauth.OC_TOKEN_REQUEST_ACCESS
import com.educamadrid.cloudeduca.testutil.oauth.OC_TOKEN_RESPONSE
import com.educamadrid.cloudeduca.utils.createRemoteOperationResultMock
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class RemoteOAuthDataSourceTest {
    private lateinit var remoteOAuthDataSource: RemoteOAuthDataSource

    private val clientManager: ClientManager = mockk(relaxed = true)
    private val ocClientMocked: OwnCloudClient = mockk()

    private val oidcService: OIDCService = mockk()

    @Before
    fun init() {
        every { clientManager.getClientForUnExistingAccount(any(), any()) } returns ocClientMocked

        remoteOAuthDataSource = RemoteOAuthDataSourceImpl(
            clientManager = clientManager,
            oidcService = oidcService,
        )
    }

    @Test
    fun `perform oidc discovery - ok`() {
        val oidcDiscoveryResult: RemoteOperationResult<OIDCDiscoveryResponse> =
            createRemoteOperationResultMock(data = OC_REMOTE_OIDC_DISCOVERY_RESPONSE, isSuccess = true)

        every {
            oidcService.getOIDCServerDiscovery(ocClientMocked)
        } returns oidcDiscoveryResult

        val oidcDiscovery = remoteOAuthDataSource.performOIDCDiscovery(OC_BASE_URL)

        assertNotNull(oidcDiscovery)
        assertEquals(OC_OIDC_SERVER_CONFIGURATION, oidcDiscovery)
    }

    @Test(expected = Exception::class)
    fun `perform oidc discovery - ko`() {
        every {
            oidcService.getOIDCServerDiscovery(ocClientMocked)
        } throws Exception()

        remoteOAuthDataSource.performOIDCDiscovery(OC_BASE_URL)
    }

    @Test
    fun `perform token request - ok`() {
        val tokenResponseResult: RemoteOperationResult<TokenResponse> =
            createRemoteOperationResultMock(data = OC_REMOTE_TOKEN_RESPONSE, isSuccess = true)

        every {
            oidcService.performTokenRequest(ocClientMocked, any())
        } returns tokenResponseResult

        val tokenResponse = remoteOAuthDataSource.performTokenRequest(OC_TOKEN_REQUEST_ACCESS)

        assertNotNull(tokenResponse)
        assertEquals(OC_TOKEN_RESPONSE, tokenResponse)
    }

    @Test(expected = Exception::class)
    fun `perform token request - ko`() {
        every {
            oidcService.performTokenRequest(ocClientMocked, OC_REMOTE_TOKEN_REQUEST_PARAMS_ACCESS)
        } throws Exception()

        remoteOAuthDataSource.performTokenRequest(OC_TOKEN_REQUEST_ACCESS)
    }

    @Test
    fun `register client - ok`() {
        val clientRegistrationResponse: RemoteOperationResult<ClientRegistrationResponse> =
            createRemoteOperationResultMock(data = OC_REMOTE_CLIENT_REGISTRATION_RESPONSE, isSuccess = true)

        every {
            oidcService.registerClientWithRegistrationEndpoint(ocClientMocked, any())
        } returns clientRegistrationResponse

        val clientRegistrationInfo = remoteOAuthDataSource.registerClient(OC_CLIENT_REGISTRATION_REQUEST)

        assertNotNull(clientRegistrationInfo)
        assertEquals(OC_CLIENT_REGISTRATION, clientRegistrationInfo)
    }

    @Test(expected = Exception::class)
    fun `register client - ko`() {
        every {
            oidcService.registerClientWithRegistrationEndpoint(ocClientMocked, OC_REMOTE_CLIENT_REGISTRATION_PARAMS)
        } throws Exception()

        remoteOAuthDataSource.registerClient(OC_CLIENT_REGISTRATION_REQUEST)
    }
}
