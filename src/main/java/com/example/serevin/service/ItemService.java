package com.example.serevin.service;

import com.example.serevin.model.Item;

import java.util.Optional;

public interface ItemService {
    Optional<Item> getItemById(int id);
}
