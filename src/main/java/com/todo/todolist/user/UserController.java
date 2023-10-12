package com.todo.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    // Utilizando ResponseEntity para lançar exeções
    public ResponseEntity create(@RequestBody UserModel userModel) {
        // VERIFICA SE O USUARIO JÁ EXISTE
        var user = this.userRepository.findByUsername(userModel.getUsername());
        if (user != null) {
            // RETORNAR ERRO
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario Já Existe");
        }
        // CRIPTOGRAFANDO A SENHA PASSADA PELO USUARIO
        var passwordHash = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        // SALVANDO A SENHA NO userModel
        userModel.setPassword(passwordHash);
        // SALVA O USUARIO userModel NO BANCO
        var userCreated = this.userRepository.save(userModel);
        // RETORNA SUCESSO
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }
}