package com.example.purefreshlaundry.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.purefreshlaundry.models.PesananSatuan;
import com.example.purefreshlaundry.repositories.PesananSatuanRepository;

@Service
public class PesananSatuanService {
    @Autowired
    private PesananSatuanRepository pesanSatuanRepos;

    public List<PesananSatuan> getAllPesananSatuan(){
        return pesanSatuanRepos.findAll();
    }

    public PesananSatuan getPesanSatuanById(Integer id){
        return pesanSatuanRepos.findById(id).orElse(null);
    }
    
}
