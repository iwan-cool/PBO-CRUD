import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

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

class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/anfield mart";
    private static final String USER = "root"; // Default user for MySQL
    private static final String PASSWORD = ""; // MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
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

    // CRUD Operations
    public void addBarang(String kodeBarang, String namaBarang, double hargaBarang) {
        String query = "INSERT INTO barang (kodeBarang, namaBarang, hargaBarang) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kodeBarang);
            stmt.setString(2, namaBarang);
            stmt.setDouble(3, hargaBarang);
            stmt.executeUpdate();
            System.out.println("Barang added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayBarang() {
        String query = "SELECT * FROM barang";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Kode Barang: " + rs.getString("kodeBarang"));
                System.out.println("Nama Barang: " + rs.getString("namaBarang"));
                System.out.println("Harga Barang: " + rs.getDouble("hargaBarang"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBarang(String kodeBarang, String newNamaBarang, double newHargaBarang) {
        String query = "UPDATE barang SET namaBarang = ?, hargaBarang = ? WHERE kodeBarang = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newNamaBarang);
            stmt.setDouble(2, newHargaBarang);
            stmt.setString(3, kodeBarang);
            stmt.executeUpdate();
            System.out.println("Barang updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBarang(String kodeBarang) {
        String query = "DELETE FROM barang WHERE kodeBarang = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, kodeBarang);
            stmt.executeUpdate();
            System.out.println("Barang deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleCRUD() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n+----------------------------------------------------+");
            System.out.println("Choose an operation:");
            System.out.println("1. Add Barang (Create)");
            System.out.println("2. View Barang (Read)");
            System.out.println("3. Update Barang");
            System.out.println("4. Delete Barang");
            System.out.println("5. Exit");
            System.out.print("Enter choice (1-5): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline left-over

            switch (choice) {
                case 1: // Add Barang (Create)
                    System.out.print("Enter Kode Barang: ");
                    String kodeBarang = scanner.nextLine();
                    System.out.print("Enter Nama Barang: ");
                    String namaBarang = scanner.nextLine();
                    System.out.print("Enter Harga Barang: ");
                    double hargaBarang = scanner.nextDouble();
                    addBarang(kodeBarang, namaBarang, hargaBarang);
                    break;
                case 2: // View Barang (Read)
                    displayBarang();
                    break;
                case 3: // Update Barang
                    System.out.print("Enter Kode Barang to Update: ");
                    String kodeToUpdate = scanner.nextLine();
                    System.out.print("Enter New Nama Barang: ");
                    String newNamaBarang = scanner.nextLine();
                    System.out.print("Enter New Harga Barang: ");
                    double newHargaBarang = scanner.nextDouble();
                    updateBarang(kodeToUpdate, newNamaBarang, newHargaBarang);
                    break;
                case 4: // Delete Barang
                    System.out.print("Enter Kode Barang to Delete: ");
                    String kodeToDelete = scanner.nextLine();
                    deleteBarang(kodeToDelete);
                    break;
                case 5: // Exit
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

public class MainProgram {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Login Section
            System.out.println("+-----------------------------------------------------+");
            System.out.println("                     LOGIN                            ");
            System.out.println("+-----------------------------------------------------+");

            String correctUsername = "ikhwan"; // Username yang valid
            String correctPassword = "12345"; // Password yang valid

            while (true) {
                System.out.print("Username : ");
                String username = scanner.nextLine(); // Input username dari pengguna
                if (username.isEmpty()) {
                    throw new InvalidInputException("Username tidak boleh kosong!");
                }

                System.out.print("Password : ");
                String password = scanner.nextLine(); // Input password dari pengguna
                if (password.isEmpty()) {
                    throw new InvalidInputException("Password tidak boleh kosong!");
                }

                // Captcha yang ditentukan
                String captcha = "LIV123"; 
                System.out.println("Captcha  : " + captcha);
                System.out.print("Masukkan Captcha : ");
                String captchaInput = scanner.nextLine(); // Input captcha dari pengguna

                // Validasi login
                if (username.equals(correctUsername) && password.equals(correctPassword) && captchaInput.equals(captcha)) {
                    System.out.println("Login berhasil!\n");
                    break;
                } else {
                    System.out.println("Login gagal, silakan coba lagi.\n");
                }
            }

            // Welcome Message with Date and Time
            System.out.println("Selamat Datang di AnfieldMart");

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss"); 
            System.out.println("Tanggal dan Waktu : " + sdf.format(new Date())); 
            System.out.println("+----------------------------------------------------+");

            // Input Details
            System.out.print("Masukkan Nama Kasir: ");
            String kasir = scanner.nextLine();
            if (kasir.isEmpty()) {
                throw new InvalidInputException("Nama Kasir tidak boleh kosong!");
            }

            System.out.print("Masukkan No Faktur: ");
            String noFaktur = scanner.nextLine();
            if (noFaktur.isEmpty()) {
                throw new InvalidInputException("No Faktur tidak boleh kosong!");
            }

            // Membuat objek transaksi dan menampilkan hasil
            Transaksi transaksi = new Transaksi(noFaktur, "", "", 0, kasir);
            transaksi.handleCRUD();  // Call to CRUD handling method

        } catch (InvalidInputException e) {
            System.out.println("Input Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        } finally {
            scanner.close(); // Close scanner
        }
    }
}
