package com.example.aitrash_jar.controller;

import com.example.aitrash_jar.dto.UploadImageDTO;
import com.example.aitrash_jar.dto.UploadResultDTO;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
public class UploadController {
    @Value("${com.deeplearning.upload.path}")
    private String uploadPath;

    @Value("${serverIP}")
    private String serverIP;

    @PostMapping("/uploadAjax")
    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles){
        log.info("serverIP: "+ serverIP);
        List<UploadResultDTO> resultDTOList = new ArrayList<>();

        for (MultipartFile uploadFile: uploadFiles){
            byte[] byteImage = null;

            if(uploadFile.getContentType().startsWith("image") == false){
                log.warn("this fil eis not image type");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            String originalName = uploadFile.getOriginalFilename();
            String fileName = originalName.substring(originalName.lastIndexOf("\\")+1);

            log.info("fileName: " + fileName);

            String folderPath = makeFolder();

            String uuid = UUID.randomUUID().toString();

            String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;

            Path savePath = Paths.get(saveName);

            try{
                uploadFile.transferTo(savePath);

                byteImage = Base64.getEncoder().encode(uploadFile.getBytes());

                resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(resultDTOList, HttpStatus.OK);
    }

    private String makeFolder(){
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String folderPath = str.replace("/", File.separator);

        //Make folder -----
        File uploadPathFolder = new File(uploadPath, folderPath);

        if (uploadPathFolder.exists() == false){
            uploadPathFolder.mkdirs();
        }
        return folderPath;

    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFilie(String fileName){
        ResponseEntity<byte[]> result = null;

        try{
            String srcFileName = URLDecoder.decode(fileName, "UTF-8");

            log.info("fileName: " + srcFileName);

            File file = new File(uploadPath + File.separator+ srcFileName);

            log.info("file: " + file);

            HttpHeaders header = new HttpHeaders();

            header.add("Content-type", Files.probeContentType(file.toPath()));

            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;


    }

    @GetMapping("/display1")
    public ResponseEntity<UploadImageDTO> getFilie1(String fileName){
        ResponseEntity<UploadImageDTO> result = null;
        String contentType = null;

        byte[] imageBytes = null;
        String imageString = null;
        JSONObject data = new JSONObject();
        JSONObject jsonObject = null;

        data.put("signature_name", "predict_images");

        try{
            String srcFileName = URLDecoder.decode(fileName, "UTF-8");

            log.info("fileName: " + srcFileName);

            File file = new File(uploadPath + File.separator+ srcFileName);
            imageBytes = FileCopyUtils.copyToByteArray(file);
            contentType = Files.probeContentType(file.toPath());
            Base64.Encoder encoder = Base64.getEncoder();
            imageString = encoder.encodeToString(imageBytes);

            log.info("file: " + file);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JSONObject imageJson = new JSONObject();
        imageJson.put("b64", imageString);

        JSONObject instance = new JSONObject();
        instance.put("images", imageJson);

        JSONArray itemList = new JSONArray();
        itemList.add(instance);

        data.put("instances", itemList);

        try {
            URL url = new URL("http://"+serverIP+":8501/v1/models/inceptionV4:predict");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("POST"); // http 메서드
            conn.setRequestProperty("Content-Type", "application/json"); // header Content-Type 정보
            conn.setDoInput(true); // 서버에 전달할 값이 있다면 true
            conn.setDoOutput(true); // 서버로부터 받는 값이 있다면 true

            // 서버에 데이터 전달
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            bw.write(data.toString()); // 버퍼에 담기
            bw.flush(); // 버퍼에 담긴 데이터 전달
            bw.close();

            // 서버로부터 데이터 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = br.readLine()) != null) { // 읽을 수 있을 때 까지 반복
                sb.append(line);
            }

            JSONParser parser = new JSONParser();
            jsonObject = (JSONObject)parser.parse(sb.toString());

            JSONArray jsonArray = (JSONArray)jsonObject.get("predictions");
            JSONArray scores = (JSONArray)((JSONObject)jsonArray.get(0)).get("scores");

            double predictGlass = (double)scores.get(0);
            double predictPlastic = (double)scores.get(1);
            double predictCan = (double)scores.get(2);


            UploadImageDTO imageDTO = UploadImageDTO.builder()
                    .percentGlass(predictGlass)
                    .percentCan(predictCan)
                    .percentPlastic(predictPlastic)
                    .imageSource(imageBytes)
                    .contentType(contentType)
                    .build();


            result = new ResponseEntity<UploadImageDTO>(imageDTO, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }



        return result;


    }



}
