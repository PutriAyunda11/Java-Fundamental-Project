package com.example.purefreshlaundry.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Login {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private String role = "pelanggan";

    @OneToOne
    @JoinColumn(name = "pelanggan_id", referencedColumnName = "id")
    private Pelanggan pelanggan;
    public void setRole(String role) {
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("pelanggan")) {
            throw new IllegalArgumentException("Role hanya bisa 'admin' atau 'pelanggan'");
        }
        this.role = role;
    }
}
