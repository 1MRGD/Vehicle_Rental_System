import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int choice = 0;

        do {
            System.out.println("\n===== VEHICLE RENTAL SYSTEM =====");
            System.out.println("1. Add Vehicle");
            System.out.println("2. Show Available Vehicles");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("❌ Please enter a number!");
                sc.next();
                continue;
            }

            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    sc.nextLine(); // clear buffer

                    System.out.print("Enter vehicle name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter vehicle type (Car/Bike): ");
                    String type = sc.nextLine();

                    System.out.print("Enter price per day: ");
                    double price = sc.nextDouble();
                    sc.nextLine(); // IMPORTANT FIX

                    VehicleService.addVehicle(name, type, price);
                    break;

                case 2:
                    VehicleService.showAvailableVehicles(); // ✅ CORRECT
                    break;

                case 3:
                    System.out.println("Thank you! Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice ❌");
            }

        } while (choice != 3);

        sc.close();
    }
}
