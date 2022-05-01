package com.starking.artesanato.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.starking.artesanato.model.entity.Pecas;
import com.starking.artesanato.model.enums.StatusLancamento;
import com.starking.artesanato.model.enums.TipoLancamento;
import com.starking.artesanato.model.repository.LancamentoRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		Pecas pecas = criarLancamento();
		
		pecas = repository.save(pecas);
		
		assertThat(pecas.getId()).isNotNull();
	}

	@Test
	public void deveDeletarUmLancamento() {
		Pecas pecas = criarEPersistirUmLancamento();
		
		pecas = entityManager.find(Pecas.class, pecas.getId());
		
		repository.delete(pecas);
		
		Pecas lancamentoInexistente = entityManager.find(Pecas.class, pecas.getId());
		assertThat(lancamentoInexistente).isNull();
	}

	
	@Test
	public void deveAtualizarUmLancamento() {
		Pecas pecas = criarEPersistirUmLancamento();
		
		pecas.setAno(2018);
		pecas.setDescricao("Teste Atualizar");
		pecas.setStatus(StatusLancamento.CANCELADO);
		
		repository.save(pecas);
		
		Pecas lancamentoAtualizado = entityManager.find(Pecas.class, pecas.getId());
		
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizar");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}
	
	@Test
	public void deveBuscarUmLancamentoPorId() {
		Pecas pecas = criarEPersistirUmLancamento();
		
		Optional<Pecas> lancamentoEncontrado = repository.findById(pecas.getId());
		
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}

	private Pecas criarEPersistirUmLancamento() {
		Pecas pecas = criarLancamento();
		entityManager.persist(pecas);
		return pecas;
	}
	
	public static Pecas criarLancamento() {
		return Pecas.builder()
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
