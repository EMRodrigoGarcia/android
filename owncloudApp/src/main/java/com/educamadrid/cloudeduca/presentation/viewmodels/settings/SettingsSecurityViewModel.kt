/**
 * ownCloud Android client application
 *
 * @author Juan Carlos Garrote Gascón
 *
 * Copyright (C) 2021 ownCloud GmbH.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.educamadrid.cloudeduca.presentation.viewmodels.settings

import androidx.lifecycle.ViewModel
import com.educamadrid.cloudeduca.R
import com.educamadrid.cloudeduca.data.preferences.datasources.SharedPreferencesProvider
import com.educamadrid.cloudeduca.enums.LockEnforcedType
import com.educamadrid.cloudeduca.enums.LockEnforcedType.Companion.parseFromInteger
import com.educamadrid.cloudeduca.presentation.ui.security.BiometricActivity
import com.educamadrid.cloudeduca.presentation.ui.security.PassCodeActivity
import com.educamadrid.cloudeduca.presentation.ui.security.PatternActivity
import com.educamadrid.cloudeduca.presentation.ui.settings.fragments.SettingsSecurityFragment
import com.educamadrid.cloudeduca.providers.ContextProvider

class SettingsSecurityViewModel(
    private val preferencesProvider: SharedPreferencesProvider,
    private val contextProvider: ContextProvider
) : ViewModel() {

    fun isPatternSet() = preferencesProvider.getBoolean(PatternActivity.PREFERENCE_SET_PATTERN, false)

    fun isPasscodeSet() = preferencesProvider.getBoolean(PassCodeActivity.PREFERENCE_SET_PASSCODE, false)

    fun setPrefLockAccessDocumentProvider(value: Boolean) =
        preferencesProvider.putBoolean(SettingsSecurityFragment.PREFERENCE_LOCK_ACCESS_FROM_DOCUMENT_PROVIDER, value)

    fun setPrefTouchesWithOtherVisibleWindows(value: Boolean) =
        preferencesProvider.putBoolean(SettingsSecurityFragment.PREFERENCE_TOUCHES_WITH_OTHER_VISIBLE_WINDOWS, value)

    fun getBiometricsState(): Boolean = preferencesProvider.getBoolean(BiometricActivity.PREFERENCE_SET_BIOMETRIC, false)

    fun isSecurityEnforcedEnabled() = parseFromInteger(contextProvider.getInt(R.integer.lock_enforced)) != LockEnforcedType.DISABLED
}
