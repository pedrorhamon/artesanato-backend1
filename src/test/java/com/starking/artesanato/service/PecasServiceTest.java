package com.starking.artesanato.service;

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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.starking.artesanato.exception.RegraNegocioException;
import com.starking.artesanato.model.entity.Pecas;
import com.starking.artesanato.model.entity.Usuario;
import com.starking.artesanato.model.enums.StatusPagamento;
import com.starking.artesanato.model.enums.TipoPagamento;
import com.starking.artesanato.model.repository.PecasRepository;
import com.starking.artesanato.model.repository.PecasRepositoryTest;
import com.starking.artesanato.service.impl.PecaServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PecasServiceTest {

	@SpyBean
	PecaServiceImpl service;
	@MockBean
	PecasRepository repository;
	
	@Test
	public void deveSalvarUmaPeca() {
		//cenário
		Pecas pecaSalvar = PecasRepositoryTest.criarPecas();
		doNothing().when(service).validar(pecaSalvar);
		
		Pecas pecaSalvo = PecasRepositoryTest.criarPecas();
		pecaSalvo.setId(1l);
		pecaSalvo.setStatus(StatusPagamento.PENDENTE);
		when(repository.save(pecaSalvar)).thenReturn(pecaSalvo);
		
		//execucao
		Pecas pecas = service.salvar(pecaSalvar);
		
		//verificação
		assertThat( pecas.getId() ).isEqualTo(pecaSalvo.getId());
		assertThat(pecas.getStatus()).isEqualTo(StatusPagamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmaPecaQuandoHouverErroDeValidacao() {
		//cenário
		Pecas pecaSalvar = PecasRepositoryTest.criarPecas();
		doThrow( RegraNegocioException.class ).when(service).validar(pecaSalvar);
		
		//execucao e verificacao
		catchThrowableOfType( () -> service.salvar(pecaSalvar), RegraNegocioException.class );
		verify(repository, never()).save(pecaSalvar);
	}
	
	@Test
	public void deveAtualizarUmPeca() {
		//cenário
		Pecas pecaSalvo = PecasRepositoryTest.criarPecas();
		pecaSalvo.setId(1l);
		pecaSalvo.setStatus(StatusPagamento.PENDENTE);

		doNothing().when(service).validar(pecaSalvo);
		
		when(repository.save(pecaSalvo)).thenReturn(pecaSalvo);
		
		//execucao
		service.atualizar(pecaSalvo);
		
		//verificação
		verify(repository, times(1)).save(pecaSalvo);
		
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmaPecaQueAindaNaoFoiSalvo() {
		//cenário
		Pecas pecas = PecasRepositoryTest.criarPecas();
		
		//execucao e verificacao
		catchThrowableOfType( () -> service.atualizar(pecas), NullPointerException.class );
		verify(repository, never()).save(pecas);
	}
	
	@Test
	public void deveDeletarUmPeca() {
		//cenário
		Pecas pecas = PecasRepositoryTest.criarPecas();
		pecas.setId(1l);
		
		//execucao
		service.deletar(pecas);
		
		//verificacao
		verify( repository ).delete(pecas);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmaPecaQueAindaNaoFoiSalvo() {
		
		//cenário
		Pecas pecas = PecasRepositoryTest.criarPecas();
		
		//execucao
		catchThrowableOfType( () -> service.deletar(pecas), NullPointerException.class );
		
		//verificacao
		verify( repository, never() ).delete(pecas);
	}
	
	
	@Test
	public void deveFiltrarPecas() {
		//cenário
		Pecas pecas = PecasRepositoryTest.criarPecas();
		pecas.setId(1l);
		
		List<Pecas> lista = Arrays.asList(pecas);
		when( repository.findAll(any(Example.class)) ).thenReturn(lista);
		
		//execucao
		List<Pecas> resultado = service.buscar(pecas);
		
		//verificacoes
		assertThat(resultado)
			.isNotEmpty()
			.hasSize(1)
			.contains(pecas);
		
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//cenário
		Pecas pecas = PecasRepositoryTest.criarPecas();
		pecas.setId(1l);
		pecas.setStatus(StatusPagamento.PENDENTE);
		
		StatusPagamento novoStatus = StatusPagamento.EFETIVADO;
		doReturn(pecas).when(service).atualizar(pecas);
		
		//execucao
		service.atualizarStatus(pecas, novoStatus);
		
		//verificacoes
		assertThat(pecas.getStatus()).isEqualTo(novoStatus);
		verify(service).atualizar(pecas);
		
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		//cenário
		Long id = 1l;
		
		Pecas pecas = PecasRepositoryTest.criarPecas();
		pecas.setId(id);
		
		when(repository.findById(id)).thenReturn(Optional.of(pecas));
		
		//execucao
		Optional<Pecas> resultado =  service.obterPorId(id);
		
		//verificacao
		assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveREtornarVazioQuandoOLancamentoNaoExiste() {
		//cenário
		Long id = 1l;
		
		Pecas pecas = PecasRepositoryTest.criarPecas();
		pecas.setId(id);
		
		when( repository.findById(id) ).thenReturn( Optional.empty() );
		
		//execucao
		Optional<Pecas> resultado =  service.obterPorId(id);
		
		//verificacao
		assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Pecas pecas = new Pecas();
		
		Throwable erro = Assertions.catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		pecas.setDescricao("");
		
		erro = Assertions.catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		pecas.setDescricao("Salario");
		
		erro = Assertions.catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		pecas.setAno(0);
		
		erro = catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		pecas.setAno(13);
		
		erro = catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		pecas.setMes(1);
		
		erro = catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		pecas.setAno(202);
		
		erro = catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		pecas.setAno(2020);
		
		erro = catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		pecas.setUsuario(new Usuario());
		
		erro = catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		pecas.getUsuario().setId(1l);
		
		erro = catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		pecas.setValor(BigDecimal.ZERO);
		
		erro = catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		pecas.setValor(BigDecimal.valueOf(1));
		
		erro = catchThrowable( () -> service.validar(pecas) );
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de Lançamento.");
		
	}
	
	@Test
	public void deveObterSaldoPorUsuario() {
		//cenario
		Long idUsuario = 1l;
		
		when( repository
				.obterSaldoPorTipoPecaEUsuarioEStatus(idUsuario, TipoPagamento.CREDITO, StatusPagamento.EFETIVADO)) 
				.thenReturn(BigDecimal.valueOf(100));
		
		when( repository
				.obterSaldoPorTipoPecaEUsuarioEStatus(idUsuario, TipoPagamento.PIX, StatusPagamento.EFETIVADO)) 
				.thenReturn(BigDecimal.valueOf(50));
		
		//execucao
		BigDecimal saldo = service.obterSaldoPorUsuario(idUsuario);
		
		//verificacao
		assertThat(saldo).isEqualTo(BigDecimal.valueOf(50));
		
	}
	
}
