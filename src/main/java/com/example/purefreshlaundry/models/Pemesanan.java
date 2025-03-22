package com.example.purefreshlaundry.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
public class Pemesanan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nama;
    private String nomorTelp;
    private String alamat;
    private String email;
    @OneToOne(mappedBy = "pemesanan")
    @ToString.Exclude
    private Pembayaran pembayaran;
    @ManyToOne
    @JoinColumn(name = "jenis_satuan_id", referencedColumnName = "id", nullable = true)
    private PesananSatuan jenisSatuan;
    private Long jumlahPesananSatuan = null;
    private Long totalPesananSatuan = null;
    
    private Long jumlahPesananKiloan = null;
    private Long hargaPerkilo = 8000L;
    private Long totalPesananKiloan = null;

    private LocalDate tanggalPemesanan;
    private Long totalHarga = null;

    @ManyToOne
    @JoinColumn(name = "status_pemesanan_id", referencedColumnName = "id", nullable = true)
    private StatusPesanan statusPemesanan;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id", nullable = true)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "pelanggan_id", referencedColumnName = "id", nullable = true)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private Pelanggan pelanggan;
    
    
    
}
