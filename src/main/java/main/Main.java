package main.java.main;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(5, 2, 8, 3, 1, 4);
        SortingService service = new SortingService();
        List<Integer> sortedList = service.sort(list, SorterType.BUBBLE_SORT);
        System.out.println(sortedList);
    }
}