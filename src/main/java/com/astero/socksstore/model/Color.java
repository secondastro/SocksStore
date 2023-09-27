package com.astero.socksstore.model;

public enum Color {
    RED("Красные"), BLACK("Черные"), WHITE("Белые"), YELLOW("Желтые"), BLUE("Синие");
    private final String text;

    Color(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
