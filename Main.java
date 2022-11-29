package org.example;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {

        Connection conn = getConnection();
        Statement statement = conn.createStatement();
        ResultSet resultSet;
        
        Scanner input = new Scanner(System.in);
        System.out.println("Доступні операції: select, delete, insert, update");
        System.out.println("Напишіть операцію: ");
        String str = input.nextLine();
        System.out.println("Доступні таблиці: ");

        resultSet = statement.executeQuery("SELECT * FROM pg_catalog.pg_tables WHERE schemaname != 'pg_catalog' " +
                "AND schemaname != 'information_schema';");

        while (resultSet.next()) {
            System.out.println("\tTable: " + resultSet.getString("tablename"));
        }

        System.out.println("Вкажіть таблицю (Нариклад: car): ");
        String table = input.nextLine();
        System.out.println("Доступні колонки: ");

        resultSet = statement.executeQuery("SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = '"
                + table + "';");

        while (resultSet.next()) {
            System.out.println("\tColumn: " + resultSet.getString("column_name"));
        }

        switch (str) {
            case "select" -> {
                System.out.println("Вкажіть назву колонки на якій виконувати пошук (Наприклад name): ");
                String column = input.nextLine();

                System.out.println("Вкажіть значення для цієї колонки (Наприклад:Tesla Model S): ");
                String value = input.nextLine();

                System.out.println("Вивід таблиці " + table + "відфільтрованій по колонці " + column + " за значенням " + value);
                resultSet = statement.executeQuery("select * from " + table + " where " + column + " = " + value);
            }
            case "delete" -> {
                System.out.println("Вкажіть назву колонки по якій будуть шукатися записи для видалення (Наприклад: age): ");
                String column = input.nextLine();

                System.out.println("Вкажіть значення для колонки (Наприклад: 2009): ");
                String value = input.nextLine();

                statement.executeUpdate("delete from " + table + " where " + column + " = '" + value + "'");

                System.out.println("Вивід всієї таблиці " + table + " :");
                resultSet = statement.executeQuery("select * from " + table);
            }
            case "insert" -> {
                System.out.println("Перелік колонок в які будуть вставлятися значення(Наприклад:name,age,colour): ");
                String columns = input.nextLine();

                System.out.println("Вкажіть значення для колонок (Наприклад:'Tesla Model X','2012','red'): ");
                String value = input.nextLine();

                statement.executeUpdate("insert into " + table + "(" + columns + ") values (" + value + ")");

                System.out.println("Вивід всієї таблиці " + table + " :");
                resultSet = statement.executeQuery("select * from " + table);
            }
            case "update" -> {
                System.out.println("Вкажіть назву колонки яку треба оновити (Наприклад: age");
                String column = input.nextLine();

                System.out.println("Вкажіть значення колонки (Наприклад:2002): ");
                String value = input.nextLine();

                System.out.println("Вкажіть нове значення колонки (Наприклад:2002): ");
                String newValue = input.nextLine();

                statement.executeUpdate("update " + table + " set " + column + " = '" + newValue + "' where "
                        + column + "= '" + value + "';");

                System.out.println("Вивід оновленого рядка таблиці " + table + " :");
                resultSet = statement.executeQuery("select * from " + table + " where " + column + " = " + newValue);
            }
            default -> System.out.println("Такої операції не існує");
        }
        input.close();
        switch (table) {
            case "brands" -> printBrands(resultSet);
            case "cars" -> printCars(resultSet);
        }
    }

    private static void printCars(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            System.out.print("\tId: " + resultSet.getInt("id") + ", ");
            System.out.print("Brand: " + resultSet.getString("brand") + ", ");
            System.out.print("Model: " + resultSet.getString("model") + ", ");
            System.out.print("Year: " + resultSet.getInt("year") + ", ");
            System.out.println("Colour: " + resultSet.getString("colour"));
        }
    }
    private static void printBrands(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            System.out.print("\tBrand: " + resultSet.getString("brand") + ", ");
            System.out.println("Year: " + resultSet.getInt("year"));
        }
    }

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        Properties prop = new Properties();
        prop.setProperty("user", "postgres");
        prop.setProperty("password", "postgres");

        return DriverManager.getConnection(url, prop);
    }
}