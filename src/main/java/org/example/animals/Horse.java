package org.example.animals;

import org.example.diet.Food;
import org.example.diet.Grass;
import org.example.diet.Herbivore;
import org.example.mobility.Land;

public class Horse implements Land, Herbivore{

    @Override
    public void eat(Food food) {
        if (food instanceof Grass) {
            switch ((Grass) food) {
                case GRASS:
                    System.out.println("Horse eats grass");
                    break;
                default:
                    System.out.println("Horse does not eat it");
                    break;
            }
        }
        else {
            System.out.println("Horse does not eat it");
        }
    }

    @Override
    public void walk() {
        System.out.println("Horse walk");
    }
}
