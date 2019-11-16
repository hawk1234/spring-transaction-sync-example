package com.mz.example.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "PERSON_EMAIL")
@NoArgsConstructor
public class PersonEmail {

    @Id
    @Column(name = "PERSON_ID")
    private Integer personId;
    @NonNull
    @Column(name = "NAME")
    private String name;
    @NonNull
    @Column(name = "EMAIL_SENT")
    private Boolean emailSent;
}
