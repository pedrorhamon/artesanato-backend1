package com.starking.artesanato.api.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.br.CPF;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

	@Email(message = "{email.not.blank}")
	@NotBlank
	private String email;
	
	@NotBlank(message = "{nome.not.blank}")
	private String nome;
	
	@CPF
	@NotBlank(message = "{cpf.not.blank}")
	private String cpf;
	
	@NotBlank(message = "{celular.not.blank}")
	private String celular;
	
	@NotBlank(message = "{senha.not.blank}")
	private String senha;
}
