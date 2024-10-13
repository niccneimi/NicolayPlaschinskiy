package main.java.main;
import java.util.*;

public class CustomMergeSort implements Sorter {
    private int maxElements;

    public CustomMergeSort(int maxElements) {
        this.maxElements = maxElements;
    }

    @Override
    public List<Integer> sort(List<Integer> list) {
        if (list.size() > maxElements) {
            throw new RuntimeException("List size exceeds maximum allowed elements");
        }
        List<Integer> sortedList = new ArrayList<>(list);
        Collections.sort(sortedList);
        return sortedList;
    }
}