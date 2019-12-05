package com.ognavi.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ognavi.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>  {

}
