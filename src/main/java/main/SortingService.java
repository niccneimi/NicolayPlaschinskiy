package main.java.main;
import java.util.*;

public class SortingService {
    public List<Integer> sort(List<Integer> list, SorterType type) {
        Sorter sorter = SorterFactory.createSorter(type, 1000);
        try {
            return sorter.sort(list);
        } catch (RuntimeException e) {
            return sort(list, type);
        }
    }
}