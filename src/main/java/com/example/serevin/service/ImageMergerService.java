package com.example.serevin.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ImageMergerService {
    File mergeImages(List<String> itemUrls, String neutralItemUrl) throws IOException;
}
