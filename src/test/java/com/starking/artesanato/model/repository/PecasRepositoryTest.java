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

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class PecasRepositoryTest {

	@Autowired
	PecasRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmaPeca() {
		Pecas pecas = criarPecas();
		
		pecas = repository.save(pecas);
		
		assertThat(pecas.getId()).isNotNull();
	}

	@Test
	public void deveDeletarUmLancamento() {
		Pecas pecas = criarEPersistirUmaPeca();
		
		pecas = entityManager.find(Pecas.class, pecas.getId());
		
		repository.delete(pecas);
		
		Pecas pecaInexistente = entityManager.find(Pecas.class, pecas.getId());
		assertThat(pecaInexistente).isNull();
	}

	
	@Test
	public void deveAtualizarUmaPeca() {
		Pecas pecas = criarEPersistirUmaPeca();
		
		pecas.setAno(2018);
		pecas.setDescricao("Teste Atualizar");
		pecas.setStatus(StatusLancamento.CANCELADO);
		
		repository.save(pecas);
		
		Pecas pecaAtualizado = entityManager.find(Pecas.class, pecas.getId());
		
		assertThat(pecaAtualizado.getAno()).isEqualTo(2018);
		assertThat(pecaAtualizado.getDescricao()).isEqualTo("Teste Atualizar");
		assertThat(pecaAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
	}
	
	@Test
	public void deveBuscarUmaPecaPorId() {
		Pecas pecas = criarEPersistirUmaPeca();
		
		Optional<Pecas> pecaEncontrado = repository.findById(pecas.getId());
		
		assertThat(pecaEncontrado.isPresent()).isTrue();
	}

	private Pecas criarEPersistirUmaPeca() {
		Pecas pecas = criarPecas();
		entityManager.persist(pecas);
		return pecas;
	}
	
	public static Pecas criarPecas() {
		return Pecas.builder()
									.ano(2019)
									.mes(1)
									.descricao("Peças qualquer")
									.valor(BigDecimal.valueOf(10))
									.tipo(TipoLancamento.CREDITO)
									.status(StatusLancamento.PENDENTE)
									.dataCadastro(LocalDate.now())
									.build();
	}
	
	
	
	
	
}
