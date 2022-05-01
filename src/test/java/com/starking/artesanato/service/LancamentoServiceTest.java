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
import com.starking.artesanato.model.enums.StatusLancamento;
import com.starking.artesanato.model.enums.TipoLancamento;
import com.starking.artesanato.model.repository.LancamentoRepository;
import com.starking.artesanato.model.repository.LancamentoRepositoryTest;
import com.starking.artesanato.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenário
		Pecas lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doNothing().when(service).validar(lancamentoASalvar);
		
		Pecas lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execucao
		Pecas pecas = service.salvar(lancamentoASalvar);
		
		//verificação
		assertThat( pecas.getId() ).isEqualTo(lancamentoSalvo.getId());
		assertThat(pecas.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenário
		Pecas lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doThrow( RegraNegocioException.class ).when(service).validar(lancamentoASalvar);
		
		//execucao e verificacao
		catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class );
		verify(repository, never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenário
		Pecas lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

		doNothing().when(service).validar(lancamentoSalvo);
		
		when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//execucao
		service.atualizar(lancamentoSalvo);
		
		//verificação
		verify(repository, times(1)).save(lancamentoSalvo);
		
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Pecas pecas = LancamentoRepositoryTest.criarLancamento();
		
		//execucao e verificacao
		catchThrowableOfType( () -> service.atualizar(pecas), NullPointerException.class );
		verify(repository, never()).save(pecas);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//cenário
		Pecas pecas = LancamentoRepositoryTest.criarLancamento();
		pecas.setId(1l);
		
		//execucao
		service.deletar(pecas);
		
		//verificacao
		verify( repository ).delete(pecas);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		
		//cenário
		Pecas pecas = LancamentoRepositoryTest.criarLancamento();
		
		//execucao
		catchThrowableOfType( () -> service.deletar(pecas), NullPointerException.class );
		
		//verificacao
		verify( repository, never() ).delete(pecas);
	}
	
	
	@Test
	public void deveFiltrarLancamentos() {
		//cenário
		Pecas pecas = LancamentoRepositoryTest.criarLancamento();
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
		Pecas pecas = LancamentoRepositoryTest.criarLancamento();
		pecas.setId(1l);
		pecas.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
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
		
		Pecas pecas = LancamentoRepositoryTest.criarLancamento();
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
		
		Pecas pecas = LancamentoRepositoryTest.criarLancamento();
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
				.obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO)) 
				.thenReturn(BigDecimal.valueOf(100));
		
		when( repository
				.obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO)) 
				.thenReturn(BigDecimal.valueOf(50));
		
		//execucao
		BigDecimal saldo = service.obterSaldoPorUsuario(idUsuario);
		
		//verificacao
		assertThat(saldo).isEqualTo(BigDecimal.valueOf(50));
		
	}
	
}
