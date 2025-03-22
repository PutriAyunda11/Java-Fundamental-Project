package com.example.purefreshlaundry.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.purefreshlaundry.models.JenisPembayaran;
import com.example.purefreshlaundry.models.Login;
import com.example.purefreshlaundry.models.PesananSatuan;
import com.example.purefreshlaundry.models.StatusPesanan;
import com.example.purefreshlaundry.repositories.JenisPembayaranRepository;
import com.example.purefreshlaundry.repositories.LoginRepository;
import com.example.purefreshlaundry.repositories.PesananSatuanRepository;
import com.example.purefreshlaundry.repositories.StatusPesananRepository;

import jakarta.annotation.PostConstruct;

@Service
public class InisialisasiService {

    @Autowired
    private JenisPembayaranRepository jenisBayarRepository;

    @Autowired
    private StatusPesananRepository statusPesanRepository;

    @Autowired
    private PesananSatuanRepository pesanSatuanRepository;

    @Autowired
    private LoginService loginService;
    @Autowired
    private  LoginRepository loginRepository;

    @PostConstruct
    public void inisialisasiData() {
        createDefaultAdmin();
        jenisBayar();
        pesanSatuan();
        statusPesan();
    }

    private void createDefaultAdmin() {
        if (!loginService.existsByUsername("adminPFLaundry")) {
            Login admin = new Login();
            admin.setUsername("adminPFLaundry");
            admin.setPassword("adm1923L"); 
            admin.setRole("admin");

            loginRepository.save(admin);
            System.out.println("Admin default berhasil ditambahkan.");
        } else {
            System.out.println("Admin default sudah ada.");
        }
    }

    private void jenisBayar() {
        if (jenisBayarRepository.count() == 0) {
            jenisBayarRepository.save(new JenisPembayaran(null, "Transfer Mandiri", "031358921", "Ayunda pureFresh"));
            jenisBayarRepository.save(new JenisPembayaran(null, "Transfer Bri", "325875491011", "Putri pureFresh"));
            jenisBayarRepository.save(new JenisPembayaran(null, "Transfer Bni", "13267909", "Tiara pureFresh"));
            jenisBayarRepository.save(new JenisPembayaran(null, "Cash", null, null));
        }
    }

    private void statusPesan() {
        if (statusPesanRepository.count() == 0) {
            statusPesanRepository.save(new StatusPesanan(null, "Menunggu"));
            statusPesanRepository.save(new StatusPesanan(null, "Diproses"));
            statusPesanRepository.save(new StatusPesanan(null, "Dalam Perjalanan"));
            statusPesanRepository.save(new StatusPesanan(null, "Selesai"));
        }
    }

    private void pesanSatuan() {
        if (pesanSatuanRepository.count() == 0) {
            pesanSatuanRepository.save(new PesananSatuan(null, "Karpet Kecil/Sedang", 15000));
            pesanSatuanRepository.save(new PesananSatuan(null, "Karpet Besar", 20000));
            pesanSatuanRepository.save(new PesananSatuan(null, "Bed Cover", 12000));
            pesanSatuanRepository.save(new PesananSatuan(null, "Sprei", 9000));
            pesanSatuanRepository.save(new PesananSatuan(null, "Jas/Blazer", 7000));
            pesanSatuanRepository.save(new PesananSatuan(null, "Gorden", 18000));
            pesanSatuanRepository.save(new PesananSatuan(null, "Kebaya / Gaun", 8000));
            pesanSatuanRepository.save(new PesananSatuan(null, "Handuk", 6000));
            pesanSatuanRepository.save(new PesananSatuan(null, "Selimut", 9000));
            pesanSatuanRepository.save(new PesananSatuan(null, "Boneka Kecil/Sedang", 7000));
            pesanSatuanRepository.save(new PesananSatuan(null, "Boneka Besar", 14000));
        }
    }

    
}
