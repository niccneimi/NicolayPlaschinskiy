package org.example.animals;

import org.example.diet.Food;
import org.example.diet.Meat;
import org.example.diet.Predatory;
import org.example.mobility.Land;

public class Tiger implements Land, Predatory {

    @Override
    public void eat(Food food) {
        if (food instanceof Meat) {
            switch ((Meat) food) {
                case BEEF:
                    System.out.println("Tiger eats beef");
                    break;
                default:
                    System.out.println("Tiger does not eat it");
                    break;
            }
        }
        else {
            System.out.println("Tiger does not eat it");
        }
    }

    @Override
    public void walk() {
        System.out.println("Tiger walk");
    }
}
