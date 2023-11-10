package com.example.data.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "city")
    private String city;

    public City() {}

    @Override
    @JsonIgnore
    public String toString() {
        return city;
    }

    @JsonIgnore
    public boolean isBadCityData() {
        return city == null;
    }
}
