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

package com.educamadrid.cloudeduca.presentation.viewmodels.capabilities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.educamadrid.cloudeduca.domain.capabilities.model.OCCapability
import com.educamadrid.cloudeduca.domain.capabilities.usecases.GetCapabilitiesAsLiveDataUseCase
import com.educamadrid.cloudeduca.domain.capabilities.usecases.RefreshCapabilitiesFromServerAsyncUseCase
import com.educamadrid.cloudeduca.domain.utils.Event
import com.educamadrid.cloudeduca.extensions.ViewModelExt.runUseCaseWithResultAndUseCachedData
import com.educamadrid.cloudeduca.presentation.UIResult
import com.educamadrid.cloudeduca.providers.CoroutinesDispatcherProvider

/**
 * View Model to keep a reference to the capability repository and an up-to-date capability
 */
class OCCapabilityViewModel(
    private val accountName: String,
    getCapabilitiesAsLiveDataUseCase: GetCapabilitiesAsLiveDataUseCase,
    private val refreshCapabilitiesFromServerAsyncUseCase: RefreshCapabilitiesFromServerAsyncUseCase,
    private val coroutineDispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _capabilities = MediatorLiveData<Event<UIResult<OCCapability>>>()
    val capabilities: LiveData<Event<UIResult<OCCapability>>> = _capabilities

    private var capabilitiesLiveData: LiveData<OCCapability?> = getCapabilitiesAsLiveDataUseCase.execute(
        GetCapabilitiesAsLiveDataUseCase.Params(
            accountName = accountName
        )
    )

    init {
        _capabilities.addSource(capabilitiesLiveData) { capabilities ->
            _capabilities.postValue(Event(UIResult.Success(capabilities)))
        }

        refreshCapabilitiesFromNetwork()
    }

    fun refreshCapabilitiesFromNetwork() = runUseCaseWithResultAndUseCachedData(
        coroutineDispatcher = coroutineDispatcherProvider.io,
        cachedData = capabilitiesLiveData.value,
        liveData = _capabilities,
        useCase = refreshCapabilitiesFromServerAsyncUseCase,
        useCaseParams = RefreshCapabilitiesFromServerAsyncUseCase.Params(
            accountName = accountName
        )
    )
}
