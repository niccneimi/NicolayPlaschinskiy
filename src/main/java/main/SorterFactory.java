package main.java.main;

public class SorterFactory {
    public static Sorter createSorter(SorterType type, int maxElements) {
        switch (type) {
            case BUBBLE_SORT:
                return new CustomBubbleSort(maxElements);
            case MERGE_SORT:
                return new CustomMergeSort(maxElements);
            default:
                throw new RuntimeException("Unsupported sorter type");
        }
    }
}