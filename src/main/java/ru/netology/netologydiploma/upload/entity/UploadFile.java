package ru.netology.netologydiploma.upload.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiploma.security.entity.User;

import javax.persistence.*;
import java.io.File;

@Entity
@Data
@Table(name = "upload_file")
public class UploadFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "size")
    private Integer size;

    @Lob
    @Column(name = "file")
    private byte[] file;

    public UploadFile() {

    }

   public UploadFile(User user, String fileName, Integer size, byte[] file) {
        this.user = user;
        this.fileName = fileName;
        this.size = size;
        this.file = file;
    }
}
