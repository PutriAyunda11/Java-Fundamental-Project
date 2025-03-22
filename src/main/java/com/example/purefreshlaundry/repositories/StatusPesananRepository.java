package com.example.purefreshlaundry.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.purefreshlaundry.models.StatusPesanan;

public interface StatusPesananRepository  extends JpaRepository<StatusPesanan, Integer>{
    StatusPesanan findByStatus(String status);
    List<StatusPesanan> findByStatusNot(String status);
}