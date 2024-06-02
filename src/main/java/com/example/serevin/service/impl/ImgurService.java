package com.example.serevin.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Service
public class ImgurService {
    private final RestTemplate restTemplate;
    @Value("${imgur.client.id}")
    private String clientId;

    public ImgurService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadImage(File file) throws IOException, JSONException {
        MultipartFile multipartFile = convertFileToMultipartFile(file);
        return uploadToImgur(multipartFile);
    }

    private MultipartFile convertFileToMultipartFile(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        try {
            return new MockMultipartFile("file", file.getName(), "image/png", input);
        } finally {
            input.close();
        }
    }

    private String uploadToImgur(MultipartFile file) throws IOException, JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", "Client-ID " + clientId);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", file.getBytes());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity("https://api.imgur.com/3/upload", requestEntity, String.class);
        } catch (Exception e) {
            log.error("Error occurred while uploading image to Imgur", e);
            throw new IOException("Error occurred while uploading image to Imgur", e);
        }
        return extractUrlFromResponse(response.getBody());
    }

    private String extractUrlFromResponse(String responseBody) throws JSONException {
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject.getJSONObject("data").getString("link");
    }
}