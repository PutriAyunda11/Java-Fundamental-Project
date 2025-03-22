package com.example.purefreshlaundry.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.purefreshlaundry.models.Pelanggan;
import com.example.purefreshlaundry.models.Pemesanan;
import com.example.purefreshlaundry.models.PesananSatuan;
import com.example.purefreshlaundry.repositories.PemesananRepository;

@Service
public class PemesananService {

    @Autowired
    private PelangganService pelangganService;

    @Autowired
    private PemesananRepository pemesananRepository;

    public Pemesanan tambahPemesanan(Pemesanan pemesanan) {
        pemesanan.setTanggalPemesanan(LocalDate.now());
        Long totalKiloan = pemesanan.getJumlahPesananKiloan() * pemesanan.getHargaPerkilo();
        pemesanan.setTotalPesananKiloan(totalKiloan);
        Long totalSatuan;
        if(pemesanan.getJenisSatuan() != null){
            PesananSatuan pesananSatuan = pemesanan.getJenisSatuan();
            totalSatuan = pemesanan.getJumlahPesananSatuan() * pesananSatuan.getHarga();
        }
        else{
            totalSatuan = pemesanan.getJumlahPesananSatuan() * 0;
        }
        pemesanan.setTotalPesananSatuan(totalSatuan);
        pemesanan.setTotalHarga(totalSatuan + totalKiloan);
        return pemesananRepository.save(pemesanan);
    }    
    public String addPesananPelanggan(Pemesanan pesan, Integer id) {
        Pelanggan pelanggan = pelangganService.findPelangganById(id);
        if (pelanggan == null) {
            return "Password pesanan tidak valid. Pemesanan gagal.";
        }
        pesan.setPelanggan(pelanggan);
        pesan.setTanggalPemesanan(LocalDate.now());
        pemesananRepository.save(pesan);

        return "Pemesanan berhasil disimpan.";
    }

    public Pemesanan getPemesananById(Integer id) {
        return pemesananRepository.findById(id).orElse(null);
    }
    public List<Pemesanan> getRiwayatPemesananSelesai() {
        List<Pemesanan> allPesanan = pemesananRepository.findAll(); 
        return allPesanan.stream()
                .filter(pemesanan ->
                pemesanan.getStatusPemesanan() != null &&
                        pemesanan.getStatusPemesanan().getStatus().equals("Selesai"))
                .collect(Collectors.toList());
    }
    public List<Pemesanan> getRiwayatPemesananSelesaiPelanggan() {
        List<Pemesanan> allPesanan = pemesananRepository.findAll(); 
        return allPesanan.stream()
                .filter(pemesanan ->
                    pemesanan.getPelanggan() != null &&
                    pemesanan.getStatusPemesanan() != null &&
                    pemesanan.getStatusPemesanan().getStatus().equals("Selesai"))
                .collect(Collectors.toList());
    }
    public List<Pemesanan> getRiwayatPemesananSelesaiPelangganById(Integer idPelanggan) {
        List<Pemesanan> allPesanan = pemesananRepository.findAll();
        return allPesanan.stream()
                .filter(pemesanan ->
                    pemesanan.getPelanggan() != null && 
                    pemesanan.getPelanggan().getId().equals(idPelanggan) &&
                    pemesanan.getStatusPemesanan() != null &&
                    pemesanan.getStatusPemesanan().getStatus().equals("Selesai"))
                .collect(Collectors.toList());
    }


    public List<Pemesanan> getRiwayatPemesananPelanggan(Integer idPelanggan) {
        List<Pemesanan> allPesanan = pemesananRepository.findAll(); 
        List<Pemesanan> filteredPesanan = allPesanan.stream()
                .filter(pemesanan -> pemesanan.getPelanggan() != null &&
                        pemesanan.getPelanggan().getId().equals(idPelanggan) &&
                        pemesanan.getStatusPemesanan() != null &&
                        !pemesanan.getStatusPemesanan().getStatus().equals("Selesai"))
                .collect(Collectors.toList());
        System.out.println("Filtered Pemesanan: " + filteredPesanan);
        return filteredPesanan;
    }

    public List<Pemesanan> getRiwayatPemesananNonSelesai() {
        List<Pemesanan> allPesanan = pemesananRepository.findAll(); 
        return allPesanan.stream()
                .filter(pemesanan ->
                pemesanan.getStatusPemesanan() != null &&
                        !pemesanan.getStatusPemesanan().getStatus().equals("Selesai"))
                .collect(Collectors.toList());
    }

    public List<Pemesanan> getRiwayatPemesananNull() {
        List<Pemesanan> allPesanan = pemesananRepository.findAll();
        if (allPesanan.isEmpty()) {
            System.out.println("Tidak ada pemesanan dalam database.");
        }
        return allPesanan.stream()
                .filter(pemesanan -> pemesanan.getJenisSatuan() == null &&
                        pemesanan.getJumlahPesananSatuan() == null &&
                        pemesanan.getTotalPesananSatuan() == null &&
                        pemesanan.getJumlahPesananKiloan() == null &&
                        pemesanan.getTotalPesananKiloan() == null &&
                        pemesanan.getTotalHarga() == null &&
                        pemesanan.getDriver() == null &&
                        pemesanan.getStatusPemesanan() == null)
                .collect(Collectors.toList());
    }

    public Pemesanan updatePemesanan(Pemesanan pemesanan) {
        if (pemesananRepository.existsById(pemesanan.getId())) {
            return pemesananRepository.save(pemesanan);
        }
        return null;
    }

    public void deletePemesananById(Integer id) {
        pemesananRepository.deleteById(id);
    }

}
