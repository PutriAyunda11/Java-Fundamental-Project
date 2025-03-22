package com.example.purefreshlaundry.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.purefreshlaundry.models.Driver;
import com.example.purefreshlaundry.models.JenisPembayaran;
import com.example.purefreshlaundry.models.Login;
import com.example.purefreshlaundry.models.Pelanggan;
import com.example.purefreshlaundry.models.Pembayaran;
import com.example.purefreshlaundry.models.Pemesanan;
import com.example.purefreshlaundry.models.PesananSatuan;
import com.example.purefreshlaundry.models.StatusPesanan;
import com.example.purefreshlaundry.repositories.JenisPembayaranRepository;
import com.example.purefreshlaundry.repositories.LoginRepository;
import com.example.purefreshlaundry.repositories.PembayaranRepository;
import com.example.purefreshlaundry.repositories.PemesananRepository;
import com.example.purefreshlaundry.services.DriverService;
import com.example.purefreshlaundry.services.LoginService;
import com.example.purefreshlaundry.services.PelangganService;
import com.example.purefreshlaundry.services.PemesananService;
import com.example.purefreshlaundry.services.PesananSatuanService;
import com.example.purefreshlaundry.services.StatusPesananService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private PelangganService pelangganService;
    @Autowired
    private PesananSatuanService pesananSatuanService;
    @Autowired
    private PemesananService pemesananService;
    @Autowired
    private PemesananRepository pemesananRepository;
    @Autowired
    private JenisPembayaranRepository jenisPembayaranRepository;
    @Autowired
    private PembayaranRepository pembayaranRepository;
    @Autowired
    private DriverService driverService;
    @Autowired
    private StatusPesananService statusPesananService;

    @GetMapping("/")
    public String homeAwal(HttpSession session) {
        session.setAttribute("accessedFromHome", true);
        return "home-utama";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/";
    }

    ////////////////////////// LOGIN DAN DAFTAR ////////////////////////

    @GetMapping("/registrasi")
    public String registrasi(HttpSession session, Model model) {
        System.out.println("Hasilnya:"+session.getAttribute("accessedFromHome"));
        if (session.getAttribute("accessedFromHome") == null) {
        return "redirect:/"; 
        }
        model.addAttribute("regis", new Login());
        model.addAttribute("pelanggan", new Pelanggan());
        return "registrasi-pelanggan";
    }

    @PostMapping("/regis-save")
    public String saveRegistrasi(
            @ModelAttribute("regis") Login login,
            @ModelAttribute("pelanggan") Pelanggan pelanggan,
            Model model) {
        if (login.getUsername().length() < 3 || login.getUsername().length() > 20) {
            model.addAttribute("error", "Username harus memiliki panjang 3-20 karakter.");
            model.addAttribute("regis", login);
            model.addAttribute("pelanggan", pelanggan);
            return "registrasi-pelanggan";
        }
        if (!login.getUsername().matches("[a-zA-Z0-9]+")) {
            model.addAttribute("error", "Username harus diisi dengan huruf dan atau angka.");
            model.addAttribute("regis", login);
            model.addAttribute("pelanggan", pelanggan);
            return "registrasi-pelanggan";
        }
        if (login.getPassword().length() < 5 || login.getPassword().length() > 8) {
            model.addAttribute("error", "Password harus memiliki panjang 5-8 karakter.");
            model.addAttribute("regis", login);
            model.addAttribute("pelanggan", pelanggan);
            return "registrasi-pelanggan";
        }
        // Validasi duplikasi username
        if (loginService.existsByUsername(login.getUsername())) {
            model.addAttribute("error", "Username sudah digunakan. Silakan pilih username lain.");
            model.addAttribute("regis", login);
            model.addAttribute("pelanggan", pelanggan);
            return "registrasi-pelanggan";
        }
        // Validasi nama
        String nama = pelanggan.getNama();
        if (!nama.matches("^[a-zA-Z ]+$")) {
            model.addAttribute("error", "Nama hanya boleh berisi huruf dan spasi.");
            model.addAttribute("regis", login);
            model.addAttribute("pelanggan", pelanggan);
            return "registrasi-pelanggan";
        }
        try {
            Pelanggan savedPelanggan = pelangganService.savePelanggan(pelanggan);
            login.setPelanggan(savedPelanggan);
            login.setRole("Pelanggan");
            loginRepository.save(login);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("regis", login);
            model.addAttribute("pelanggan", pelanggan);
            return "registrasi-pelanggan";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("accessedFromHome") == null) {
        return "redirect:/"; 
        }
        model.addAttribute("errorMessage", null);
        return "login";
    }

    @PostMapping("/cek-login")
    public String loginPost(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {
        Login login = loginService.findByUsernameAndPassword(username, password);
        if (login != null) {
            if (login.getRole().equalsIgnoreCase("admin")) {
                session.setAttribute("loggedInUser", login);
                return "redirect:/home-admin";
            }
            Pelanggan pelanggan = login.getPelanggan();
            if (pelanggan == null) {
                model.addAttribute("errorMessage", "Data pelanggan tidak ditemukan.");
                return "login";
            }

            session.setAttribute("loggedInUser", pelanggan);
            return "redirect:/home-pelanggan";
        } else {
            model.addAttribute("errorMessage", "Username atau password salah. Silakan coba lagi.");
            return "login";
        }
    }

    //////////////////////////////////// PELANGGAN
    //////////////////////////////////// ///////////////////////////////////////

    @GetMapping("/home-pelanggan")
    public String homePengguna(HttpSession session, Model model) {
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("loggedInUser");
        if (pelanggan != null) {
            model.addAttribute("pelanggan", pelanggan);
            List<Pemesanan> riwayatPemesanan = pemesananService.getRiwayatPemesananPelanggan(pelanggan.getId());
            if (riwayatPemesanan != null && !riwayatPemesanan.isEmpty()) {
                model.addAttribute("riwayatPemesanan", riwayatPemesanan);
                System.out.println("masuk sini");
            } else {
                model.addAttribute("riwayatPemesanan", new ArrayList<Pemesanan>());
                System.out.println("masuk else");
            }
            List<JenisPembayaran> jenisPembayaranList = jenisPembayaranRepository.findAll();
            model.addAttribute("jenisPembayaranList", jenisPembayaranList);
            return "home-pelanggan";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/akun-pelanggan")
    public String akunPelanggan(HttpSession session, Model model) {
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("loggedInUser");
        if (pelanggan != null) {
            Login login = loginRepository.findByPelanggan(pelanggan);
            model.addAttribute("akun", login);
            return "akun-pelanggan";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/riwayat-pemesanan-pelanggan")
    public String riwayat(HttpSession sessions, Model model) {
        Pelanggan pelanggan = (Pelanggan) sessions.getAttribute("loggedInUser");
        if (pelanggan != null) {
            Integer idPelanggan = pelanggan.getId();
            List<Pemesanan> riwayatPesan = pemesananService.getRiwayatPemesananSelesaiPelangganById(idPelanggan);
            model.addAttribute("daftarRiwayat", riwayatPesan);
            return "riwayat-dipelanggan";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/pemesanan-pelanggan")
    public String addPesananPelanggan(HttpSession session, Model model) {
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("loggedInUser");
        if (pelanggan == null) {
            return "redirect:/login";
        }
        model.addAttribute("pelanggan", pelanggan);
        Pemesanan pesann = new Pemesanan();
        model.addAttribute("pemesanan", pesann);
        return "pemesanan-pelanggan";
    }

    @PostMapping("pelanggan-pesan")
    public String addPemesanan(@ModelAttribute Pemesanan pesan,
            @RequestParam Integer idPelanggan,
            HttpSession session,
            Model model) {
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("loggedInUser");
        if (pelanggan == null) {
            return "redirect:/login";
        }
        pesan.setPelanggan(pelanggan);
        String result = pemesananService.addPesananPelanggan(pesan, idPelanggan);
        model.addAttribute("message", result);
        return "redirect:/home-pelanggan";
    }

    @GetMapping("/pembayaran-pelanggan")
    public String tampilkanFormPembayaran(@RequestParam("id") Integer pemesananId, Model model) {
        Pemesanan pemesanan = pemesananService.getPemesananById(pemesananId);
        if (pemesanan == null) {
            throw new IllegalArgumentException("Pemesanan tidak ditemukan!");
        }
        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setTanggalPembayaran(LocalDate.now());
        pembayaran.setStatusPembayaran("Pending");
        model.addAttribute("pemesanan", pemesanan);
        List<JenisPembayaran> listPembayaran = jenisPembayaranRepository.findAll();
        model.addAttribute("jenisPembayaranList", listPembayaran);
        model.addAttribute("pembayaran", pembayaran);
        return "formPembayaran";
    }

    @PostMapping("/pembayaran-pelanggan")
    public String simpanPembayaranPelanggan(
        @RequestParam("buktiPembayarann") MultipartFile buktiPembayaran,
            @ModelAttribute Pembayaran pembayaran,
            @RequestParam("pemesananId") Integer pemesananId, Model model) {
                if (buktiPembayaran.isEmpty()) {
                    model.addAttribute("error", "Please select a file to upload.");
                    return "pembayaran-pelanggan";
                    }
            
                    try {
                        Path path = Paths.get("src/main/resources/static/uploads",
                                buktiPembayaran.getOriginalFilename());
            
                        Files.createDirectories(path.getParent());
                        Files.write(path, buktiPembayaran.getBytes());
            
                        pembayaran.setBuktiPembayaran("/uploads/"+buktiPembayaran.getOriginalFilename());
                        model.addAttribute("message", "File uploaded successfully: " + buktiPembayaran.getOriginalFilename());
                    } catch (IOException e) {
                        model.addAttribute("message", "File upload failed.");
                        e.printStackTrace();
                    }
        Pemesanan pemesanan = pemesananService.getPemesananById(pemesananId);
        if (pemesanan == null) {
            throw new IllegalArgumentException("Pemesanan tidak ditemukan!");
        }
        pembayaran.setPemesanan(pemesanan);
        if (pembayaran.getTanggalPembayaran() == null) {
            pembayaran.setTanggalPembayaran(LocalDate.now());
        }
        if (pembayaran.getStatusPembayaran() == null) {
            pembayaran.setStatusPembayaran("Pending");
        }
        pembayaran.setTanggalPembayaran(LocalDate.now());
        pembayaranRepository.save(pembayaran);
        return "redirect:/home-pelanggan";
    }

    @GetMapping("/layanan-pelanggan")
    public String layananPelanggan(HttpSession session, Model model) {
        Pelanggan pelanggan = (Pelanggan) session.getAttribute("loggedInUser");
        if (pelanggan == null) {
            return "redirect:/login";
        }
        List<PesananSatuan> pesananSatuan = pesananSatuanService.getAllPesananSatuan();
        model.addAttribute("pesananSatuan", pesananSatuan);
        return "layanan-pelanggan";
    }

    ///////////////////////////////////// ADMIN
    ///////////////////////////////////// //////////////////////////////////////

    @GetMapping("/home-admin")
    public String riwayatPemesananNonSelesai(HttpSession session, Model model) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser == null || !(loggedInUser instanceof Login)) {
            return "redirect:/login";
        }
        Login login = (Login) loggedInUser;
        if (!"admin".equalsIgnoreCase(login.getRole())) {
            return "redirect:/login";
        }
        List<Pemesanan> pemesananNonSelesai = pemesananService.getRiwayatPemesananNonSelesai();
        model.addAttribute("pemesananNonSelesai", pemesananNonSelesai);
        List<Pemesanan> pemesananNull = pemesananService.getRiwayatPemesananNull();
        model.addAttribute("pemesananNull", pemesananNull);
        List<Pembayaran> pembayaranPendingList = pembayaranRepository.findAll().stream()
                .filter(pembayaran -> "Pending".equals(pembayaran.getStatusPembayaran()))
                .collect(Collectors.toList());
        model.addAttribute("pembayaranPendingList", pembayaranPendingList);
        return "home-admin";
    }

    // Metode isLoggedInAsAdmin memastikan pengguna sudah login
    private boolean isLoggedInAsAdmin(HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        if (loggedInUser instanceof Login) {
            Login login = (Login) loggedInUser;
            return "admin".equalsIgnoreCase(login.getRole());
        }
        return false;
    }

    @GetMapping("/riwayat-pemesanan")
    public String tampilRiwayat(
            @RequestParam(name = "search", required = false) String keyword,
            @RequestParam(name = "sort", required = false) String sortCriteria,
            HttpSession session,
            Model model) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        List<Pemesanan> pesananSelesai = pemesananService.getRiwayatPemesananSelesai();
        if (keyword != null && !keyword.isEmpty()) {
            pesananSelesai = pesananSelesai.stream()
                    .filter(p -> p.getNama() != null && p.getNama().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (sortCriteria != null) {
            switch (sortCriteria) {
                case "nama":
                    pesananSelesai.sort((p1, p2) -> {
                        String nama1 = p1.getNama() != null ? p1.getNama() : "";
                        String nama2 = p2.getNama() != null ? p2.getNama() : "";
                        return nama1.compareToIgnoreCase(nama2);
                    });
                    break;
                case "jenis-pesanan":
                    pesananSelesai.sort((p1, p2) -> {
                        String jenis1 = p1.getJenisSatuan() != null ? p1.getJenisSatuan().getJenisPesanan() : "";
                        String jenis2 = p2.getJenisSatuan() != null ? p2.getJenisSatuan().getJenisPesanan() : "";
                        return jenis1.compareToIgnoreCase(jenis2);
                    });
                    break;
                default:
                    break;
            }
        }
        model.addAttribute("riwayatPemesanan", pesananSelesai);
        return "riwayat-pemesanan";
    }

    @GetMapping("/search")
    public String searchByName(@RequestParam(name = "search") String keyword, HttpSession session, Model model) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        List<Pemesanan> pemesanan = pemesananRepository.findByNamaContainingIgnoreCase(keyword);
        pemesanan = pemesanan.stream()
                .filter(p -> "Selesai".equalsIgnoreCase(p.getStatusPemesanan().getStatus()))
                .collect(Collectors.toList());
        model.addAttribute("riwayatPemesanan", pemesanan);
        return "riwayat-pemesanan";
    }

    @GetMapping("/sort-by-nama")
    public String sortByNama(HttpSession session, Model model) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        List<Pemesanan> pemesanan = pemesananRepository.findAll().stream()
                .filter(p -> "Selesai".equals(p.getStatusPemesanan().getStatus()))
                .sorted((p1, p2) -> {
                    String nama1 = p1.getNama() != null ? p1.getNama() : "";
                    String nama2 = p2.getNama() != null ? p2.getNama() : "";
                    return nama1.compareToIgnoreCase(nama2);
                })
                .collect(Collectors.toList());
        model.addAttribute("riwayatPemesanan", pemesanan);
        return "riwayat-pemesanan";
    }

    @GetMapping("/sort-by-jenis-pesanan")
    public String sortByJenisPesanan(HttpSession session, Model model) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        List<Pemesanan> pemesanan = pemesananRepository.findAll().stream()
                .filter(p -> "Selesai".equals(p.getStatusPemesanan().getStatus()))
                .sorted((p1, p2) -> p1.getJenisSatuan().getJenisPesanan()
                        .compareToIgnoreCase(p2.getJenisSatuan().getJenisPesanan()))
                .collect(Collectors.toList());
        model.addAttribute("riwayatPemesanan", pemesanan);
        return "riwayat-pemesanan";
    }

    @GetMapping("/pemesanan")
    public String addPemesanan(Model model, HttpSession session) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        Pemesanan pesanan = new Pemesanan();
        model.addAttribute("pemesanann", pesanan);
        model.addAttribute("drivers", driverService.getAllDriver());
        model.addAttribute("jenisPesananList", pesananSatuanService.getAllPesananSatuan());
        model.addAttribute("statusPesananList", statusPesananService.getStatusPesananKecualiSelesai());
        return "pemesanan-admin";
    }

    @PostMapping("/pemesanan")
    public String tambahPemesanan(
            @ModelAttribute Pemesanan pemesanan,
            @RequestParam(required = false) String passwordPemesanan,
            Model model) {
        if (passwordPemesanan != null && !passwordPemesanan.isEmpty()) {
            Pelanggan pelanggan = pelangganService.findByPasswordPemesanan(passwordPemesanan);
            if (pelanggan != null) {
                pemesanan.setPelanggan(pelanggan);
            } else {
                model.addAttribute("error", "Password tidak valid.");
                return "pemesanan-admin";
            }
        }
        Pemesanan savedPemesanan = pemesananService.tambahPemesanan(pemesanan);
        model.addAttribute("pemesanann", savedPemesanan);
        List<JenisPembayaran> jenisBayar = jenisPembayaranRepository.findAll();
        model.addAttribute("jenisPembayaranList", jenisBayar);
        return "redirect:/pembayaran-admin?pemesananId=" + savedPemesanan.getId();
    }

    @GetMapping("/edit-pemesanan/{id}")
    public String editStatusPemesanan(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        Pemesanan pemesanan = pemesananService.getPemesananById(id);
        if (pemesanan == null) {
            throw new IllegalArgumentException("Pemesanan tidak ditemukan!");
        }
        List<StatusPesanan> statusPesananList;
        if (pemesanan.getPembayaran() == null) {
            statusPesananList = statusPesananService.getStatusPesananKecualiSelesai();
        } else {
            statusPesananList = statusPesananService.getAllStatus();
        }
        List<PesananSatuan> jenisSatuanList = pesananSatuanService.getAllPesananSatuan();
        List<Driver> driverList = driverService.getAllDriver();
        model.addAttribute("pemesanann", pemesanan);
        model.addAttribute("statusPesananList", statusPesananList);
        model.addAttribute("jenisSatuanList", jenisSatuanList);
        model.addAttribute("driverList", driverList);
        return "edit-pemesanan";
    }

    @PostMapping("/edit-pemesanan/{id}")
    public String updateStatusPemesanan(
            @PathVariable("id") Integer id,
            @RequestParam("statusId") Integer statusId,
            @RequestParam(value = "jenisSatuanId", required = false) Integer jenisSatuanId,
            @RequestParam(value = "jumlahPesananSatuan", required = false) Long jumlahPesananSatuan,
            @RequestParam(value = "jumlahPesananKiloan", required = false) Long jumlahPesananKiloan,
            @RequestParam(value = "driverId", required = false) Integer driverId,
            Model model) {
        if (statusId == 4 && jenisSatuanId == null && jumlahPesananSatuan == null && jumlahPesananKiloan == null) {
            model.addAttribute("error", "Masukkan data laundry Anda.");
            System.out.println("masukk");
            model.addAttribute("pemesanann", id);
            return "redirect:/edit-pemesanan/{id}";
        }
        Pemesanan pemesanan = pemesananService.getPemesananById(id);
        StatusPesanan statusPesanan = statusPesananService.getStatusById(statusId);
        pemesanan.setStatusPemesanan(statusPesanan);
        PesananSatuan jenisSatuan = null;
        if (jenisSatuanId != null) {
            jenisSatuan = pesananSatuanService.getPesanSatuanById(jenisSatuanId);
            pemesanan.setJenisSatuan(jenisSatuan);
        }
        pemesanan.setJumlahPesananSatuan(jumlahPesananSatuan);
        pemesanan.setJumlahPesananKiloan(jumlahPesananKiloan);
        Long totalHarga = 0L;
        if (jenisSatuan != null && jumlahPesananSatuan != null) {
            totalHarga += jumlahPesananSatuan * jenisSatuan.getHarga();
        }
        if (jumlahPesananKiloan != null) {
            Long hargaKilo = pemesanan.getHargaPerkilo() != null ? pemesanan.getHargaPerkilo() : 0L;
            totalHarga += jumlahPesananKiloan * hargaKilo;
        }
        pemesanan.setTotalHarga(totalHarga);
        if (driverId != null) {
            Driver driver = driverService.findDriverById(driverId);
            pemesanan.setDriver(driver);
        }
        pemesananService.updatePemesanan(pemesanan);
        return "redirect:/home-admin";
    }

    @PostMapping("/terima-pemesanan/{id}")
    public String terimaPemesanan(@PathVariable("id") Integer id) {
        Pemesanan pemesanan = pemesananService.getPemesananById(id);
        if (pemesanan != null) {
            StatusPesanan menungguStatus = statusPesananService.getStatusByStatus("Menunggu");
            if (menungguStatus != null) {
                pemesanan.setStatusPemesanan(menungguStatus);
                pemesananService.updatePemesanan(pemesanan);
            }
        }
        return "redirect:/home-admin";
    }

    @PostMapping("/tolak-pemesanan/{id}")
    public String tolakPemesanan(@PathVariable("id") Integer id) {
        pemesananService.deletePemesananById(id);
        return "redirect:/home-admin";
    }

    @GetMapping("/pembayaran-admin")
    public String pembayaranAdmin(@RequestParam("pemesananId") Integer pemesananId, Model model, HttpSession session) {
        if (!isLoggedInAsAdmin(session)) {
        return "redirect:/login";
        }
        Pemesanan pemesanan = pemesananService.getPemesananById(pemesananId);
        model.addAttribute("pemesanan", pemesanan); 
        List<JenisPembayaran> jenisPembayaran = jenisPembayaranRepository.findAll();
        model.addAttribute("jenisPembayaranList", jenisPembayaran);
        return "pembayaran-admin";
    }

    @PostMapping("/pembayaran")
    public String simpanPembayaran(
            @RequestParam("buktiPembayaran1") MultipartFile buktiPembayaran,
            @ModelAttribute Pembayaran pembayaran,
            @RequestParam("pemesananId") Integer pemesananId,
            Model model) {

        if (buktiPembayaran.isEmpty()) {
        model.addAttribute("error", "Please select a file to upload.");
        return "pembayaran-admin";
        }
        try {
            Path path = Paths.get("src/main/resources/static/uploads",
                    buktiPembayaran.getOriginalFilename());

            Files.createDirectories(path.getParent());
            Files.write(path, buktiPembayaran.getBytes());
            pembayaran.setBuktiPembayaran("/uploads/"+buktiPembayaran.getOriginalFilename());
            model.addAttribute("message", "File uploaded successfully: " + buktiPembayaran.getOriginalFilename());
        } catch (IOException e) {
            model.addAttribute("message", "File upload failed.");
            e.printStackTrace();
        }

        Pemesanan pemesanan = pemesananService.getPemesananById(pemesananId);
        if (pemesanan == null) {
            throw new IllegalArgumentException("Pemesanan tidak ditemukan!");
        }
        boolean isPasswordPemesananDiisi = pemesanan.getPelanggan() != null &&
                pemesanan.getPelanggan().getPasswordPemesanan() != null;

        if (!isPasswordPemesananDiisi) {
            if (pembayaran.getJenisPembayaran() == null || pembayaran.getBuktiPembayaran() == null) {
                model.addAttribute("pemesanan", pemesanan);
                List<JenisPembayaran> jenisBayar = jenisPembayaranRepository.findAll();
                model.addAttribute("jenisPembayaranList", jenisBayar);
                model.addAttribute("error", "Semua data pembayaran wajib diisi.");
                return "pembayaran-admin";
            }
        }
        pembayaran.setPemesanan(pemesanan);
        pembayaran.setTanggalPembayaran(LocalDate.now());
        pembayaranRepository.save(pembayaran);
        return "redirect:/home-admin";
    }

    @GetMapping("/konfirmasiPembayaran/{id}")
    public String konfirmasiPembayaran(@PathVariable Integer id, HttpSession session) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        Pembayaran pembayaran = pembayaranRepository.findById(id).orElse(null);
        if (pembayaran != null && "Pending".equals(pembayaran.getStatusPembayaran())) {
            pembayaran.setStatusPembayaran("Completed");
            pembayaran.setTanggalPembayaran(LocalDate.now());
            pembayaranRepository.save(pembayaran);
        }
        return "redirect:/home-admin";
    }

    @GetMapping("/pelanggan/tambah")
    public String tampilTambahPelanggan(Model model, HttpSession session) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        return "tambah-pelanggan";
    }

    @PostMapping("/pelanggan/tambah")
    public String tambahPelanggan(@RequestParam String nama, @RequestParam String email, @RequestParam String nomorTelp,
            @RequestParam String alamat, @RequestParam String username, @RequestParam String password,
            @RequestParam String passwordPemesanan,
            Model model) {
        if (username.length() < 3 || username.length() > 20) {
            model.addAttribute("error", "Username harus memiliki panjang 3-20 karakter.");
            return "tambah-pelanggan";
        }
        if (!username.matches("[a-zA-Z0-9]+")) {
            model.addAttribute("error", "Username hanya boleh berisi huruf dan angka.");
            return "tambah-pelanggan";
        }
        if (loginService.existsByUsername(username)) {
            model.addAttribute("error", "Username sudah digunakan. Silakan pilih username lain.");
            return "tambah-pelanggan";
        }
        if (password.length() < 5 || password.length() > 8) {
            model.addAttribute("error", "Password Login harus memiliki panjang 5-8 karakter.");
            return "tambah-pelanggan";
        }
        if (!password.matches("[a-zA-Z0-9]+")) {
            model.addAttribute("error", "Password Login hanya boleh berisi huruf dan angka.");
            return "tambah-pelanggan";
        }
        if (passwordPemesanan == null || passwordPemesanan.length() < 5 || passwordPemesanan.length() > 8) {
            model.addAttribute("error", "Password Pemesanan harus memiliki panjang 5-8 karakter.");
            return "tambah-pelanggan";
        }
        if (!passwordPemesanan.matches("[a-zA-Z0-9]+")) {
            model.addAttribute("error", "Password Pemesanan hanya boleh berisi huruf dan angka.");
            return "tambah-pelanggan";
        }
        if (alamat == null || alamat.trim().isEmpty()) {
            model.addAttribute("error", "Alamat tidak boleh kosong.");
            return "tambah-pelanggan";
        }
        if (alamat.length() < 10 || alamat.length() > 255) {
            model.addAttribute("error", "Alamat harus memiliki panjang 10-255 karakter.");
            return "tambah-pelanggan";
        }
        if (nomorTelp == null || !nomorTelp.matches("[0-9]+") || nomorTelp.length() < 10 || nomorTelp.length() > 15) {
            model.addAttribute("error", "Nomor Telepon harus berupa angka dengan panjang 10-15 karakter.");
            return "tambah-pelanggan";
        }
        Pelanggan pelanggan = new Pelanggan();
        pelanggan.setNama(nama);
        pelanggan.setEmail(email);
        pelanggan.setNomorTelp(nomorTelp);
        pelanggan.setAlamat(alamat);
        pelanggan.setPasswordPemesanan(passwordPemesanan);
        pelanggan.setTanggalDaftar(java.time.LocalDate.now());
        pelanggan = pelangganService.savePelanggan(pelanggan);
        Login login = new Login();
        login.setUsername(username);
        login.setPassword(password);
        login.setPelanggan(pelanggan);
        loginRepository.save(login);
        return "redirect:/pelanggan/tampil";
    }

    @GetMapping("/pelanggan/tampil")
    public String tampilPelanggan(Model model, HttpSession session) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("pelangganList", pelangganService.getAllPelanggan());
        return "tampil-pelanggan";
    }

    @GetMapping("/pelanggan/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, HttpSession session) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        Pelanggan pelanggan = pelangganService.findPelangganById(id);
        if (pelanggan == null) {
            return "redirect:/pelanggan/tampil";
        }
        Login login = loginService.findByPelanggan(pelanggan);
        model.addAttribute("pelanggan", pelanggan);
        model.addAttribute("login", login);
        return "update-pelanggan";
    }

    @PostMapping("/pelanggan/update/{id}")
    public String updatePelanggan(@PathVariable("id") Integer id,
            @ModelAttribute("pelanggan") Pelanggan pelanggan,
            @ModelAttribute("login") Login login,
            Model model) {
        if (login.getUsername().length() < 3 || login.getUsername().length() > 20) {
            model.addAttribute("error", "Username harus memiliki panjang 3-20 karakter.");
            return "update-pelanggan";
        }
        if (!login.getUsername().matches("[a-zA-Z0-9]+")) {
            model.addAttribute("error", "Username harus diisi dengan huruf dan atau angka.");
            return "update-pelanggan";
        }
        if (login.getPassword().length() < 5 || login.getPassword().length() > 8) {
            model.addAttribute("error", "Password Login harus memiliki panjang 5-8 karakter.");
            return "update-pelanggan";
        }
        if (pelanggan.getNomorTelp() == null || !pelanggan.getNomorTelp().matches("[0-9]+")
                || pelanggan.getNomorTelp().length() < 10 || pelanggan.getNomorTelp().length() > 15) {
            model.addAttribute("error", "Nomor Telepon harus berupa angka dengan panjang 10-15 karakter.");
            return "tambah-pelanggan";
        }
        if (!login.getPassword().matches("[a-zA-Z0-9]+")) {
            model.addAttribute("error", "Password Login hanya boleh berisi huruf dan angka.");
            return "update-pelanggan";
        }
        String passwordPemesanan = pelanggan.getPasswordPemesanan();
        if (passwordPemesanan == null || passwordPemesanan.length() < 5 || passwordPemesanan.length() > 8) {
            model.addAttribute("error", "Password Pemesanan harus memiliki panjang 5-8 karakter.");
            return "update-pelanggan";
        }
        if (!passwordPemesanan.matches("[a-zA-Z0-9]+")) {
            model.addAttribute("error", "Password Pemesanan hanya boleh berisi huruf dan angka.");
            return "update-pelanggan";
        }
        Pelanggan existingPelanggan = pelangganService.findPelangganById(id);
        if (existingPelanggan != null) {
            existingPelanggan.setNama(pelanggan.getNama());
            existingPelanggan.setEmail(pelanggan.getEmail());
            existingPelanggan.setNomorTelp(pelanggan.getNomorTelp());
            existingPelanggan.setAlamat(pelanggan.getAlamat());
            existingPelanggan.setPasswordPemesanan(pelanggan.getPasswordPemesanan());
            pelangganService.savePelanggan(existingPelanggan);
            Login existingLogin = loginService.findByPelanggan(existingPelanggan);
            if (existingLogin != null) {
                existingLogin.setUsername(login.getUsername());
                existingLogin.setPassword(login.getPassword());
                loginRepository.save(existingLogin);
            }
        }
        return "redirect:/pelanggan/tampil";
    }

    @GetMapping("/pelanggan/delete/{id}")
    public String deletePelanggan(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isLoggedInAsAdmin(session)) {
            return "redirect:/login";
        }
        Pelanggan pelanggan = pelangganService.findPelangganById(id);
        if (pelanggan != null) {
            pelangganService.setPelangganToNull(id);
            Login login = loginService.findByPelanggan(pelanggan);
            if (login != null) {
                loginRepository.deleteById(login.getId());
            }
            pelangganService.deletePelanggan(id);
            return "redirect:/pelanggan/tampil";
        } else {
            model.addAttribute("errorMessage", "Pelanggan tidak ditemukan!");
            return "redirect:/pelanggan/tampil";
        }
    }

}
