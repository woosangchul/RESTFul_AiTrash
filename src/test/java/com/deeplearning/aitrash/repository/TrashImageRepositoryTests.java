package com.deeplearning.aitrash.repository;

import com.deeplearning.aitrash.entity.TrashImage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.UUID;

@SpringBootTest
public class TrashImageRepositoryTests {

    @Autowired
    private TrashImageRepository imageRepository;

    @Test
    @Commit
    public void insertTrashImage(){
        TrashImage trashImage = TrashImage.builder()
                    .uuid(UUID.randomUUID().toString())
                    .imgName("test1.jpg")
                    .build();

        imageRepository.save(trashImage);
        System.out.println("==========================");



    }






}
