package com.example.purefreshlaundry.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.purefreshlaundry.models.Driver;
import com.example.purefreshlaundry.models.Pemesanan;
import com.example.purefreshlaundry.repositories.DriverRepository;
import com.example.purefreshlaundry.repositories.PemesananRepository;

import jakarta.annotation.PostConstruct;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PemesananRepository pemesananRepository;

    public List<Driver> getAllDriver() {
        return driverRepository.findAll();
    }

    public void saveDriver(Driver driver) {
        driverRepository.save(driver);
    }

    public void deleteById(Integer driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver tidak ditemukan"));
        List<Pemesanan> pemesanans = pemesananRepository.findByDriver(driver);
        for (Pemesanan pemesanan : pemesanans) {
            pemesanan.setDriver(null);
            pemesananRepository.save(pemesanan);
        }

        driverRepository.delete(driver);
    }

    public Driver findDriverById(Integer id) {
        return driverRepository.findById(id).orElse(null);
    }    

    @PostConstruct
    public void sap() {
        if (driverRepository.count() == 0) {
            driverRepository.save(new Driver(null,"DIDO","0876413293128","089543217822"));
            driverRepository.save(new Driver(null,"AHMAD","089765432187","082364543276"));
            driverRepository.save(new Driver(null,"TARMIJI","085532143267","089654321567"));
            driverRepository.save(new Driver(null,"RENDI","088909875643","088890432111"));
            driverRepository.save(new Driver(null,"RIKO","089908076532","082215643784"));
        }

    }
}
