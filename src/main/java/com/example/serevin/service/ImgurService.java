package com.example.serevin.service;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

public interface ImgurService {
    String uploadImage(File file) throws IOException, JSONException;
}
