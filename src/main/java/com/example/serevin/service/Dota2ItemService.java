package com.example.serevin.service;

import com.example.serevin.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class Dota2ItemService {
    private final RestTemplate restTemplate;
    private Map<Integer, Item> itemCache;

    @Autowired
    public Dota2ItemService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        loadItems();
    }
    @Scheduled(fixedDelay = 86400000) // 24 hours
    public void reloadItems() {
        loadItems();
    }
    private void loadItems() {
        String url = "https://api.opendota.com/api/constants/items";
        Map<String, Map<String, Object>> response = restTemplate.getForObject(url, Map.class);
        itemCache = new HashMap<>();
        response.forEach((key, valueMap) -> {
            Item item = new Item();
            item.setId((Integer) valueMap.get("id"));
//            item.setDname((String) valueMap.get("dname"));
            item.setImageUrl("https://steamcdn-a.akamaihd.net" + (String) valueMap.get("img"));
            itemCache.put(item.getId(), item);
        });
    }
    public Item getItemById(int id) {
        Item item = itemCache.get(id);
        if (item == null) {
            loadItems();
            item = itemCache.get(id);
            if (item == null) {
                throw new RuntimeException("Item with ID " + id + " not found");
            }
        }
        return item;
    }
}
