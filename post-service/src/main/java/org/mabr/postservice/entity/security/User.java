package org.mabr.postservice.entity.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mabr.postservice.entity.data.City;
import org.mabr.postservice.entity.data.Currency;
import org.mabr.postservice.entity.data.Image;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(schema = "security", name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private int age;

    @ManyToOne
    private City city;

    @ManyToOne
    private Currency currency;

    @ManyToOne
    private Image avatar;
}