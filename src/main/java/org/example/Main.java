interface Walkable {
    void walk();
}


interface Swimmable {
    void swim();
}

interface Flyable {
    void fly();
}

interface Herbivore {
    void eat(Grass food);
}

interface Carnivore {
    void eat(Meat food);
}

class Grass {}

class Meat {
    private String type;

    public Meat(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

class Horse implements Walkable, Herbivore {
    @Override
    public void walk() {
        System.out.println("Лошадь ходит");
    }

    @Override
    public void eat(Grass food) {
        System.out.println("Лошадь ест траву");
    }
}

class Tiger implements Walkable, Carnivore {
    @Override
    public void walk() {
        System.out.println("Тигр ходит");
    }

    @Override
    public void eat(Meat food) {
        if (food.getType().equals("говядина")) {
            System.out.println("Тигр ест говядину");
        } else {
            System.out.println("Тигр не ест это");
        }
    }
}

class Dolphin implements Swimmable, Carnivore {
    @Override
    public void swim() {
        System.out.println("Дельфин плывет");
    }

    @Override
    public void eat(Meat food) {
        if (food.getType().equals("рыба")) {
            System.out.println("Дельфин ест рыбу");
        } else {
            System.out.println("Дельфин не ест это");
        }
    }
}

class Eagle implements Flyable, Carnivore {
    @Override
    public void fly() {
        System.out.println("Орел летит");
    }

    @Override
    public void eat(Meat food) {
        System.out.println("Орел ест мясо");
    }
}

class Camel implements Walkable, Herbivore {
    @Override
    public void walk() {
        System.out.println("Верблюд ходит");
    }

    @Override
    public void eat(Grass food) {
        System.out.println("Верблюд ест траву");
    }
}

public class Main {
    public static void main(String[] args) {
        Horse horse = new Horse();
        horse.walk();
        horse.eat(new Grass());

        Tiger tiger = new Tiger();
        tiger.walk();
        tiger.eat(new Meat("говядина"));
        tiger.eat(new Meat("рыба"));

        Dolphin dolphin = new Dolphin();
        dolphin.swim();
        dolphin.eat(new Meat("рыба"));
        dolphin.eat(new Meat("говядина"));

        Eagle eagle = new Eagle();
        eagle.fly();
        eagle.eat(new Meat("говядина"));

        Camel camel = new Camel();
        camel.walk();
        camel.eat(new Grass());
    }
}
