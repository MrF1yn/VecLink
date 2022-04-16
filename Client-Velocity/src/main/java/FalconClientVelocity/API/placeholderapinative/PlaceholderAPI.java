package FalconClientVelocity.API.placeholderapinative;

import FalconClientVelocity.API.placeholderapinative.replacer.CharsReplacer;
import FalconClientVelocity.API.placeholderapinative.replacer.Replacer;
import FalconClientVelocity.FalconMainVelocity;
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
        if(FalconMainVelocity.plugin.papi==null){
            return new PlaceholderAPI();
        }
        return null;

    }

    private PlaceholderAPI(){

    }

    public static String setPlaceholdersIfAvailable(Player p, String text){
        if(FalconMainVelocity.plugin.papi==null){
            return text;
        }
       return FalconMainVelocity.plugin.papi.setPlaceholders(p,text);
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
                FalconMainVelocity.plugin.log(hook.getIdentifier()+" already registered!");
                
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
