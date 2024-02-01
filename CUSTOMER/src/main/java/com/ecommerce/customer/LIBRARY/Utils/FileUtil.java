package com.ecommerce.customer.LIBRARY.Utils;

import com.ecommerce.customer.LIBRARY.Exceptions.ImageProcessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

;

@Component
public class FileUtil {

    private final String IMAGE_UPLOAD_FOLDER = "/home/ubuntu/Vnilusso/src/main/resources/product-images";

    public List<String> uploadToLocalAndReadyImages(List<MultipartFile> multipartFiles) {

        File uploadDir = new File(IMAGE_UPLOAD_FOLDER);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        List<String> readyImages = new ArrayList<>();

        try{
            for(MultipartFile file : multipartFiles){

                String originalFileName = file.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
                String uniqueFilename = UUID.randomUUID().toString()+fileExtension;

                Path destination = Path.of(IMAGE_UPLOAD_FOLDER, uniqueFilename);

                while ((Files.exists(destination))){
                    uniqueFilename = UUID.randomUUID().toString()+fileExtension;
                    destination = Path.of(IMAGE_UPLOAD_FOLDER, uniqueFilename);
                }

                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                readyImages.add(uniqueFilename);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            deleteImagesFromFile(readyImages);
            throw new ImageProcessException("Image process Exception", "There was an Issue processing the Images, try again after sometime.");
        }
        return readyImages;
    }

    public void deleteImagesFromFile(List<String> imagesUrl){
        for(String image : imagesUrl){
            File file = new File(IMAGE_UPLOAD_FOLDER+"/"+image);
            file.delete();
        }
    }

}
