import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

class Barang {
    protected String kodeBarang;
    protected String namaBarang;
    protected double hargaBarang;

    public Barang(String kodeBarang, String namaBarang, double hargaBarang) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.hargaBarang = hargaBarang;
    }

    public double hitungTotal(int jumlahBeli) {
        return jumlahBeli * hargaBarang;
    }
}

class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

class DatabaseHelper {
    private Connection connection;

    public DatabaseHelper(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public void insertBarang(String kodeBarang, String namaBarang, double hargaBarang) throws SQLException {
        String query = "INSERT INTO barang (kode_barang, nama_barang, harga_barang) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, kodeBarang);
            stmt.setString(2, namaBarang);
            stmt.setDouble(3, hargaBarang);
            stmt.executeUpdate();
        }
    }

    public void readBarang() throws SQLException {
        String query = "SELECT * FROM barang";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("\n+--------------------+--------------------+--------------------+");
            System.out.println("| Kode Barang       | Nama Barang        | Harga Barang       |");
            System.out.println("+--------------------+--------------------+--------------------+");
            while (rs.next()) {
                System.out.printf("| %-18s | %-18s | %-18.2f |\n",
                        rs.getString("kode_barang"),
                        rs.getString("nama_barang"),
                        rs.getDouble("harga_barang"));
            }
            System.out.println("+--------------------+--------------------+--------------------+");
        }
    }

    public void updateBarang(String kodeBarang, String namaBarang, double hargaBarang) throws SQLException {
        String query = "UPDATE barang SET nama_barang = ?, harga_barang = ? WHERE kode_barang = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, namaBarang);
            stmt.setDouble(2, hargaBarang);
            stmt.setString(3, kodeBarang);
            stmt.executeUpdate();
        }
    }

    public void deleteBarang(String kodeBarang) throws SQLException {
        String query = "DELETE FROM barang WHERE kode_barang = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, kodeBarang);
            stmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

class Transaksi extends Barang {
    private String noFaktur;
    private String kasir;

    public Transaksi(String noFaktur, String kodeBarang, String namaBarang, double hargaBarang, String kasir) {
        super(kodeBarang, namaBarang, hargaBarang);
        this.noFaktur = noFaktur;
        this.kasir = kasir;
    }

    public void tampilkanTransaksi(int jumlahBeli) {
        double total = hitungTotal(jumlahBeli);
        System.out.println("\n+----------------------------------------------------+");
        System.out.println("No. Faktur      : " + noFaktur);
        System.out.println("Kode Barang     : " + kodeBarang);
        System.out.println("Nama Barang     : " + namaBarang);
        System.out.println("Harga Barang    : Rp " + hargaBarang);
        System.out.println("Jumlah Beli     : " + jumlahBeli);
        System.out.println("TOTAL           : Rp " + total);
        System.out.println("+----------------------------------------------------+");
        System.out.println("Kasir           : " + kasir);
        System.out.println("+----------------------------------------------------+");
    }
}

public class MainProgram {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String url = "jdbc:mysql://localhost:3306/anfieldmart";
        String user = "root";
        String password = ""; // Sesuaikan dengan password MySQL Anda

        LogTransaksi logTransaksi = new LogTransaksi();

        try (DatabaseHelper db = new DatabaseHelper(url, user, password)) {
            // Login dan operasi lainnya seperti pada kode sebelumnya

            while (true) {
                System.out.println("\nPilih Operasi:");
                System.out.println("1. Tambah Barang");
                System.out.println("2. Lihat Barang");
                System.out.println("3. Ubah Barang");
                System.out.println("4. Hapus Barang");
                System.out.println("5. Buat Transaksi");
                System.out.println("6. Simpan Log Transaksi");
                System.out.println("7. Keluar");
                System.out.print("Pilihan: ");
                int pilihan = scanner.nextInt();
                scanner.nextLine();

                switch (pilihan) {
                    case 1:
                        System.out.print("Masukkan Kode Barang: ");
                        String kodeBarang = scanner.nextLine();
                        System.out.print("Masukkan Nama Barang: ");
                        String namaBarang = scanner.nextLine();
                        System.out.print("Masukkan Harga Barang: ");
                        double hargaBarang = scanner.nextDouble();
                        db.insertBarang(kodeBarang, namaBarang, hargaBarang);
                        logTransaksi.tambahLog("Barang ditambahkan: " + kodeBarang + " - " + namaBarang + " - Rp " + hargaBarang);
                        System.out.println("Barang berhasil ditambahkan.");
                        break;
                        case 2:
                        db.readBarang();
                        break;
                    case 3:
                        System.out.print("Masukkan Kode Barang yang akan diubah: ");
                        String kodeUpdate = scanner.nextLine();
                        System.out.print("Masukkan Nama Barang baru: ");
                        String namaUpdate = scanner.nextLine();
                        System.out.print("Masukkan Harga Barang baru: ");
                        double hargaUpdate = scanner.nextDouble();
                        db.updateBarang(kodeUpdate, namaUpdate, hargaUpdate);
                        logTransaksi.tambahLog("Barang diubah: " + kodeUpdate + " - " + namaUpdate + " - Rp " + hargaUpdate);
                        System.out.println("Barang berhasil diubah.");
                        break;
                    case 4:
                        System.out.print("Masukkan Kode Barang yang akan dihapus: ");
                        String kodeDelete = scanner.nextLine();
                        db.deleteBarang(kodeDelete);
                        logTransaksi.tambahLog("Barang dihapus: " + kodeDelete);
                        System.out.println("Barang berhasil dihapus.");
                        break;
                    case 5:
                        System.out.print("Masukkan No. Faktur: ");
                        String noFaktur = scanner.nextLine();
                        System.out.print("Masukkan Kode Barang: ");
                        String kodeTransaksi = scanner.nextLine();
                        System.out.print("Masukkan Nama Barang: ");
                        String namaTransaksi = scanner.nextLine();
                        System.out.print("Masukkan Harga Barang: ");
                        double hargaTransaksi = scanner.nextDouble();
                        System.out.print("Masukkan Jumlah Beli: ");
                        int jumlahBeli = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Masukkan Nama Kasir: ");
                        String kasir = scanner.nextLine();

                        Transaksi transaksi = new Transaksi(noFaktur, kodeTransaksi, namaTransaksi, hargaTransaksi, kasir);
                        transaksi.tampilkanTransaksi(jumlahBeli);
                        logTransaksi.tambahLog("Transaksi dibuat: Faktur: " + noFaktur + ", Barang: " + namaTransaksi + ", Jumlah: " + jumlahBeli + ", Total: Rp " + transaksi.hitungTotal(jumlahBeli));
                        break;
                        case 6:
                        System.out.print("Masukkan nama file untuk menyimpan log: ");
                        String fileName = scanner.nextLine();
                        logTransaksi.simpanLog(fileName);
                        break;
                    case 7:
                        System.out.println("Keluar dari program.");
                        return;
                    default:
                        System.out.println("Pilihan tidak valid.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}

class LogTransaksi {
    private List<String> logs = new ArrayList<>();

    public void tambahLog(String log) {
        logs.add(log);
    }

    public void simpanLog(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (String log : logs) {
                writer.write(log + "\n");
            }
            System.out.println("Log transaksi berhasil disimpan ke " + fileName);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan log transaksi: " + e.getMessage());
        }
    }
}
