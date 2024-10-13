package main.java.main;
import java.util.*;

public class CustomBubbleSort implements Sorter {
    private int maxElements;

    public CustomBubbleSort(int maxElements) {
        this.maxElements = maxElements;
    }

    @Override
    public List<Integer> sort(List<Integer> list) {
        if (list.size() > maxElements) {
            throw new RuntimeException("List size exceeds maximum allowed elements");
        }
        List<Integer> sortedList = new ArrayList<>(list);
        for (int i = 0; i < sortedList.size(); i++) {
            for (int j = i + 1; j < sortedList.size(); j++) {
                if (sortedList.get(i) > sortedList.get(j)) {
                    int temp = sortedList.get(i);
                    sortedList.set(i, sortedList.get(j));
                    sortedList.set(j, temp);
                }
            }
        }
        return sortedList;
    }
}