package com.starking.artesanato.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.starking.artesanato.model.entity.Pecas;
import com.starking.artesanato.model.enums.StatusPagamento;
import com.starking.artesanato.model.enums.TipoPagamento;

public interface PecasRepository extends JpaRepository<Pecas, Long> {

	@Query( value = 
			  " select sum(l.valor) from Pecas l join l.usuario u "
			+ " where u.id = :idUsuario and l.tipo =:tipo and l.status = :status group by u " )
	BigDecimal obterSaldoPorTipoPecaEUsuarioEStatus(
			@Param("idUsuario") Long idUsuario, 
			@Param("tipo") TipoPagamento tipo,
			@Param("status") StatusPagamento status);
	
}
