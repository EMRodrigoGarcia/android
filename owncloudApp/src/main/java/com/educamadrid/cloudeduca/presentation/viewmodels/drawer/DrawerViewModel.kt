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

package com.educamadrid.cloudeduca.presentation.viewmodels.drawer

import android.accounts.Account
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educamadrid.cloudeduca.authentication.AccountUtils
import com.educamadrid.cloudeduca.domain.user.model.UserQuota
import com.educamadrid.cloudeduca.domain.user.usecases.GetStoredQuotaUseCase
import com.educamadrid.cloudeduca.domain.utils.Event
import com.educamadrid.cloudeduca.extensions.ViewModelExt.runUseCaseWithResult
import com.educamadrid.cloudeduca.presentation.UIResult
import com.educamadrid.cloudeduca.providers.CoroutinesDispatcherProvider
import com.educamadrid.cloudeduca.utils.FileStorageUtils
import kotlinx.coroutines.launch

class DrawerViewModel(
    private val getStoredQuotaUseCase: GetStoredQuotaUseCase,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _userQuota = MediatorLiveData<Event<UIResult<UserQuota?>>>()
    val userQuota: LiveData<Event<UIResult<UserQuota?>>> = _userQuota

    fun getStoredQuota(
        accountName: String
    ) = runUseCaseWithResult(
        coroutineDispatcher = coroutinesDispatcherProvider.io,
        showLoading = true,
        liveData = _userQuota,
        useCase = getStoredQuotaUseCase,
        useCaseParams = GetStoredQuotaUseCase.Params(accountName = accountName)
    )

    fun getAccounts(context: Context): List<Account> {
        return AccountUtils.getAccounts(context).asList()
    }

    fun getCurrentAccount(context: Context): Account? {
        return AccountUtils.getCurrentOwnCloudAccount(context)
    }

    fun setCurrentAccount(context: Context, accountName: String): Boolean {
        return AccountUtils.setCurrentOwnCloudAccount(context, accountName)
    }

    fun deleteUnusedUserDirs(accounts: Array<Account>) {
        viewModelScope.launch(coroutinesDispatcherProvider.io) {
            FileStorageUtils.deleteUnusedUserDirs(accounts)
        }
    }
}
