package com.example.purefreshlaundry.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.purefreshlaundry.models.Login;
import com.example.purefreshlaundry.models.Pelanggan;

public interface LoginRepository extends JpaRepository<Login, Integer>{
    List<Login> findByUsernameAndPassword(String username, String password);    
    boolean existsByUsername(String username);
    Login findByPelanggan(Pelanggan pelanggan);
} 
