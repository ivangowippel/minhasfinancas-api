package com.ognavi.minhasfinancas.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ognavi.minhasfinancas.model.entity.Lancamento;
import com.ognavi.minhasfinancas.model.enums.StatusLancamento;
import com.ognavi.minhasfinancas.model.enums.TipoLancamento;

@RunWith(SpringRunner.class)
@DataJpaTest	// é utilizado para testes de integração
@AutoConfigureTestDatabase(replace = Replace.NONE)	//serve para não sobre escrever as configurações de teste
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired	//injeta o LancamentoRepository
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		// cenário
		Lancamento lancamento = criarLancamento();
		
		// ação/execução
		lancamento = repository.save(lancamento);
		
		// verificação
		assertThat(lancamento.getId()).isNotNull();
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		// cenário
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		// ação/execução
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		repository.delete(lancamento);
		
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		
		// verificação
		assertThat(lancamentoInexistente).isNull();
	}

	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		lancamento.setAno(2018);
		lancamento.setDescricao("Teste Atualizar");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		
		repository.save(lancamento);
		
		// verificação
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizar");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		// cenário
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		// ação/execução
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		// verificação
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
	
	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}
	
	public static Lancamento criarLancamento() {
		return Lancamento.builder()
									.ano(2019)
									.mes(1)
									.descricao("lancamento qualquer")
									.valor(BigDecimal.valueOf(10))
									.tipo(TipoLancamento.RECEITA)
									.status(StatusLancamento.PENDENTE)
									.dataCadastro(LocalDate.now())
									.build();
	}
	
}
