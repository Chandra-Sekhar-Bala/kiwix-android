/*
 * Kiwix Android
 * Copyright (c) 2021 Kiwix <android.kiwix.org>
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.kiwix.kiwixmobile.testutils

import android.view.View
import android.view.ViewGroup
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class Matcher {
  companion object {
    @JvmStatic
    fun childAtPosition(
      parentMatcher: Matcher<View?>,
      position: Int
    ): Matcher<View> {
      return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
          description.appendText("Child at position $position in parent ")
          parentMatcher.describeTo(description)
        }

        public override fun matchesSafely(view: View): Boolean {
          val parent = view.parent
          return parent is ViewGroup && parentMatcher.matches(parent) && view == parent.getChildAt(
            position
          )
        }
      }
    }
  }
}
