package com.example.data.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bike_photos")
public class BikePhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bike_id")
    private int bikeId;

    @Column(name = "photo_url")
    private String photoUrl;

    public BikePhoto() {}

    @Override
    @JsonIgnore
    public String toString() {
        return "BikePhoto{" +
                "id=" + id +
                ", bikeId=" + bikeId +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}