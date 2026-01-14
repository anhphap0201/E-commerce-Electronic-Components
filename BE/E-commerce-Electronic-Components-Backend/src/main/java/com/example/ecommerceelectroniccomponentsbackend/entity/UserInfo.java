package com.example.ecommerceelectroniccomponentsbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfo {
    @Id
    @GeneratedValue long id;
    private String name;
    private String email;
    private String password;
    private String roles;
}

