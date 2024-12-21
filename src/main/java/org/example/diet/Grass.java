package org.example.diet;

public enum Grass implements Food{
    GRASS {
        @Override
        public String getType() {
            return "GRASS";
        }
    }
}
