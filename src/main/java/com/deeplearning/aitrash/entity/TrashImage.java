package com.deeplearning.aitrash.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class TrashImage extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long inum;

    private String uuid;
    private String imgName;
    private String path;
    private String predictTrash;
    private double percentCan;
    private double percentGlass;
    private double percentPlastic;


}
