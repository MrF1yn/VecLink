package dev.mrflyn.veclinkvelocity.API.placeholderapinative;

import dev.mrflyn.veclinkvelocity.VecLinkMainVelocity;
import dev.mrflyn.veclinkvelocity.API.placeholderapinative.replacer.CharsReplacer;
import dev.mrflyn.veclinkvelocity.API.placeholderapinative.replacer.Replacer;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class PlaceholderAPI {
    @NotNull
    private final Map<String, PlaceholderExpansion> expansions = new ConcurrentHashMap<>();
    private final ReentrantLock expansionsLock = new ReentrantLock();
    private static final Replacer REPLACER_PERCENT = new CharsReplacer(Replacer.Closure.PERCENT);
    private static final Replacer REPLACER_BRACKET = new CharsReplacer(Replacer.Closure.BRACKET);

    public static PlaceholderAPI createPAPI(){
        if(VecLinkMainVelocity.plugin.papi==null){
            return new PlaceholderAPI();
        }
        return null;

    }

    private PlaceholderAPI(){

    }

    public static String setPlaceholdersIfAvailable(Player p, String text){
        if(VecLinkMainVelocity.plugin.papi==null){
            return text;
        }
       return VecLinkMainVelocity.plugin.papi.setPlaceholders(p,text);
    }

    @NotNull
    public String setPlaceholders(final Player player,
                                         @NotNull final String text) {
        return REPLACER_PERCENT.apply(text, player,this::getExpansion);
    }

    @NotNull
    public String setBracketPlaceholders(final Player player,
                                  @NotNull final String text) {
        return REPLACER_BRACKET.apply(text, player, this::getExpansion);
    }

    @Nullable
    public PlaceholderExpansion getExpansion(@NotNull final String identifier) {
        expansionsLock.lock();
        try {
            return expansions.get(identifier.toLowerCase(Locale.ROOT));
        } finally {
            expansionsLock.unlock();
        }
    }

    public void registerPlaceholder(PlaceholderExpansion hook){
        expansionsLock.lock();
        try {
            if(expansions.containsKey(hook.getIdentifier())){
                VecLinkMainVelocity.plugin.log(hook.getIdentifier()+" already registered!");
                
                return;
            }
            expansions.put(hook.getIdentifier(), hook);
        }finally {
            expansionsLock.unlock();
        }
    }

    public void clearHooks(){
        expansions.clear();
    }
}
