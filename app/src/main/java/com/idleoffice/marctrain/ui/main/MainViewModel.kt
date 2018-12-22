/*
 * Copyright (c) 2018 IdleOffice Inc.
 *
 * MainViewModel.kt is part of MarcTrainTracker.
 *
 * MarcTrainTracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MarcTrainTracker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.idleoffice.marctrain.ui.main

import com.idleoffice.marctrain.coroutines.ContextProvider
import com.idleoffice.marctrain.ui.base.BaseViewModel


class MainViewModel(
        contextProvider: ContextProvider)
    : BaseViewModel<MainNavigator>(contextProvider)