package com.starking.artesanato.model.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;

import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Table(name = "usuario", schema = "artesanato")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome")
	private String nome;

	@Column(name = "email")
	@Email
	private String email;

	@Column(name = "cpf")
	@CPF
	private String cpf;

	@Column(name = "celular")
	private String celular;

	@Column(name = "senha")
	@JsonIgnore
	private String senha;

}
