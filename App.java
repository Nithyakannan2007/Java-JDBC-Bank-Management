
import java.sql.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        String url = "jdbc:oracle:thin:@10.9.66.89:1521/FREEPDB1";
        Connection con = DriverManager.getConnection(
            url, "system", "Nithya");

        Statement st = con.createStatement();
        st.executeUpdate(
            "CREATE TABLE bank_accounts (" +
            "acc_no NUMBER PRIMARY KEY, " +
            "name VARCHAR2(50), balance NUMBER(12,2))");

        while (true) {
            System.out.println("\n1.Create Account");
            System.out.println("2.Deposit");
            System.out.println("3.Withdraw");
            System.out.println("4.Display Balance");
            System.out.println("5.Exit");
            System.out.print("Choice: ");
            int ch = sc.nextInt();

            if (ch == 5) break;

            System.out.print("Account Number: ");
            int acc = sc.nextInt();

            if (ch == 1) {
                sc.nextLine();
                System.out.print("Customer Name: ");
                String name = sc.nextLine();
                System.out.print("Initial Balance: ");
                double bal = sc.nextDouble();

                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO bank_accounts VALUES(?,?,?)");
                ps.setInt(1, acc);
                ps.setString(2, name);
                ps.setDouble(3, bal);
                ps.executeUpdate();
                System.out.println("Account Created!");
            } else if (ch == 2 || ch == 3) {
                System.out.print("Amount: ");
                double amount = sc.nextDouble();

                PreparedStatement ps = con.prepareStatement(
                    "SELECT balance FROM bank_accounts WHERE acc_no=?");
                ps.setInt(1, acc);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("Account Not Found!");
                    continue;
                }

                double bal = rs.getDouble(1);

                if (amount <= 0) {
                    System.out.println("Invalid Amount!");
                    continue;
                }
                if (ch == 3 && amount > bal) {
                    System.out.println("Insufficient Balance!");
                    continue;
                }

                PreparedStatement up = con.prepareStatement(
                    "UPDATE bank_accounts SET balance=? WHERE acc_no=?");
                up.setDouble(1, ch == 2 ? bal + amount : bal - amount);
                up.setInt(2, acc);
                up.executeUpdate();
                System.out.println("Transaction Successful!");
            } else if (ch == 4) {
                PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM bank_accounts WHERE acc_no=?");
                ps.setInt(1, acc);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Balance: " + rs.getDouble("balance"));
                } else {
                    System.out.println("Account Not Found!");
                }
            }
        }

        con.close();
        sc.close();
    }
}
