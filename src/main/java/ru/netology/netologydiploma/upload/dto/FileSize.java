package ru.netology.netologydiploma.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileSize {
    String filename;
    Integer Size;

}

