package com.example.data.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "telegram_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "userid")
    private long userId;

    @Column(name = "dialog_part")
    private int dialogPart;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(name = "language")
    private String language;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId=" + userId +
                ", dialogPart=" + dialogPart +
                ", isAdmin=" + isAdmin +
                ", language='" + language + '\'' +
                '}';
    }
}
