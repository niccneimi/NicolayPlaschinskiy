/**
 * Примеры использования CustomArrayList 
 */
public class MainHW3 {
    public static void main(String[] args) {
        CustomArrayList<Integer> list = new CustomArrayList<>();

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        System.out.println("Initial list: " + list);
        System.out.println("Element at index 2: " + list.get(2));
        System.out.println("Removed element: " + list.remove(2));
        System.out.println("List after removal: " + list);

        for (int i = 6; i <= 20; i++) {
            list.add(i);
        }
        System.out.println("List after adding more elements: " + list);

        try {
            list.remove(25);
            System.out.println("Should not reach this point");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Caught IndexOutOfBoundsException");
        }

        try {
            list.add(null);
            System.out.println("Should not reach this point");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught IllegalArgumentException");
        }
    }
}