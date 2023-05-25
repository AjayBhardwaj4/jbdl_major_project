package com.bhardwaj.services.epay.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Long userId;

    @Column(unique = true)
    private String phoneNumber;

    private Double balance;

    private String identifierValue;

    @Enumerated(value = EnumType.STRING)
    private UserIdentifier userIdentifier;
}
