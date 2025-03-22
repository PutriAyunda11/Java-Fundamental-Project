package com.example.purefreshlaundry.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.purefreshlaundry.models.Pelanggan;
import com.example.purefreshlaundry.models.Pemesanan;
import com.example.purefreshlaundry.repositories.PelangganRepository;
import com.example.purefreshlaundry.repositories.PemesananRepository;

@Service
public class PelangganService {

    @Autowired
    private PelangganRepository pelangganRepository;

    @Autowired
    private PemesananRepository pemesananRepository;

    public Pelanggan savePelanggan(Pelanggan pelanggan) {
        String password = pelanggan.getPasswordPemesanan();
        
        if (password == null || password.length() < 5 || password.length() > 8) {
            throw new IllegalArgumentException("PasswordPemesanan harus memiliki panjang minimal 5 dan maksimal 8.");
        }
        if (!password.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("PasswordPemesanan hanya boleh berisi angka dan huruf.");
        }
        boolean isPasswordExist = pelangganRepository.findAll()
                .stream()
                .anyMatch(existing -> existing.getPasswordPemesanan().equals(password));
        
        if (isPasswordExist && pelanggan.getId() == null) {
            throw new IllegalArgumentException("PasswordPemesanan sudah digunakan, silakan gunakan password lain.");
        }
        Pelanggan existingPelanggan = null;
        if (pelanggan.getId() != null) {
            existingPelanggan = pelangganRepository.findById(pelanggan.getId()).orElse(null);
        }
    
        if (existingPelanggan != null && !existingPelanggan.getPasswordPemesanan().equals(password)) {
            throw new IllegalArgumentException("PasswordPemesanan sudah digunakan, silakan gunakan password lain.");
        }
    
        if (pelanggan.getTanggalDaftar() == null) {
            pelanggan.setTanggalDaftar(LocalDate.now());
        }
    
        return pelangganRepository.save(pelanggan);
    }

    public Pelanggan findByPasswordPemesanan(String passwordPemesanan) {
        return pelangganRepository.findByPasswordPemesanan(passwordPemesanan);
    }
     public void setPelangganToNull(Integer pelangganId) {
        List<Pemesanan> pemesananList = pemesananRepository.findByPelangganId(pelangganId);
        for (Pemesanan pemesanan : pemesananList) {
            pemesanan.setPelanggan(null);
            pemesananRepository.save(pemesanan);
        }
    }

    public void deletePelanggan(Integer id) {
        pelangganRepository.deleteById(id);
    }

    public List<Pelanggan> getAllPelanggan(){
        return pelangganRepository.findAll();
    }


    public Pelanggan findPelangganById(Integer id ){
        return pelangganRepository.findById(id).orElse(null);
    }

}
