package com.example.serevin.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
@Service
public class ImageMergerService {
    public File mergeImages(List<String> imageUrls) throws IOException {
        int width = 0, height = 0;
        BufferedImage[] images = new BufferedImage[imageUrls.size()];
        int index = 0;

        // Загрузка всех изображений и расчет общей ширины и максимальной высоты
        for (String url : imageUrls) {
            BufferedImage image = ImageIO.read(new URL(url));
            images[index++] = image;
            width += image.getWidth();
            height = Math.max(height, image.getHeight());
        }

        // Создание нового изображения с общей шириной и максимальной высотой
        BufferedImage mergedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = mergedImage.createGraphics();

        // Отрисовка всех изображений в одно
        int x = 0;
        for (BufferedImage image : images) {
            g2.drawImage(image, x, 0, null);
            x += image.getWidth();
        }
        g2.dispose();

        File outputFile = new File("merged_image_items.png"); // Файл сохраняется в каталоге запуска приложения
        ImageIO.write(mergedImage, "PNG", outputFile);
        return outputFile;
    }
}
