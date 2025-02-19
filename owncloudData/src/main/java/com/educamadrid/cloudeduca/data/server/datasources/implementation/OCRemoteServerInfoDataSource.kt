/**
 * ownCloud Android client application
 *
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

package com.educamadrid.cloudeduca.data.server.datasources.implementation

import com.educamadrid.cloudeduca.data.ClientManager
import com.educamadrid.cloudeduca.data.executeRemoteOperation
import com.educamadrid.cloudeduca.data.server.datasources.RemoteServerInfoDataSource
import com.educamadrid.cloudeduca.domain.exceptions.OwncloudVersionNotSupportedException
import com.educamadrid.cloudeduca.domain.exceptions.SpecificServiceUnavailableException
import com.educamadrid.cloudeduca.domain.server.model.AuthenticationMethod
import com.educamadrid.cloudeduca.domain.server.model.ServerInfo
import com.educamadrid.cloudeduca.lib.common.http.HttpConstants
import com.educamadrid.cloudeduca.lib.common.network.WebdavUtils.normalizeProtocolPrefix
import com.educamadrid.cloudeduca.lib.resources.status.RemoteServerInfo
import com.educamadrid.cloudeduca.lib.resources.status.services.ServerInfoService

class OCRemoteServerInfoDataSource(
    private val serverInfoService: ServerInfoService,
    private val clientManager: ClientManager
) : RemoteServerInfoDataSource {

    // Basically, tries to access to the root folder without authorization and analyzes the response.
    fun getAuthenticationMethod(path: String): AuthenticationMethod {
        // Use the same client across the whole login process to keep cookies updated.
        val owncloudClient = clientManager.getClientForUnExistingAccount(path, false)

        // Step 1: check whether the root folder exists.
        val checkPathExistenceResult =
            serverInfoService.checkPathExistence(path, isUserLogged = false, client = owncloudClient)

        // Step 2: Check if server is available (If server is in maintenance for example, throw exception with specific message)
        if (checkPathExistenceResult.httpCode == HttpConstants.HTTP_SERVICE_UNAVAILABLE) {
            throw SpecificServiceUnavailableException(checkPathExistenceResult.httpPhrase)
        }

        // Step 3: look for authentication methods
        var authenticationMethod = AuthenticationMethod.NONE
        if (checkPathExistenceResult.httpCode == HttpConstants.HTTP_UNAUTHORIZED) {
            val authenticateHeaders = checkPathExistenceResult.authenticateHeaders
            var isBasic = false
            authenticateHeaders.forEach { authenticateHeader ->
                if (authenticateHeader.contains(AuthenticationMethod.BEARER_TOKEN.toString())) {
                    return AuthenticationMethod.BEARER_TOKEN  // Bearer top priority
                } else if (authenticateHeader.contains(AuthenticationMethod.BASIC_HTTP_AUTH.toString())) {
                    isBasic = true
                }
            }

            if (isBasic) {
                authenticationMethod = AuthenticationMethod.BASIC_HTTP_AUTH
            }
        }

        return authenticationMethod
    }

    fun getRemoteStatus(path: String): RemoteServerInfo {
        val ownCloudClient = clientManager.getClientForUnExistingAccount(path, true)

        val remoteStatusResult = serverInfoService.getRemoteStatus(path, ownCloudClient)

        val remoteServerInfo = executeRemoteOperation {
            remoteStatusResult
        }

        if (!remoteServerInfo.ownCloudVersion.isServerVersionSupported && !remoteServerInfo.ownCloudVersion.isVersionHidden) {
            throw OwncloudVersionNotSupportedException()
        }

        return remoteServerInfo
    }

    override fun getServerInfo(path: String): ServerInfo {
        // First step: check the status of the server (including its version)
        val remoteServerInfo = getRemoteStatus(path)
        val normalizedProtocolPrefix =
            normalizeProtocolPrefix(remoteServerInfo.baseUrl, remoteServerInfo.isSecureConnection)

        // Second step: get authentication method required by the server
        val authenticationMethod = getAuthenticationMethod(normalizedProtocolPrefix)

        return ServerInfo(
            ownCloudVersion = remoteServerInfo.ownCloudVersion.version,
            baseUrl = normalizedProtocolPrefix,
            authenticationMethod = authenticationMethod,
            isSecureConnection = remoteServerInfo.isSecureConnection
        )
    }
}
