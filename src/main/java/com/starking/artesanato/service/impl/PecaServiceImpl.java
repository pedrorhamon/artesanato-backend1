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
import com.starking.artesanato.model.enums.StatusPagamento;
import com.starking.artesanato.model.enums.TipoPagamento;
import com.starking.artesanato.model.repository.PecasRepository;
import com.starking.artesanato.service.PecaService;
import com.starking.artesanato.utils.ConstantesUtils;

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
		pecas.setStatus(StatusPagamento.PENDENTE);
		return this.repository.save(pecas);
	}

	@Override
	@Transactional
	public Pecas atualizar(Pecas pecas) {
		Objects.requireNonNull(pecas.getId());
		validar(pecas);
		return this.repository.save(pecas);
	}

	@Override
	@Transactional
	public void deletar(Pecas pecas) {
		Objects.requireNonNull(pecas.getId());
		this.repository.delete(pecas);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pecas> buscar(Pecas lancamentoFiltro) {
		Example<Pecas> example = Example.of( lancamentoFiltro, 
				ExampleMatcher.matching()
					.withIgnoreCase()
					.withStringMatcher(StringMatcher.CONTAINING) );
		
		return this.repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Pecas pecas, StatusPagamento status) {
		pecas.setStatus(status);
		atualizar(pecas);
	}

	@Override
	public void validar(Pecas pecas) {
		
		if(pecas.getDescricao() == null || pecas.getDescricao().trim().equals("")) {
			throw new RegraNegocioException(ConstantesUtils.DESCRICAO_VALIDA);
		}
		
		if(pecas.getMes() == null || pecas.getMes() < 1 || pecas.getMes() > 12) {
			throw new RegraNegocioException(ConstantesUtils.MES_VALIDO);
		}
		
		if(pecas.getAno() == null || pecas.getAno().toString().length() != 4 ) {
			throw new RegraNegocioException(ConstantesUtils.ANO_VALIDO);
		}
		
		if(pecas.getUsuario() == null || pecas.getUsuario().getId() == null) {
			throw new RegraNegocioException(ConstantesUtils.INFORME_USUARIO);
		}
		
		if(pecas.getValor() == null || pecas.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
			throw new RegraNegocioException(ConstantesUtils.INFORME_VALOR);
		}
		
		if(pecas.getTipo() == null) {
			throw new RegraNegocioException(ConstantesUtils.INFORME_PECAS);
		}
	}

	@Override
	public Optional<Pecas> obterPorId(Long id) {
		return this.repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) {
		
		BigDecimal credito = this.repository.obterSaldoPorTipoPecaEUsuarioEStatus(id, TipoPagamento.CREDITO, StatusPagamento.EFETIVADO);
		BigDecimal pix = this.repository.obterSaldoPorTipoPecaEUsuarioEStatus(id, TipoPagamento.PIX, StatusPagamento.EFETIVADO);
		
		if(credito == null) {
			credito = BigDecimal.ZERO;
		}
		
		if(pix == null) {
			pix = BigDecimal.ZERO;
		}
		
		return credito.subtract(pix);
	}

}
