package com.ognavi.minhasfinancas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ognavi.minhasfinancas.exception.RegraNegocioException;
import com.ognavi.minhasfinancas.model.entity.Lancamento;
import com.ognavi.minhasfinancas.model.entity.Usuario;
import com.ognavi.minhasfinancas.model.enums.StatusLancamento;
import com.ognavi.minhasfinancas.model.enums.TipoLancamento;
import com.ognavi.minhasfinancas.model.repository.LancamentoRepository;
import com.ognavi.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.ognavi.minhasfinancas.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	@MockBean
	LancamentoRepository repository;

	@Test
	public void deveSalvarUmLancamento() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doNothing().when(service).validar(lancamentoASalvar);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		//execução
		Lancamento lancamento = service.salvar(lancamentoASalvar);

		//verificação
		assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}

	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

		//execução e verificação
		catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		verify(repository, never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		//cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		doNothing().when(service).validar(lancamentoSalvo);

		when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		//execução
		service.atualizar(lancamentoSalvo);

		//verificação
		verify(repository, times(1)).save(lancamentoSalvo);
	}

	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		//execução e verificação
		catchThrowableOfType( () -> service.atualizar(lancamento), NullPointerException.class);
		verify(repository, never()).save(lancamento);
	}	

	@Test
	public void deveDeletarUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		//execução
		service.deletar(lancamento);

		//verificação
		verify(repository).delete(lancamento);
	}

	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		//execução
		catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class);

		//verificação
		verify(repository, never()).delete(lancamento);		
	}

	@Test
	public void deveFiltrarLancamentos() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		List<Lancamento> lista = Arrays.asList(lancamento);
		when(repository.findAll(any(Example.class))).thenReturn(lista);

		//execução
		List<Lancamento> resultado = service.buscar(lancamento);

		//verificações
		assertThat(resultado)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamento);
	}

	@Test
	public void deveAtualizaOStatusDeUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		doReturn(lancamento).when(service).atualizar(lancamento);

		//execução
		service.atualizarStatus(lancamento, novoStatus);

		//verificações
		assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		verify(service).atualizar(lancamento);
	}

	@Test
	public void deveObterUmLancamentoPorId() {
		//cenário
		Long id = 1l;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		//execução
		Optional<Lancamento> resultado = service.obterPorId(id);

		//verificação
		assertThat(resultado.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		//cenário
		Long id = 1l;

		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);

		when( repository.findById(id) ).thenReturn( Optional.empty() );

		//execução
		Optional<Lancamento> resultado = service.obterPorId(id);

		//verificação
		assertThat(resultado.isPresent()).isFalse();
	}	

	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		//cenário
		Lancamento lancamento = new Lancamento(); //para poder validar um lançamento ele precisa estar vazio

		Throwable erro = catchThrowable( () -> service.validar(lancamento) ); //vai capturar o erro que vai lançar aqui
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");

		lancamento.setDescricao("");

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida!");

		lancamento.setDescricao("Salario");

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setMes(0);

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setMes(13);

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setMes(1);

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido!");

		lancamento.setAno(202);

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido!");

		lancamento.setAno(2020);

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário!");

		lancamento.setUsuario(new Usuario());

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário!");

		lancamento.getUsuario().setId(1l);

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");

		lancamento.setValor(BigDecimal.ZERO);

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");

		lancamento.setValor(BigDecimal.valueOf(1));

		erro = catchThrowable( () -> service.validar(lancamento) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lançamento!");
	}

	@Test
	public void deveObterSaldoPorUsuario() {
		//cenário
		Long idUsuario = 1l;

		when(repository
				.obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO))
				.thenReturn(BigDecimal.valueOf(100));

		when(repository
				.obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO))
				.thenReturn(BigDecimal.valueOf(50));

		//execução
		BigDecimal saldo = service.obterSaldoPorUsuario(idUsuario);

		//verificação
		assertThat(saldo).isEqualTo(BigDecimal.valueOf(50));
	}

}