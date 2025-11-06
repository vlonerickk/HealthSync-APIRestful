package br.com.healthsync.dao;

import br.com.healthsync.model.Agendamento;
import br.com.healthsync.model.Especialidade;
import br.com.healthsync.model.Paciente;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO {

    public void create(Agendamento agendamento) {
        String sql = "INSERT INTO T_HS_AGENDAMENTO (id_paciente, ds_especialidade, dt_hora_agendamento, nm_medico) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"id_agendamento"})) {

            stmt.setLong(1, agendamento.getPaciente().getId());
            stmt.setString(2, agendamento.getEspecialidade().name());
            stmt.setTimestamp(3, Timestamp.valueOf(agendamento.getDataHora()));
            stmt.setString(4, agendamento.getNomeMedico());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    agendamento.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Agendamento readById(long id) {
        String sql = "SELECT * FROM T_HS_AGENDAMENTO WHERE id_agendamento = ?";
        Agendamento agendamento = null;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    agendamento = new Agendamento();
                    agendamento.setId(rs.getLong("id_agendamento"));

                    PacienteDAO pacienteDAO = new PacienteDAO();
                    Paciente paciente = pacienteDAO.readById(rs.getLong("id_paciente"));
                    agendamento.setPaciente(paciente);

                    agendamento.setEspecialidade(Especialidade.valueOf(rs.getString("ds_especialidade")));
                    agendamento.setDataHora(rs.getTimestamp("dt_hora_agendamento").toLocalDateTime());
                    agendamento.setNomeMedico(rs.getString("nm_medico"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agendamento;
    }

    public void update(Agendamento agendamento) {
        String sql = "UPDATE T_HS_AGENDAMENTO SET id_paciente = ?, ds_especialidade = ?, dt_hora_agendamento = ?, nm_medico = ? WHERE id_agendamento = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, agendamento.getPaciente().getId());
            stmt.setString(2, agendamento.getEspecialidade().name());
            stmt.setTimestamp(3, Timestamp.valueOf(agendamento.getDataHora()));
            stmt.setString(4, agendamento.getNomeMedico());
            stmt.setLong(5, agendamento.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(long id) {
        String sql = "DELETE FROM T_HS_AGENDAMENTO WHERE id_agendamento = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Métodos para Regras de Negócio
    public boolean pacientePossuiConsultaNoDia(long pacienteId, LocalDate dia) {
        String sql = "SELECT COUNT(*) FROM T_HS_AGENDAMENTO WHERE id_paciente = ? AND TRUNC(dt_hora_agendamento) = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, pacienteId);
            stmt.setDate(2, Date.valueOf(dia));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean medicoPossuiConsultaNoHorario(String nomeMedico, LocalDateTime dataHora) {
        String sql = "SELECT COUNT(*) FROM T_HS_AGENDAMENTO WHERE nm_medico = ? AND dt_hora_agendamento = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nomeMedico);
            stmt.setTimestamp(2, Timestamp.valueOf(dataHora));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Agendamento> findUpcoming() {
        String sql = "SELECT * FROM T_HS_AGENDAMENTO WHERE dt_hora_agendamento > CURRENT_TIMESTAMP ORDER BY dt_hora_agendamento";
        List<Agendamento> agendamentos = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Agendamento agendamento = new Agendamento();
                agendamento.setId(rs.getLong("id_agendamento"));

                PacienteDAO pacienteDAO = new PacienteDAO();
                Paciente paciente = pacienteDAO.readById(rs.getLong("id_paciente"));
                agendamento.setPaciente(paciente);

                agendamento.setEspecialidade(Especialidade.valueOf(rs.getString("ds_especialidade")));
                agendamento.setDataHora(rs.getTimestamp("dt_hora_agendamento").toLocalDateTime());
                agendamento.setNomeMedico(rs.getString("nm_medico"));
                agendamentos.add(agendamento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agendamentos;
    }
}