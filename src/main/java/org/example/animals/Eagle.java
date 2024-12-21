package org.example.animals;

import org.example.diet.Food;
import org.example.diet.Meat;
import org.example.diet.Predatory;
import org.example.mobility.Flying;

public class Eagle implements Flying, Predatory{

    @Override
    public void eat(Food food) {
        if (food instanceof Meat) {
            switch ((Meat) food) {
                case FISH:
                    System.out.println("Eagle eats fish");
                    break;
                case BEEF:
                    System.out.println("Eagle eats beef");
                    break;
                default:
                    System.out.println("Eagle does not eat it");
                    break;
            }
        }
        else {
            System.out.println("Eagle does not eat it");
        }
    }

    @Override
    public void fly() {
        System.out.println("Eagle flies");
    }
    
}
