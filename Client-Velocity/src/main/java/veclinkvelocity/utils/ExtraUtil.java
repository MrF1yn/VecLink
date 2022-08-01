package veclinkvelocity.utils;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Iterator;

public class ExtraUtil {
    public static <T extends Collection<? super String>> T copyPartialMatches(String token, Iterable<String> originals, T collection) throws UnsupportedOperationException, IllegalArgumentException {
        Validate.notNull(token, "Search token cannot be null");
        Validate.notNull(collection, "Collection cannot be null");
        Validate.notNull(originals, "Originals cannot be null");
        Iterator var4 = originals.iterator();

        while(var4.hasNext()) {
            String string = (String)var4.next();
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }

        return collection;
    }

    public static TextComponent color(String s){
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    public static boolean startsWithIgnoreCase(String string, String prefix) throws IllegalArgumentException, NullPointerException {
        Validate.notNull(string, "Cannot check a null string for a match");
        return string.length() < prefix.length() ? false : string.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
