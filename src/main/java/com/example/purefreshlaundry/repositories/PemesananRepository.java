package com.example.purefreshlaundry.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.purefreshlaundry.models.Driver;
import com.example.purefreshlaundry.models.Pemesanan;

public interface PemesananRepository extends JpaRepository<Pemesanan, Integer>{
    List<Pemesanan> findByDriver(Driver driver);
    List<Pemesanan> findByPelangganId(Integer pelangganid);
    List<Pemesanan> findByNamaContainingIgnoreCase(String nama);
    List<Pemesanan> findAllByOrderByIdAsc();
    List<Pemesanan> findAllByOrderByTanggalPemesananAsc();
}