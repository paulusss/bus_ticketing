import java.sql.*;
import java.util.Scanner;
public class BookingBus {
	 // Menyiapkan paramter JDBC untuk koneksi ke datbase
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/bus";
    static final String USER = "root";
    static final String PASS = "";

    // Menyiapkan objek yang diperlukan untuk mengelola database
    static Connection conn;
    static Statement stmt;
    static ResultSet rs;
    
    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        
        // Melakukan koneksi ke database
        // harus dibungkus dalam blok try/catch
        try {
            // register driver yang akan dipakai
            Class.forName(JDBC_DRIVER);
            
            // buat koneksi ke database
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // buat objek statement
            stmt = conn.createStatement();
            
            welcome();

            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public static void welcome() {
        System.out.println("Selamat datang di pemesanan bus online");
        System.out.println("Silakan pilih menu;");
        System.out.println("1. Login");
        System.out.println("2. Daftar");
        
        int pilihan = in.nextInt();
        
        if (pilihan==1) {
        	login();
        }else if(pilihan==2) {
        	daftar();
        }else{
        	System.out.println("Pilihan anda tidak ada");
        	welcome();
        }
    }
    
    public static void daftar() {
    	System.out.println("Masukkan NIK");
    	int NIK = in.nextInt();
    	System.out.println("Masukkan Nama");
    	String nama = in.nextLine();
    	nama = in.nextLine();
    	System.out.println("Masukkan No. Tilpun");
    	String tilpun = in.nextLine();
    	String sql = "INSERT INTO `penumpang`(`nik`, `nama`, `no_telfon`) VALUES ('"+NIK+"','"+nama+"','"+tilpun+"')";
    	try {
			stmt.execute(sql);
			System.out.println("Data berhasil disimpan");
			welcome();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void login() {
    	System.out.println("Masukkan NIK");
    	int NIK = in.nextInt();
    	String sql = "SELECT count(*) FROM penumpang where nik="+NIK;
    	try {
			rs = stmt.executeQuery(sql);
			while(rs.next()){
                int kecocokan = rs.getInt("count(*)");
                if (kecocokan>0) {
                	mainMenu(NIK);
                }else {
                	System.out.println("Anda belum daftar");
                	welcome();
                }
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void mainMenu(int NIK) {
    	String sql = "SELECT nama FROM penumpang WHERE NIK = "+NIK;
    	String nama = "";
    	try {
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				nama = rs.getString("nama");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("Selamat datang "+nama);
        System.out.println("Silakan pilih menu;");
        System.out.println("1. Booking");
        System.out.println("2. Batalkan booking");
        System.out.println("3. Logout");
        
        int pilihan = in.nextInt();
        
        if (pilihan==1) {
        	booking(NIK);
        }else if(pilihan==2) {
        	cancel(NIK);
        }else if(pilihan==3) {
        	welcome();
        }else{
        	System.out.println("Pilihan anda tidak ada");
        	mainMenu(NIK);
        }
    }
    
    public static void booking(int NIK) {
    	System.out.println("Masukkan terminal keberangkatan");
    	String berangkat = in.nextLine();
    	berangkat = in.nextLine();
    	System.out.println("Masukkan tanggal keberangkatan (YYYY-MM-DD)");
    	String tanggal = in.nextLine();
    	
    	String sql = "SELECT keberangkatan.id_keberangkatan AS ID, jurusan.deskripsi AS deskripsi, keberangkatan.tanggal AS waktu, "
    			+ "bus.nama_perusahaan AS PO, keberangkatan.kelas AS kelas, keberangkatan.harga AS harga FROM keberangkatan "
    			+ "INNER JOIN jurusan ON keberangkatan.id_jurusan = jurusan.id_jurusan INNER JOIN bus ON keberangkatan.no_polisi = bus.no_polisi "
    			+ "WHERE jurusan.terminal_awal = '"+berangkat+"'AND keberangkatan.tanggal LIKE '"+tanggal+"%'"
    					+ "AND (SELECT COUNT(*) FROM booking WHERE id_keberangkatan_bus = keberangkatan.id_keberangkatan) < "
    					+ "(SELECT maxofseat FROM bus WHERE no_polisi = keberangkatan.no_polisi)";
    	
    	try {
			rs = stmt.executeQuery(sql);
			
            while(rs.next()){
                System.out.println("ID keberangkatan: " + rs.getString("ID"));
                System.out.println("Jurusan: " + rs.getString("deskripsi"));
                System.out.println("Waktu: " + rs.getString("waktu"));
                System.out.println("PO: " + rs.getString("PO"));
                System.out.println("Kelas: " + rs.getString("kelas"));
                System.out.println("Harga: Rp. " + rs.getString("harga"));
                System.out.println();
            }
            
            System.out.println("Silakan pilih menu;");
            System.out.println("1. Booking");
            System.out.println("2. Kembali");

            int pilihan = in.nextInt();
            String sampah = in.nextLine();
            
            if (pilihan==1) {
            	bookingBeneran (NIK);
            }else if(pilihan==2) {
            	mainMenu(NIK);
            }else{
            	System.out.println("Pilihan anda tidak ada");
            	mainMenu(NIK);
            }
            
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void bookingBeneran(int NIK) {
    	System.out.println("Masukkan ID keberangkatan yang diinginkan");
    	int keberangkatan = in.nextInt();
    	String sampah = in.nextLine();
    	String sqlHarga = "SELECT harga FROM keberangkatan WHERE id_keberangkatan = "+keberangkatan;
    	try {
			rs = stmt.executeQuery(sqlHarga);
	        while(rs.next()){
	            System.out.println("Anda akan membayar: " + rs.getString("harga"));
	        }
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	

    	
    	System.out.println("Masukkan nomor kartu kridit / debit");
    	int kartu = in.nextInt();
    	sampah = in.nextLine();
    	int nomorKursi = (int) (Math.random()*10)+1;
    	String sql = "INSERT INTO `booking`(`nik_penumpang`, `seat_id_bus`, `id_keberangkatan_bus`) "
    			+ "VALUES ('"+NIK+"','"+nomorKursi+"','"+keberangkatan+"')";
    	try {
			stmt.execute(sql);
			System.out.println("Data berhasil disimpan");
			
	    	sql = "SELECT id_booking FROM booking WHERE `nik_penumpang` = "+NIK+" AND `seat_id_bus` = "+ nomorKursi+" AND `id_keberangkatan_bus` ="+ keberangkatan;
	    	rs = stmt.executeQuery(sql);
	    	
	    	while(rs.next()){
                System.out.println("ID booking anda: " + rs.getString("id_booking"));
            }
	    	System.out.println("Nomor Kursi "+ nomorKursi);
	    	System.out.println();
			mainMenu(NIK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void cancel(int NIK) {
    	System.out.println("Masukkan ID booking yang akan dibatalkan");
    	int IDBooking = in.nextInt();
    	String sampah = in.nextLine();
    	String sql = "DELETE FROM booking WHERE id_booking = "+IDBooking;
    	try {
			stmt.execute(sql);
			System.out.println("Booking berhasil dibatalkan\n");
			mainMenu(NIK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
