package com.edu.fpt.medtest.controller;

import com.edu.fpt.medtest.model.UploadFileResponse;
import com.edu.fpt.medtest.service.FileStorageService;
import com.edu.fpt.medtest.utils.UploadFileResponseAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.*;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    //upload 1 file
    @PostMapping(value = "/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/saveFile/")
                .path(fileName)
                .toUriString();
        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadImage")
    public ResponseEntity uploadImage2(@RequestBody String base64String) {

        //decode string base64 to file image and copy link image
        //imagePath
        String[] strings = base64String.split(",");
        String extension;
        switch (strings[0]) {//check image's extension
            case "data:image/jpeg;base64":
                extension = "jpeg";
                break;
            case "data:image/png;base64":
                extension = "png";
                break;
            default://should write cases for more images types
                extension = "jpg";
                break;
        }
        //convert base64 string to binary data
        byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
        String fileName = System.currentTimeMillis() + "_medtest_image." + extension;
        //String path = ".\\src\\main\\java\\com\\edu\\fpt\\medtest\\resultImage\\" + fileName;
        String path = "src/main/java/com/edu/fpt/medtest/resultImage/" + fileName;
        File file = new File(path);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveFile/").path(fileName).toUriString();
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        UploadFileResponseAPI uploadFileResponseAPI = new UploadFileResponseAPI();
        uploadFileResponseAPI.setSuccess(true);
        uploadFileResponseAPI.setUri(fileDownloadUri);
        return new ResponseEntity(uploadFileResponseAPI, HttpStatus.OK);
    }

    @GetMapping("/saveFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        //System.out.println(resource.getFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

