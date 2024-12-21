package org.example.diet;

public enum Meat implements Food{
    BEEF {
        @Override
        public String getType() {
            return "BEEF";
        }
    },
    FISH {
        @Override
        public String getType() {
            return "FISH";
        }
    }
}
