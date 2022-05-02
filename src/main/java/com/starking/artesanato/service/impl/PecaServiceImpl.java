package com.starking.artesanato.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.starking.artesanato.exception.RegraNegocioException;
import com.starking.artesanato.model.entity.Pecas;
import com.starking.artesanato.model.enums.StatusLancamento;
import com.starking.artesanato.model.enums.TipoLancamento;
import com.starking.artesanato.model.repository.PecasRepository;
import com.starking.artesanato.service.PecaService;

@Service
public class PecaServiceImpl implements PecaService {
	
	private PecasRepository repository;
	
	public PecaServiceImpl(PecasRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Pecas salvar(Pecas pecas) {
		validar(pecas);
		pecas.setStatus(StatusLancamento.PENDENTE);
		return repository.save(pecas);
	}

	@Override
	@Transactional
	public Pecas atualizar(Pecas pecas) {
		Objects.requireNonNull(pecas.getId());
		validar(pecas);
		return repository.save(pecas);
	}

	@Override
	@Transactional
	public void deletar(Pecas pecas) {
		Objects.requireNonNull(pecas.getId());
		repository.delete(pecas);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pecas> buscar(Pecas lancamentoFiltro) {
		Example example = Example.of( lancamentoFiltro, 
				ExampleMatcher.matching()
					.withIgnoreCase()
					.withStringMatcher(StringMatcher.CONTAINING) );
		
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Pecas pecas, StatusLancamento status) {
		pecas.setStatus(status);
		atualizar(pecas);
	}

	@Override
	public void validar(Pecas pecas) {
		
		if(pecas.getDescricao() == null || pecas.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma Descrição válida.");
		}
		
		if(pecas.getMes() == null || pecas.getMes() < 1 || pecas.getMes() > 12) {
			throw new RegraNegocioException("Informe um Mês válido.");
		}
		
		if(pecas.getAno() == null || pecas.getAno().toString().length() != 4 ) {
			throw new RegraNegocioException("Informe um Ano válido.");
		}
		
		if(pecas.getUsuario() == null || pecas.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um Usuário.");
		}
		
		if(pecas.getValor() == null || pecas.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
			throw new RegraNegocioException("Informe um Valor válido.");
		}
		
		if(pecas.getTipo() == null) {
			throw new RegraNegocioException("Informe um tipo de Lançamento.");
		}
	}

	@Override
	public Optional<Pecas> obterPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) {
		
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.CREDITO, StatusLancamento.EFETIVADO);
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.PIX, StatusLancamento.EFETIVADO);
		
		if(receitas == null) {
			receitas = BigDecimal.ZERO;
		}
		
		if(despesas == null) {
			despesas = BigDecimal.ZERO;
		}
		
		return receitas.subtract(despesas);
	}

}
