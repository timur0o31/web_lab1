package lab1;

import java.util.HashMap;

public class Validator {
    public Validator(){
    }
    public static boolean validate(HashMap<String, String> map1){
        int x = Integer.parseInt(map1.get("x"));
        float y = Float.parseFloat(map1.get("y"));
        int r = Integer.parseInt(map1.get("r"));
        return (x>=-4 && x<=4) && (y>=-3 && y<=5) && (r>=1 && r<=5);

    }

}
