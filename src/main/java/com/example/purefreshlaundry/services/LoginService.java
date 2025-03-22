package com.example.purefreshlaundry.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.purefreshlaundry.models.Login;
import com.example.purefreshlaundry.models.Pelanggan;
import com.example.purefreshlaundry.repositories.LoginRepository;

@Service
public class LoginService {
    @Autowired
    private LoginRepository loginRepository;


    public Login findByUsernameAndPassword(String username, String password) {
        List<Login> logins = loginRepository.findByUsernameAndPassword(username, password);
        return logins.stream().findFirst().orElse(null);
    }

    public boolean existsByUsername(String username) {
        return loginRepository.existsByUsername(username);
    }

    public Login findByPelanggan(Pelanggan pelanggan) {
        return loginRepository.findByPelanggan(pelanggan);
    }
}
