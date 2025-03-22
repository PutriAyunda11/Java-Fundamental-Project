package com.example.purefreshlaundry.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.purefreshlaundry.models.StatusPesanan;
import com.example.purefreshlaundry.repositories.StatusPesananRepository;

@Service
public class StatusPesananService {

    @Autowired
    private StatusPesananRepository repository;

    public List<StatusPesanan> getAllStatus() {
        return repository.findAll();
    }

    public StatusPesanan getStatusById(Integer id){
        return repository.findById(id).orElse(null);
    }    

    public StatusPesanan getStatusByStatus(String status){
        return repository.findByStatus(status);
    }

    public List<StatusPesanan> getStatusPesananKecualiSelesai() {
        return repository.findByStatusNot("Selesai");
    }
    
}
