package com.example.purefreshlaundry.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.purefreshlaundry.models.Pelanggan;

public interface PelangganRepository extends JpaRepository<Pelanggan, Integer>{
    Pelanggan findByEmail(String email);
    Pelanggan findByPasswordPemesanan(String passwordPemesanan);
}