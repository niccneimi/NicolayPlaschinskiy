package org.example.animals;

import org.example.diet.Food;
import org.example.diet.Grass;
import org.example.diet.Herbivore;
import org.example.mobility.Land;

public class Camel implements Land, Herbivore{

    @Override
    public void eat(Food food) {
        if (food instanceof Grass) {
            switch ((Grass) food) {
                case GRASS:
                    System.out.println("Camel eats grass");
                    break;
                default:
                    System.out.println("Camel does not eat it");
                    break;
            }
        }
        else {
            System.out.println("Camel does not eat it");
        }
    }

    @Override
    public void walk() {
        System.out.println("Camel walks");
    }
    

}
