/*
 * This file is part of PlaceholderAPI
 *
 * PlaceholderAPI
 * Copyright (c) 2015 - 2021 PlaceholderAPI Team
 *
 * PlaceholderAPI free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlaceholderAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package dev.mrflyn.veclinkvelocity.API.placeholderapinative.replacer;

import dev.mrflyn.veclinkvelocity.API.placeholderapinative.PlaceholderExpansion;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Replacer {

  @NotNull
  String apply(@NotNull final String text, @Nullable final Player player,
      @NotNull final Function<String, @Nullable PlaceholderExpansion> lookup);


  enum Closure {
    BRACKET('{', '}'),
    PERCENT('%', '%');


    public final char head, tail;

    Closure(final char head, final char tail) {
      this.head = head;
      this.tail = tail;
    }
  }

}
