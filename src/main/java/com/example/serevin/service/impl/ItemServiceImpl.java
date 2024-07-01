package com.example.serevin.service.impl;

import com.example.serevin.model.Item;
import com.example.serevin.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final RestTemplate restTemplate;
    private Map<Integer, Item> itemCache;

    @Autowired
    private ItemServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        loadItems();
    }
    @Scheduled(fixedDelay = 86400000) // 24 hours
    private void reloadItems() {
        log.info("Reloading items from the API");
        loadItems();
    }

    private void loadItems() {
        String url = "https://api.opendota.com/api/constants/items";
        Map<String, Map<String, Object>> response = null;
        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (Exception e){
            log.error("Error when getting data about items from the API", e);
        }
        itemCache = new HashMap<>();
        response.forEach((key, valueMap) -> {
            Item item = new Item();
            item.setId((Integer) valueMap.get("id"));
            item.setImageUrl("https://steamcdn-a.akamaihd.net" + (String) valueMap.get("img"));
            itemCache.put(item.getId(), item);
        });
    }

    @Override
    public Optional<Item>  getItemById(int id) {
        Item item = itemCache.get(id);
        if (item == null) {
            loadItems();
            item = itemCache.get(id);
            if (item == null) {
                return Optional.empty();
            }
        }
        return Optional.of(item);
    }
}
