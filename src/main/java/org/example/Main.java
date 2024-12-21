package org.example;

import org.example.diet.Grass;
import org.example.diet.Meat;
import org.example.animals.*;;

public class Main {
    public static void main(String[] args) {
        Horse horse = new Horse();
        horse.walk();
        horse.eat(Grass.GRASS);
        horse.eat(Meat.FISH);

        Tiger tiger = new Tiger();
        tiger.walk();
        tiger.eat(Meat.BEEF);
        tiger.eat(Meat.FISH);
        tiger.eat(Grass.GRASS);

        Dolphin dolphin = new Dolphin();
        dolphin.swim();
        dolphin.eat(Meat.FISH);
        dolphin.eat(Meat.BEEF);

        Eagle eagle = new Eagle();
        eagle.fly();
        eagle.eat(Meat.BEEF);
        eagle.eat(Meat.FISH);

        Camel camel = new Camel();
        camel.walk();
        camel.eat(Grass.GRASS);
    }
}
