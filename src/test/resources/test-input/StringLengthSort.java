import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StringLengthSort {

    public void sort(List<String> strings) {
        Collections.sort(strings, new Comparator<String>() {
            @Override
            public int compare(String left, String right) {
                Integer leftLength = left.length();
                Integer rightLength = right.length();
                return leftLength.compareTo(rightLength);
            }
        });
    }

}