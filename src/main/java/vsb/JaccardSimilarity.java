package vsb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaccardSimilarity {

    public static double calculateJaccardSimilarity(List<String> paths1, List<String> paths2) {
        Set<String> set1 = new HashSet<>(paths1);
        Set<String> set2 = new HashSet<>(paths2);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty()) {
            return 0.0; // Vyhneme se dělení nulou
        }

        return (double) intersection.size() / union.size();
    }
}
