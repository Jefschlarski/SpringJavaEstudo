package com.todo.todolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

//@Data cria os getters e setters
@Data
@Entity(name = "tb_users") // CRIANDO A TABELA
public class UserModel {

    @Id // DECLARANDO A CHAVE PRIMARIA
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @Column(unique = true)
    private String username;
    private String name;
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
