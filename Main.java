import java.util.*;
import java.io.*;

/* ================= VEHICLE CLASS ================= */
class Vehicle implements Serializable {
    String id, brand, model, type, fuel;
    double pricePerDay;
    boolean available = true;
    int totalRating = 0;
    int ratingCount = 0;

    public Vehicle(String id, String brand, String model,
                   String type, String fuel, double pricePerDay) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.fuel = fuel;
        this.pricePerDay = pricePerDay;
    }

    double averageRating() {
        return ratingCount == 0 ? 0 : (double) totalRating / ratingCount;
    }
}

/* ================= RENTAL CLASS ================= */
class Rental implements Serializable {
    Vehicle vehicle;
    String customerName;
    int days;

    public Rental(Vehicle vehicle, String customerName, int days) {
        this.vehicle = vehicle;
        this.customerName = customerName;
        this.days = days;
    }
}

/* ================= SYSTEM CLASS ================= */
class VehicleRentalSystem {

    List<Vehicle> vehicles = new ArrayList<>();
    List<Rental> rentals = new ArrayList<>();
    Scanner sc = new Scanner(System.in);

    final double HELMET_FEE = 50;
    final double ELECTRIC_CHARGE = 100;
    final double LATE_FINE_PER_DAY = 200;

    /* ---------- FILE HANDLING ---------- */
    void saveData() {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream("data.dat"))) {
            oos.writeObject(vehicles);
            oos.writeObject(rentals);
        } catch (Exception ignored) {}
    }

    void loadData() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream("data.dat"))) {
            vehicles = (List<Vehicle>) ois.readObject();
            rentals = (List<Rental>) ois.readObject();
        } catch (Exception ignored) {}
    }

    void addVehicle(Vehicle v) {
        vehicles.add(v);
    }

    /* ---------- DISPLAY ---------- */
    void showAvailable() {
        System.out.println("\n--- AVAILABLE VEHICLES ---");
        boolean found = false;
        for (Vehicle v : vehicles) {
            if (v.available) {
                found = true;
                System.out.printf("%s | %s | %s %s | %s | ⭐ %.1f%n",
                        v.id, v.type, v.brand, v.model, v.fuel, v.averageRating());
            }
        }
        if (!found) System.out.println("No vehicles available.");
    }

    void showRented() {
        System.out.println("\n--- RENTED VEHICLES ---");
        if (rentals.isEmpty()) {
            System.out.println("No rented vehicles.");
            return;
        }
        for (Rental r : rentals) {
            System.out.println(r.vehicle.id + " | " + r.vehicle.brand +
                    " | Rented by: " + r.customerName);
        }
    }

    /* ---------- RENT ---------- */
    void rentVehicle() {
        System.out.print("Customer Name: ");
        String name = sc.nextLine();

        showAvailable();
        System.out.print("Enter Vehicle ID: ");
        String id = sc.nextLine();

        Vehicle selected = null;
        for (Vehicle v : vehicles) {
            if (v.id.equalsIgnoreCase(id) && v.available) {
                selected = v;
                break;
            }
        }

        if (selected == null) {
            System.out.println("Invalid selection.");
            return;
        }

        System.out.print("Rental Days: ");
        int days = sc.nextInt();
        sc.nextLine();

        double base = selected.pricePerDay * days;
        double helmet = selected.type.equals("Car") ? 0 : HELMET_FEE * days;
        double charge = selected.fuel.equals("Electric") ? ELECTRIC_CHARGE * days : 0;
        double total = base + helmet + charge;

        System.out.println("TOTAL AMOUNT: ₹" + total);
        System.out.print("Confirm (Y/N): ");

        if (sc.nextLine().equalsIgnoreCase("Y")) {
            selected.available = false;
            rentals.add(new Rental(selected, name, days));
            saveData();
            System.out.println("Vehicle Rented Successfully ✅");
        }
    }

    /* ---------- RETURN ---------- */
    void returnVehicle() {
        System.out.print("Enter Vehicle ID: ");
        String id = sc.nextLine();

        Rental found = null;
        for (Rental r : rentals) {
            if (r.vehicle.id.equalsIgnoreCase(id)) {
                found = r;
                break;
            }
        }

        if (found == null) {
            System.out.println("Vehicle not rented.");
            return;
        }

        System.out.print("Actual days used: ");
        int actualDays = sc.nextInt();
        sc.nextLine();

        int lateDays = Math.max(0, actualDays - found.days);
        double fine = lateDays * LATE_FINE_PER_DAY;

        if (fine > 0) {
            System.out.println("Late Fine: ₹" + fine);
        }

        found.vehicle.available = true;
        rentals.remove(found);
        saveData();
        System.out.println("Vehicle Returned Successfully 🔁");
    }

    /* ---------- RATING ---------- */
    void rateVehicle() {
        System.out.print("Enter Vehicle ID to rate: ");
        String id = sc.nextLine();

        for (Vehicle v : vehicles) {
            if (v.id.equalsIgnoreCase(id)) {
                System.out.print("Rate (1–5): ");
                int rating = sc.nextInt();
                sc.nextLine();

                if (rating < 1 || rating > 5) {
                    System.out.println("Invalid rating.");
                    return;
                }

                v.totalRating += rating;
                v.ratingCount++;
                saveData();
                System.out.println("Thank you for rating ⭐");
                return;
            }
        }
        System.out.println("Vehicle not found.");
    }

    /* ---------- MENU ---------- */
    void menu() {
        while (true) {
            System.out.println("\n====== VEHICLE RENTAL SYSTEM ======");
            System.out.println("1. Show Available Vehicles");
            System.out.println("2. Show Rented Vehicles");
            System.out.println("3. Rent Vehicle");
            System.out.println("4. Return Vehicle");
            System.out.println("5. Rate a Vehicle");
            System.out.println("6. Exit");
            System.out.print("Choice: ");

            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1 -> showAvailable();
                case 2 -> showRented();
                case 3 -> rentVehicle();
                case 4 -> returnVehicle();
                case 5 -> rateVehicle();
                case 6 -> {
                    saveData();
                    System.out.println("Thank you! 👋");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }
}

/* ================= MAIN CLASS ================= */
public class Main {
    public static void main(String[] args) {

        VehicleRentalSystem system = new VehicleRentalSystem();
        system.loadData();

        /* ===== AUTO UPDATE / DATA MIGRATION ===== */
        if (system.vehicles.size() < 30) {

            system.vehicles.clear();
            system.rentals.clear();

            // -------- CARS (10) --------
            system.addVehicle(new Vehicle("C001","Toyota","Camry","Car","Petrol",60));
            system.addVehicle(new Vehicle("C002","Honda","City","Car","Petrol",65));
            system.addVehicle(new Vehicle("C003","Hyundai","Verna","Car","Petrol",62));
            system.addVehicle(new Vehicle("C004","Maruti","Swift","Car","Petrol",55));
            system.addVehicle(new Vehicle("C005","Tata","Nexon","Car","Petrol",70));
            system.addVehicle(new Vehicle("C006","Mahindra","XUV300","Car","Petrol",75));
            system.addVehicle(new Vehicle("C007","Kia","Seltos","Car","Petrol",80));
            system.addVehicle(new Vehicle("C008","Skoda","Slavia","Car","Petrol",78));
            system.addVehicle(new Vehicle("C009","MG","Hector","Car","Petrol",85));
            system.addVehicle(new Vehicle("C010","Toyota","Innova","Car","Petrol",90));

            // -------- BIKES (10) --------
            system.addVehicle(new Vehicle("B001","Yamaha","FZ","Bike","Petrol",40));
            system.addVehicle(new Vehicle("B002","Honda","CBR","Bike","Petrol",45));
            system.addVehicle(new Vehicle("B003","Bajaj","Pulsar","Bike","Petrol",42));
            system.addVehicle(new Vehicle("B004","TVS","Apache","Bike","Petrol",44));
            system.addVehicle(new Vehicle("B005","Royal Enfield","Classic 350","Bike","Petrol",55));
            system.addVehicle(new Vehicle("B006","KTM","Duke 200","Bike","Petrol",60));
            system.addVehicle(new Vehicle("B007","Ather","450X","Bike","Electric",50));
            system.addVehicle(new Vehicle("B008","Ola","Roadster","Bike","Electric",48));
            system.addVehicle(new Vehicle("B009","Revolt","RV400","Bike","Electric",46));
            system.addVehicle(new Vehicle("B010","Ultraviolette","F77","Bike","Electric",65));

            // -------- SCOOTERS (10) --------
            system.addVehicle(new Vehicle("S001","Honda","Activa","Scooter","Petrol",35));
            system.addVehicle(new Vehicle("S002","TVS","Jupiter","Scooter","Petrol",34));
            system.addVehicle(new Vehicle("S003","Suzuki","Access","Scooter","Petrol",36));
            system.addVehicle(new Vehicle("S004","Yamaha","Fascino","Scooter","Petrol",33));
            system.addVehicle(new Vehicle("S005","Hero","Pleasure","Scooter","Petrol",32));
            system.addVehicle(new Vehicle("S006","Ola","S1","Scooter","Electric",30));
            system.addVehicle(new Vehicle("S007","Ather","450","Scooter","Electric",32));
            system.addVehicle(new Vehicle("S008","TVS","iQube","Scooter","Electric",31));
            system.addVehicle(new Vehicle("S009","Bajaj","Chetak","Scooter","Electric",34));
            system.addVehicle(new Vehicle("S010","Hero","Vida V1","Scooter","Electric",33));

            system.saveData();
        }

        system.menu();
    }
}
