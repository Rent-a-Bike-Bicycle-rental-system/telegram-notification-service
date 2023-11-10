package com.example.data.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

@Getter
@Setter
@Entity
@Table(name = "applications")
public class Application implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "city_id")
    private int city;

    @Column(name = "comment")
    private String comment;

    @Column(name = "bike_id")
    private int bikeId;

    @Column(name = "time")
    private long timestamp;

    public Application() {}

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", city=" + city +
                ", comment='" + comment + '\'' +
                ", bikeId=" + bikeId +
                '}';
    }


    @JsonIgnore
    public boolean isGoodApplicationData() {
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$", email) &&
                Pattern.matches("^[0-9]{9}$", phone) && name != null && comment != null;

    }

    @Serial
    private static final long serialVersionUID = 3798288615978871567L;
}
