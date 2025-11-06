package br.com.healthsync.dao;

import br.com.healthsync.model.Paciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public void create(Paciente paciente) {
        String sql = "INSERT INTO T_HS_PACIENTE (nm_paciente, nr_cpf, dt_nascimento) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"id_paciente"})) {

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getCpf());
            stmt.setDate(3, Date.valueOf(paciente.getDataNascimento()));
            stmt.executeUpdate();

            // Recupera o ID gerado pelo banco
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    paciente.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Paciente readById(long id) {
        String sql = "SELECT * FROM T_HS_PACIENTE WHERE id_paciente = ?";
        Paciente paciente = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paciente = new Paciente();
                    paciente.setId(rs.getLong("id_paciente"));
                    paciente.setNome(rs.getString("nm_paciente"));
                    paciente.setCpf(rs.getString("nr_cpf"));
                    paciente.setDataNascimento(rs.getDate("dt_nascimento").toLocalDate());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paciente;
    }

    public List<Paciente> readAll() {
        String sql = "SELECT * FROM T_HS_PACIENTE ORDER BY nm_paciente";
        List<Paciente> pacientes = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getLong("id_paciente"));
                paciente.setNome(rs.getString("nm_paciente"));
                paciente.setCpf(rs.getString("nr_cpf"));
                paciente.setDataNascimento(rs.getDate("dt_nascimento").toLocalDate());
                pacientes.add(paciente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pacientes;
    }

    public void update(Paciente paciente) {
        String sql = "UPDATE T_HS_PACIENTE SET nm_paciente = ?, nr_cpf = ?, dt_nascimento = ? WHERE id_paciente = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getCpf());
            stmt.setDate(3, Date.valueOf(paciente.getDataNascimento()));
            stmt.setLong(4, paciente.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM T_HS_PACIENTE WHERE id_paciente = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}