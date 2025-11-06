package br.com.healthsync.service;

import br.com.healthsync.dao.AgendamentoDAO;
import br.com.healthsync.model.Agendamento;
import br.com.healthsync.model.Especialidade;
import br.com.healthsync.model.Paciente;
import br.com.healthsync.util.BusinessException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

public class AgendamentoService {

    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();

    /**
     * MÉTODO 1: Agendar uma nova consulta com validações complexas.
     * Regras de negócio:
     * 1. Um paciente não pode agendar duas consultas no mesmo dia.
     * 2. Um médico não pode ter dois agendamentos no mesmo horário.
     * 3. Valida a idade do paciente para a especialidade (ex: Pediatria, Geriatria).
     */
    public void agendarConsulta(Agendamento novoAgendamento) throws BusinessException {
        // Regra 1: Paciente não pode ter outra consulta no mesmo dia
        if (agendamentoDAO.pacientePossuiConsultaNoDia(novoAgendamento.getPaciente().getId(), novoAgendamento.getDataHora().toLocalDate())) {
            throw new BusinessException("Erro: O paciente já possui uma consulta agendada para este dia.");
        }

        // Regra 2: Médico não pode ter consulta no mesmo horário
        if (agendamentoDAO.medicoPossuiConsultaNoHorario(novoAgendamento.getNomeMedico(), novoAgendamento.getDataHora())) {
            throw new BusinessException("Erro: O médico selecionado não está disponível neste horário.");
        }

        // Regra 3: Validação de idade por especialidade
        validarIdadeParaEspecialidade(novoAgendamento.getPaciente(), novoAgendamento.getEspecialidade());

        agendamentoDAO.create(novoAgendamento);
        System.out.println("Agendamento realizado com sucesso para " + novoAgendamento.getPaciente().getNome() + "!");
    }

    /**
     * MÉTODO 2: Cancelar uma consulta.
     * Regra de negócio:
     * 1. A consulta só pode ser cancelada com no mínimo 24 horas de antecedência.
     */
    public void cancelarConsulta(long agendamentoId) throws BusinessException {
        Agendamento agendamento = agendamentoDAO.readById(agendamentoId);
        if (agendamento == null) {
            throw new BusinessException("Agendamento não encontrado.");
        }

        long horasDeAntecedencia = Duration.between(LocalDateTime.now(), agendamento.getDataHora()).toHours();

        if (horasDeAntecedencia < 24) {
            throw new BusinessException("Cancelamento não permitido. A consulta deve ser cancelada com no mínimo 24 horas de antecedência.");
        }

        agendamentoDAO.delete(agendamentoId);
        System.out.println("Agendamento ID " + agendamentoId + " cancelado com sucesso.");
    }

    /**
     * MÉTODO 3: Remarcar uma consulta.
     * Regra de negócio:
     * 1. Aplica a mesma regra de antecedência de 24 horas para remarcação.
     * 2. Verifica a disponibilidade do médico no novo horário.
     */
    public void remarcarConsulta(long agendamentoId, LocalDateTime novaDataHora) throws BusinessException {
        Agendamento agendamento = agendamentoDAO.readById(agendamentoId);
        if (agendamento == null) {
            throw new BusinessException("Agendamento não encontrado.");
        }

        long horasDeAntecedencia = Duration.between(LocalDateTime.now(), agendamento.getDataHora()).toHours();
        if (horasDeAntecedencia < 24) {
            throw new BusinessException("Remarcação não permitida com menos de 24 horas de antecedência.");
        }

        if (agendamentoDAO.medicoPossuiConsultaNoHorario(agendamento.getNomeMedico(), novaDataHora)) {
            throw new BusinessException("Erro: O médico não está disponível no novo horário selecionado.");
        }

        agendamento.setDataHora(novaDataHora);
        agendamentoDAO.update(agendamento);
        System.out.println("Agendamento ID " + agendamentoId + " remarcado com sucesso para " + novaDataHora);
    }

    /**
     * MÉTODO 4 (Auxiliar de Lógica): Validar idade do paciente para a especialidade.
     * Regras de negócio (criativas):
     * 1. Pediatria: Apenas para pacientes com até 16 anos.
     * 2. Geriatria: Apenas para pacientes com 60 anos ou mais.
     */
    private void validarIdadeParaEspecialidade(Paciente paciente, Especialidade especialidade) throws BusinessException {
        int idade = Period.between(paciente.getDataNascimento(), LocalDate.now()).getYears();

        if (especialidade == Especialidade.PEDIATRIA && idade > 16) {
            throw new BusinessException("Especialidade 'Pediatria' é permitida apenas para pacientes com até 16 anos.");
        }
        if (especialidade == Especialidade.GERIATRIA && idade < 60) {
            throw new BusinessException("Especialidade 'Geriatria' é recomendada para pacientes com 60 anos ou mais.");
        }
    }

    public List<Agendamento> listarProximosAgendamentos() {
        return agendamentoDAO.findUpcoming();
    }
}