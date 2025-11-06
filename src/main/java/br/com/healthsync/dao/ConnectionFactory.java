package br.com.healthsync.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";
    private static final String USER = "rm566127";
    private static final String PASSWORD = "030307";

    public static Connection getConnection() {
        try {
            // Carrega o driver JDBC do Oracle
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Retorna a conexão com o banco de dados
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver Oracle JDBC não encontrado.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao obter a conexão com o banco de dados.");
            e.printStackTrace();
            return null;
        }
    }
}