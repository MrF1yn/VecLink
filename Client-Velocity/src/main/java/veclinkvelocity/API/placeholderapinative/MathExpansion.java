package veclinkvelocity.API.placeholderapinative;

import veclinkvelocity.VecLinkMainVelocity;
import com.udojava.evalex.Expression;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MathExpansion extends PlaceholderExpansion{

    private final Map<String, Object> defaults = new HashMap<>();
//    private final Cache<String, Long> invalidPlaceholders = Caffeine.newBuilder()
//        .expireAfterWrite(10, TimeUnit.SECONDS)
//        .build();
    
    public MathExpansion(){
        defaults.put("Precision", 3);
        defaults.put("Rounding", "half-up");
        defaults.put("Debug", false);
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "math";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "Andre_601";
    }

    
    public String onRequest(Player player, @NotNull String identifier){
        
        // Used for warnings.
        String placeholder = "%math_" + identifier + "%";
        
        // Replace any bracket placeholder when possible and replace [prc] with the percent symbol (%)
        identifier = VecLinkMainVelocity.plugin.papi.setBracketPlaceholders(player, identifier);
        identifier = identifier.replace("[prc]", "%");
        
        // Create a null-padded Array by splitting at _
        String[] values = Arrays.copyOf(identifier.split("_", 2), 2);
        
        // Placeholder is %math_<expression>% 
        if(values[1] == null)
            return evaluate(placeholder, values[0], 0, RoundingMode.HALF_UP);
        
        //Placeholder is %math_<text>_% -> Invalid.
        if(values[1].isEmpty()){
            VecLinkMainVelocity.plugin.log(placeholder+ " Not allowed placeholder-syntax '%%math_<text>_%%'");
            
            return null;
        }
        
        // Split values[0] at : and put null where nothing exists.
        String[] options = Arrays.copyOf(values[0].split(":", 2), 2);
        
        int precision;
        RoundingMode roundingMode;
        
        if(isNullOrEmpty(options[0])){
            precision = 0;
        }else{
            try{
                precision = Integer.parseInt(options[0]);
            }catch(NumberFormatException ex){
                // String isn't a valid number -> Invalid placeholder.
                VecLinkMainVelocity.plugin.log(placeholder+" is not a valid number for precision! "+ options[0]);
                return null;
            }
        }
        
        if(isNullOrEmpty(options[1])){
            roundingMode = RoundingMode.HALF_UP;
        }else{
            roundingMode = getRoundingMode(options[1].toLowerCase());
        }
        
        return evaluate(placeholder, values[1], precision, roundingMode);
    }
    
    private String evaluate(String placeholder, String expression, int precision, RoundingMode roundingMode){
        try{
            Expression exp = new Expression(expression);
            BigDecimal result = exp.eval().setScale(precision, roundingMode);
            
            return result.toPlainString();
        }catch(Exception ex){
            // Math evaluation failed -> Invalid placeholder
            VecLinkMainVelocity.plugin.log(placeholder+ " is not a valid Math Expression. "+ expression);
            return null;
        }
    }
    
    private RoundingMode getRoundingMode(String mode){
        switch(mode){
            case "up":
                return RoundingMode.UP;
            
            case "down":
                return RoundingMode.DOWN;
            
            case "ceiling":
                return RoundingMode.CEILING;
                
            case "floor":
                return RoundingMode.FLOOR;
                
            case "half-down":
                return RoundingMode.HALF_DOWN;
            
            case "half-even":
                return RoundingMode.HALF_EVEN;
            
            case "half-up":
            default:
                return RoundingMode.HALF_UP;
        }
    }
    
    private boolean isNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }
}
