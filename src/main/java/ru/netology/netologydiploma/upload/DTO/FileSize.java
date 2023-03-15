package ru.netology.netologydiploma.upload.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileSize {
    String filename;
    Integer Size;

}

