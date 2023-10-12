package com.todo.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.todo.todolist.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Pega o path da requisição
        var servletPatch = request.getServletPath();
        // Caso seja uma tasks vai validar o usuario
        if (servletPatch.startsWith("/tasks/")) {
            /*
             * pegar a autenticação
             * Validar Usuario
             * Validar Senha
             * seguir
             */

            // pegar a autenticação
            var authorization = request.getHeader("Authorization");
            // tratando autenticação
            var authorizationEncoded = authorization.substring("Basic".length()).trim();
            byte[] authorizationDecoded = Base64.getDecoder().decode(authorizationEncoded);
            var authorizationString = new String(authorizationDecoded);
            String[] credentials = authorizationString.split(":");
            String username = credentials[0];
            String password = credentials[1];
            // Validar Usuario
            var userVerify = this.userRepository.findByUsername(username);
            if (userVerify == null) {
                System.out.println("usuario invalido");
                response.sendError(401);
            } else {

                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), userVerify.getPassword());
                if (passwordVerify.verified) {
                    System.out.println("autenticado");
                    request.setAttribute("idUser", userVerify.getId());
                    filterChain.doFilter(request, response);
                } else {
                    System.out.println("senha invalida");
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
