package com.example.constants;

public class DBConnectionConstants {

    public static final String URL = "jdbc:mysql://localhost:3306/Ikodave";
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DATABASE_USER = System.getenv("DB_USERNAME");
    public static final String DATABASE_PASSWORD = System.getenv("DB_PASSWORD");

}
