package com.example.purefreshlaundry.models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Pembayaran {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "pemesanan_id", referencedColumnName = "id")
    private Pemesanan pemesanan;
    @ManyToOne
    @JoinColumn(name = "jenisPembayaran_id", referencedColumnName = "id")
    private JenisPembayaran jenisPembayaran;
    private String buktiPembayaran;
    private LocalDate tanggalPembayaran;
    private String statusPembayaran;
    }
