package com.starking.artesanato.service.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.starking.artesanato.exception.ErroAutenticacao;
import com.starking.artesanato.exception.RegraNegocioException;
import com.starking.artesanato.model.entity.Usuario;
import com.starking.artesanato.model.repository.UsuarioRepository;
import com.starking.artesanato.service.UsuarioService;
import com.starking.artesanato.utils.*;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	private UsuarioRepository repository;
	private PasswordEncoder encoder;
	
	public UsuarioServiceImpl(
			UsuarioRepository repository, 
			PasswordEncoder encoder) {
		super();
		this.repository = repository;
		this.encoder = encoder;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = this.repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao(ConstantesUtils.USUARIO_NAO_ENCONTRADO);
		}
		
		boolean senhasBatem = encoder.matches(senha, usuario.get().getSenha());
		
		if(!senhasBatem) {
			throw new ErroAutenticacao(ConstantesUtils.SENHA_INVALIDA);
		}

		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		usuario.setCpf(usuario.getCpf());
		usuario.setCelular(usuario.getCelular());
		criptografarSenha(usuario);
		return repository.save(usuario);
	}

	private void criptografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
		String senhaCripto = encoder.encode(senha);
		usuario.setSenha(senhaCripto);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = this.repository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException(ConstantesUtils.USUARIO_CADASTRADO);
		}
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return this.repository.findById(id);
	}

}
