package com.ognavi.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ognavi.minhasfinancas.exception.RegraNegocioException;
import com.ognavi.minhasfinancas.model.entity.Lancamento;
import com.ognavi.minhasfinancas.model.enums.StatusLancamento;
import com.ognavi.minhasfinancas.model.repository.LancamentoRepository;
import com.ognavi.minhasfinancas.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {

	private LancamentoRepository repository;
	
	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional //vai abrir uma transação, executa o conteúdo do método, ao final faz um commit e se der algum erro faz um rollback
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional //vai abrir uma transação, executa o conteúdo do método, ao final faz um commit e se der algum erro faz um rollback
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());	//vai garantir que vai ser passado um lançamento com id
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional //vai abrir uma transação, executa o conteúdo do método, ao final faz um commit e se der algum erro faz um rollback
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());	//vai garantir que vai ser passado um lançamento com id
		repository.delete(lancamento);
		
	}

	@Override
	@Transactional(readOnly = true) 
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		Example example = Example.of(lancamentoFiltro, ExampleMatcher
				.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma Descrição válida!");
		}
		
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe uma Mês válido!");
		}
		
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe uma Ano válido!");
		}
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe uma Usuário!");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe uma Valor válido!");
		}
		
		if(lancamento.getTipo() == null ) {
			throw new RegraNegocioException("Informe um Tipo de Lançamento!");
		}
		
		
	}

}