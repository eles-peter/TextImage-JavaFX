package general;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Lum lum1 = new Lum(1);
        Lum lum2 = new Lum(5);
        Lum lum3 = new Lum(0);
        Lum lum4 = new Lum(2);

        List<Lum> lums = new ArrayList<>();
        lums.add(lum1);
        lums.add(lum2);
        lums.add(lum3);
        lums.add(lum4);
        System.out.println(lums);

        Collections.sort(lums);
        System.out.println(lums);






    }

}
