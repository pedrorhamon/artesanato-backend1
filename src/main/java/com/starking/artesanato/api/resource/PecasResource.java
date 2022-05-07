package com.starking.artesanato.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.starking.artesanato.api.dto.AtualizaStatusDTO;
import com.starking.artesanato.api.dto.PecasDTO;
import com.starking.artesanato.exception.RegraNegocioException;
import com.starking.artesanato.model.entity.Pecas;
import com.starking.artesanato.model.entity.Usuario;
import com.starking.artesanato.model.enums.StatusPagamento;
import com.starking.artesanato.model.enums.TipoPagamento;
import com.starking.artesanato.service.PecaService;
import com.starking.artesanato.service.UsuarioService;
import com.starking.artesanato.utils.ConstantesUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pecas")
@RequiredArgsConstructor
public class PecasResource {

	private final PecaService service;
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity<?> buscar(
			@RequestParam(value ="descricao" , required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam("usuario") Long idUsuario
			) {
		
		Pecas pecasFiltro = new Pecas();
		pecasFiltro.setDescricao(descricao);
		pecasFiltro.setMes(mes);
		pecasFiltro.setAno(ano);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body(ConstantesUtils.USUARIO_NAO_ENCONTRADO_ID);
		}else {
			pecasFiltro.setUsuario(usuario.get());
		}
		
		List<Pecas> pecas = service.buscar(pecasFiltro);
		return ResponseEntity.ok(pecas);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> obterPeca( @PathVariable("id") Long id ) {
		return service.obterPorId(id)
					.map( pecas -> new ResponseEntity<>(converter(pecas), HttpStatus.OK) )
					.orElseGet( () -> new ResponseEntity<>(HttpStatus.NOT_FOUND) );
	}

	@PostMapping
	public ResponseEntity<?> salvar( @RequestBody PecasDTO dto ) {
		try {
			Pecas peca = converter(dto);
			peca = service.salvar(peca);
			return new ResponseEntity<>(peca, HttpStatus.CREATED);
		}catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> atualizar( @PathVariable("id") Long id, @RequestBody PecasDTO dto ) {
		return service.obterPorId(id).map( entity -> {
			try {
				Pecas pecas = converter(dto);
				pecas.setId(entity.getId());
				service.atualizar(pecas);
				return ResponseEntity.ok(pecas);
			}catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet( () ->
			new ResponseEntity<>(ConstantesUtils.PECA_NAO_ENCONTRADA, HttpStatus.BAD_REQUEST) );
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity<?> atualizarStatus( @PathVariable("id") Long id , @RequestBody AtualizaStatusDTO dto ) {
		return service.obterPorId(id).map( entity -> {
			StatusPagamento statusSelecionado = StatusPagamento.valueOf(dto.getStatus());
			
			if(statusSelecionado == null) {
				return ResponseEntity.badRequest().body(ConstantesUtils.ATUALIZAR_STATUS_PECA);
			}
			
			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			}catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		
		}).orElseGet( () ->
		new ResponseEntity<>(ConstantesUtils.PECA_NAO_ENCONTRADA, HttpStatus.BAD_REQUEST) );
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deletar( @PathVariable("id") Long id ) {
		return service.obterPorId(id).map( entidade -> {
			service.deletar(entidade);
			return new ResponseEntity<>( HttpStatus.NO_CONTENT );
		}).orElseGet( () -> 
			new ResponseEntity<>(ConstantesUtils.PECA_NAO_ENCONTRADA, HttpStatus.BAD_REQUEST) );
	}
	
	private PecasDTO converter(Pecas pecas) {
		return PecasDTO.builder()
					.id(pecas.getId())
					.descricao(pecas.getDescricao())
					.valor(pecas.getValor())
					.mes(pecas.getMes())
					.ano(pecas.getAno())
					.status(pecas.getStatus().name())
					.tipo(pecas.getTipo().name())
					.usuario(pecas.getUsuario().getId())
					.build();
					
	}
	
	private Pecas converter(PecasDTO dto) {
		Pecas pecas = new Pecas();
		pecas.setId(dto.getId());
		pecas.setDescricao(dto.getDescricao());
		pecas.setAno(dto.getAno());
		pecas.setMes(dto.getMes());
		pecas.setValor(dto.getValor());
		
		Usuario usuario = usuarioService
			.obterPorId(dto.getUsuario())
			.orElseThrow( () -> new RegraNegocioException(ConstantesUtils.USUARIO_NAO_ENCONTRADO_ID) );
		
		pecas.setUsuario(usuario);

		if(dto.getTipo() != null) {
			pecas.setTipo(TipoPagamento.valueOf(dto.getTipo()));
		}
		
		if(dto.getStatus() != null) {
			pecas.setStatus(StatusPagamento.valueOf(dto.getStatus()));
		}
		
		return pecas;
	}
}
