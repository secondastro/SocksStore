package com.astero.socksstore.model.Operation;

public enum OperationType {
    ADD("Приёмка"), SELL("Продажа"), WRITE_OFF("Списание");

    private final String text;

    OperationType(String text) {
        this.text = text;
    }
}
