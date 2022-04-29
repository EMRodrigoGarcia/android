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

package com.educamadrid.cloudeduca.data.sharing.sharees.datasources.implementation

import com.educamadrid.cloudeduca.data.executeRemoteOperation
import com.educamadrid.cloudeduca.data.sharing.sharees.datasources.RemoteShareeDataSource
import com.educamadrid.cloudeduca.data.sharing.sharees.datasources.mapper.RemoteShareeMapper
import com.educamadrid.cloudeduca.domain.sharing.sharees.model.OCSharee
import com.educamadrid.cloudeduca.lib.resources.shares.services.ShareeService

class OCRemoteShareeDataSource(
    private val shareeService: ShareeService,
    private val shareeMapper: RemoteShareeMapper
) : RemoteShareeDataSource {

    override fun getSharees(
        searchString: String,
        page: Int,
        perPage: Int
    ): List<OCSharee> =
        executeRemoteOperation { shareeService.getSharees(searchString, page, perPage) }.let {
            shareeMapper.toModel(it)
        }
}
