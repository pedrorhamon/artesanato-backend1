package com.starking.artesanato.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.starking.artesanato.model.entity.Pecas;
import com.starking.artesanato.model.enums.StatusLancamento;

public interface PecaService {

	Pecas salvar(Pecas pecas);
	
	Pecas atualizar(Pecas pecas);
	
	void deletar(Pecas pecas);
	
	List<Pecas> buscar( Pecas pecasFiltro );
	
	void atualizarStatus(Pecas pecas, StatusLancamento status);
	
	void validar(Pecas pecas);
	
	Optional<Pecas> obterPorId(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long id);
}