package com.example.purefreshlaundry.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.purefreshlaundry.models.Pembayaran;

public interface PembayaranRepository extends JpaRepository<Pembayaran, Integer>{
    
}