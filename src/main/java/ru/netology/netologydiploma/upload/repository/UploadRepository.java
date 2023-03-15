package ru.netology.netologydiploma.upload.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.netologydiploma.security.entity.User;
import ru.netology.netologydiploma.upload.DTO.FileSize;
import ru.netology.netologydiploma.upload.entity.UploadFile;

import java.util.List;

@Repository
public interface UploadRepository extends JpaRepository<UploadFile, Long> {
    boolean existsByFileNameAndUser(String fileName, User user);

    @Query("select u.link from UploadFile u where u.fileName=:filename and u.user=:user")
    String findLinkByFileNameAndUser(@Param("filename") String filename, @Param("user") User user);

    UploadFile findUploadFileByUserAndFileName(User user, String fileName);

    //ДЛЯ ОГРАНИЧЕНИЯ ВЫБОРКИ УКАЗЫВАЕМ ПАРАМЕТР Pageable
    @Query("select new ru.netology.netologydiploma.upload.DTO.FileSize(u.fileName, u.size) from UploadFile u where u.user=:user")
    List<FileSize> findFileSize(@Param("user")User user, Pageable pageable);

}
