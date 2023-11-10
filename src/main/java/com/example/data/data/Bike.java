package com.example.data.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bikes")
public class Bike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_pl")
    private String namePl;

    @Column(name = "name_ua")
    private String nameUa;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "comment_en")
    private String commentEn;

    @Column(name = "comment_pl")
    private String commentPl;

    @Column(name = "comment_ua")
    private String commentUa;

    @Column(name = "comment_ru")
    private String commentRu;

    @Column(name = "rental")
    private int rental;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "bike_id")
    private List<BikePhoto> photos;

    public Bike() {
    }

    @JsonIgnore
    public boolean isBadBikeData() {
        return nameEn == null || namePl == null || nameUa == null || nameRu == null ||
                commentEn == null || commentPl == null || commentUa == null || commentRu == null || rental == 0 || photos == null;
    }

    @JsonIgnore
    public String toString(String lang) {
        var sb = new StringBuilder();
        switch (lang) {
            case "pl" -> {
                sb.append("==========\n");
                sb.append("Bike name: ").append(this.namePl).append("\n");
                sb.append("Bike comment: \n").append(this.commentPl).append("\n");
                sb.append("Rental: ").append(rental).append("\n\n");
            }
            case "ua" -> {
                sb.append("==========\n");
                sb.append("Bike name: ").append(this.nameUa).append("\n");
                sb.append("Bike comment: \n").append(this.commentUa).append("\n");
                sb.append("Rental: ").append(rental).append("\n\n");
            }
            case "ru" -> {
                sb.append("==========\n");
                sb.append("Bike name: ").append(this.nameRu).append("\n");
                sb.append("Bike comment: \n").append(this.commentRu).append("\n");
                sb.append("Rental: ").append(rental).append("\n\n");
            }
            default -> {
                sb.append("==========\n");
                sb.append("Bike name: ").append(this.nameEn).append("\n");
                sb.append("Bike comment: \n").append(this.commentEn).append("\n");
                sb.append("Rental: ").append(rental).append("\n\n");
            }
        }

        return sb.toString();
    }
}
