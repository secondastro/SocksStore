package com.astero.socksstore.service;

import com.astero.socksstore.model.Socks;

public interface SocksService {
    int addSocks(Socks socks);
    int removeSocks(Socks socks);
    int getQuantity(String color, int size, int cottonMin, int cottonMax);
}
