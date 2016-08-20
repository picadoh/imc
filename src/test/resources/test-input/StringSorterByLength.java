import com.github.picadoh.imc.integration.StringSorter;

import java.util.List;

public class StringSorterByLength implements StringSorter {

    @Override
    public void sort(List<String> strings) {
        new StringLengthSort().sort(strings);
    }

}