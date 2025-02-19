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

package com.educamadrid.cloudeduca.data.file.repository

import com.educamadrid.cloudeduca.data.files.datasources.RemoteFileDataSource
import com.educamadrid.cloudeduca.data.files.repository.OCFileRepository
import com.educamadrid.cloudeduca.domain.exceptions.NoConnectionWithServerException
import com.educamadrid.cloudeduca.testutil.OC_SERVER_INFO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class OCFileRepositoryTest {

    private val remoteFileDataSource = mockk<RemoteFileDataSource>(relaxed = true)
    private val ocFileRepository: OCFileRepository = OCFileRepository(remoteFileDataSource)

    @Test
    fun checkPathExistenceExists() {
        every { remoteFileDataSource.checkPathExistence(OC_SERVER_INFO.baseUrl, false) } returns true

        ocFileRepository.checkPathExistence(OC_SERVER_INFO.baseUrl, false)

        verify(exactly = 1) {
            remoteFileDataSource.checkPathExistence(OC_SERVER_INFO.baseUrl, false)
        }
    }

    @Test(expected = NoConnectionWithServerException::class)
    fun checkPathExistenceExistsNoConnection() {
        every { remoteFileDataSource.checkPathExistence(OC_SERVER_INFO.baseUrl, false) } throws NoConnectionWithServerException()

        ocFileRepository.checkPathExistence(OC_SERVER_INFO.baseUrl, false)

        verify(exactly = 1) {
            remoteFileDataSource.checkPathExistence(OC_SERVER_INFO.baseUrl, false)
        }
    }
}
