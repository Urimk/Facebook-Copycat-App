package com.example.facebookapp;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {

        public static Bitmap decodeImageFromBase64(String base64String) {
            // remove js header from base64 image string
            if (base64String.contains(",")) {
                String[] parts = base64String.split(",");
                base64String = parts[1];
            }
            try {
                byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
                // Decode byte array into Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
    }

    public static String encodeImageToBase64(ContentResolver resolver, Uri pfpUri) {
            String basedImage = "";
        try (InputStream inputStream = resolver.openInputStream(pfpUri)){
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] bytes = outputStream.toByteArray();
                basedImage = "data:image/jpg;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
            }

        }
        catch (Exception e) {

        }
        return basedImage;
    }
}
