package br.com.healthsync.app;

import br.com.healthsync.dao.PacienteDAO;
import br.com.healthsync.model.Agendamento;
import br.com.healthsync.model.Especialidade;
import br.com.healthsync.model.Paciente;
import br.com.healthsync.service.AgendamentoService;
import br.com.healthsync.util.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TesteHealthSync {

    public static void main(String[] args) {
        // Instanciando os DAOs e Services
        PacienteDAO pacienteDAO = new PacienteDAO();
        AgendamentoService agendamentoService = new AgendamentoService();

        // Criando pacientes para teste
        Paciente p1 = new Paciente("Richard", "333.333.444-00", LocalDate.of(1985, 5, 20));
        Paciente p2 = new Paciente("Laura", "111.222.555-90", LocalDate.of(2010, 8, 15)); // Paciente pediátrico
        Paciente p3 = new Paciente("Maicon", "444.333.777-01", LocalDate.of(1950, 2, 10)); // Paciente geriátrico

        // 1. Testando o CRUD de Paciente
        System.out.println("--- 1. TESTE CRUD PACIENTE ---");
        pacienteDAO.create(p1);
        pacienteDAO.create(p2);
        pacienteDAO.create(p3);
        System.out.println("Pacientes cadastrados:");
        pacienteDAO.readAll().forEach(System.out::println);

        // Atualizando um paciente
        p1.setNome("Carlos Andrade da Silva");
        pacienteDAO.update(p1);
        System.out.println("\nPaciente atualizado: " + pacienteDAO.readById(p1.getId()));


        // 2. Testando Agendamento com Sucesso
        System.out.println("\n--- 2. TESTE DE AGENDAMENTO (SUCESSO) ---");
        try {
            Agendamento ag1 = new Agendamento(p1, Especialidade.CARDIOLOGIA, LocalDateTime.now().plusDays(10).withHour(14), "Dr. Ricardo Borges");
            agendamentoService.agendarConsulta(ag1);
        } catch (BusinessException e) {
            System.err.println(e.getMessage());
        }

        // 3. Testando Regra: Paciente já tem consulta no dia
        System.out.println("\n--- 3. TESTE REGRA DE NEGÓCIO (PACIENTE JÁ TEM CONSULTA) ---");
        try {
            Agendamento ag2 = new Agendamento(p1, Especialidade.DERMATOLOGIA, LocalDateTime.now().plusDays(10).withHour(16), "Dra. Ana Costa");
            agendamentoService.agendarConsulta(ag2);
        } catch (BusinessException e) {
            System.err.println("TESTE OK: " + e.getMessage());
        }

        // 4. Testando Regra: Idade incompatível com especialidade (menor para geriatra)
        System.out.println("\n--- 4. TESTE REGRA DE NEGÓCIO (IDADE INCOMPATÍVEL) ---");
        try {
            Agendamento ag3 = new Agendamento(p2, Especialidade.GERIATRIA, LocalDateTime.now().plusDays(12), "Dra. Lúcia Martins");
            agendamentoService.agendarConsulta(ag3);
        } catch (BusinessException e) {
            System.err.println("TESTE OK: " + e.getMessage());
        }

        // 5. Testando Cancelamento (antecedência inválida)
        System.out.println("\n--- 5. TESTE REGRA DE NEGÓCIO (CANCELAMENTO INVÁLIDO) ---");
        try {
            // Criando uma consulta para hoje para forçar o erro
            Agendamento consultaParaHoje = new Agendamento(p3, Especialidade.GERIATRIA, LocalDateTime.now().plusHours(2), "Dra. Lúcia Martins");
            agendamentoService.agendarConsulta(consultaParaHoje);

            // Tentando cancelar
            System.out.println("Tentando cancelar a consulta ID: " + consultaParaHoje.getId());
            agendamentoService.cancelarConsulta(consultaParaHoje.getId());

        } catch (BusinessException e) {
            System.err.println("TESTE OK: " + e.getMessage());
        }

        // 6. Testando Listagem de Agendamentos Futuros
        System.out.println("\n--- 6. LISTANDO AGENDAMENTOS FUTUROS ---");
        agendamentoService.listarProximosAgendamentos().forEach(System.out::println);
    }
}
