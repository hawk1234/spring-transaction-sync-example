package com.mz.example.db.repository;

import com.mz.example.db.model.PersonEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface PersonEmailRepository extends JpaRepository<PersonEmail, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<PersonEmail> findByEmailSentFalse();
}
