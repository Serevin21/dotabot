package com.example.serevin.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
public class ImageMergerService {
    public File mergeImages(List<String> itemUrls, String neutralItemUrl) throws IOException {
        if ((itemUrls == null || itemUrls.isEmpty()) && (neutralItemUrl == null || neutralItemUrl.isEmpty())) {
            return createEmptyImage();
        }

        int width = 0, height = 0;
        BufferedImage[] images = new BufferedImage[itemUrls.size() + (neutralItemUrl != null && !neutralItemUrl.isEmpty() ? 1 : 0)];
        int index = 0;

        // Загрузка всех изображений и расчет общей ширины и максимальной высоты
        for (String url : itemUrls) {
            try {
                BufferedImage image = ImageIO.read(new URL(url));
                images[index++] = image;
                width += image.getWidth();
                height = Math.max(height, image.getHeight());
            } catch (MalformedURLException e) {
                throw new MalformedURLException("Invalid URL: " + url);
            } catch (IOException e) {
                throw new IOException("Failed to load image from URL: " + url, e);
            }
        }

        // Загрузка изображения для кружка item_neutral()
        if (neutralItemUrl != null && !neutralItemUrl.isEmpty()) {
            try {
                BufferedImage neutralItemImage = ImageIO.read(new URL(neutralItemUrl));
                images[index] = cropImageToCircle(neutralItemImage); // Обрезаем изображение в форму круга
                width += neutralItemImage.getWidth();
                height = Math.max(height, neutralItemImage.getHeight());
            } catch (MalformedURLException e) {
                throw new MalformedURLException("Invalid URL: " + neutralItemUrl);
            } catch (IOException e) {
                throw new IOException("Failed to load neutral item image from URL: " + neutralItemUrl, e);
            }
        }

        // Создание нового изображения с общей шириной и максимальной высотой
        BufferedImage mergedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = mergedImage.createGraphics();

        // Отрисовка всех изображений в одно
        int x = 0;
        for (BufferedImage image : images) {
            if (image != null) {
                g2.drawImage(image, x, 0, null);
                x += image.getWidth();
            }
        }

        g2.dispose();

        File outputFile = new File("merged_image_items.png"); // Файл сохраняется в каталоге запуска приложения
        ImageIO.write(mergedImage, "PNG", outputFile);
        return outputFile;
    }

    // Метод для обрезки изображения в форму круга
    private BufferedImage cropImageToCircle(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = result.createGraphics();

        // Создаем форму круга
        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, image.getWidth(), image.getHeight());
        g2.setClip(circle);
        g2.drawImage(image, 0, 0, null);

        g2.dispose();
        return result;
    }

    // Метод для создания пустого изображения
    private File createEmptyImage() throws IOException {
        int width = 1;
        int height = 1;
        BufferedImage emptyImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = emptyImage.createGraphics();

        // Устанавливаем белый фон
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        g2.dispose();

        File outputFile = new File("empty_image.png");
        ImageIO.write(emptyImage, "PNG", outputFile);
        return outputFile;
    }
}
