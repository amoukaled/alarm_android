/* Copyright (C) 2021  Ali Moukaled
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.abstraktlabs.alarm.models

import com.abstraktlabs.alarm.utils.Constants

enum class ClockFace {
    Stacked,
    Expanded,
    Classic;

    companion object {
        fun from(value: String?): ClockFace {
            return when (value) {
                Constants.STACKED_CLOCK_FACE -> {
                    Stacked
                }
                Constants.EXPANDED_CLOCK_FACE -> {
                    Expanded
                }
                Constants.CLASSIC_CLOCK_FACE -> {
                    Classic
                }
                else -> {
                    Stacked
                }
            }
        }
    }
}

