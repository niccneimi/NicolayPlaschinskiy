package org.example.animals;

import org.example.diet.Food;
import org.example.diet.Meat;
import org.example.diet.Predatory;
import org.example.mobility.Waterfowl;

public class Dolphin implements Waterfowl, Predatory{

    @Override
    public void eat(Food food) {
        if (food instanceof Meat) {
            switch ((Meat) food) {
                case FISH:
                    System.out.println("Dolphin eats fish");
                    break;
                default:
                    System.out.println("Dolphin does not eat it");
                    break;
            }
        }
        else {
            System.out.println("Dolphin does not eat it");
        }
    }

    @Override
    public void swim() {
        System.out.println("Dolphin swims");
    }
    
}
