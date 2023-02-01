package com.astero.socksstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Socks {
    private Color color;
    private int size;
    private int cottonPart;
    private int quantity;


}
