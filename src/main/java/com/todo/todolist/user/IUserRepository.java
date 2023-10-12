package com.todo.todolist.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

//Interface é o modelo dos metodos da aplicação
public interface IUserRepository extends JpaRepository<UserModel, UUID> {
    UserModel findByUsername(String username);
}
