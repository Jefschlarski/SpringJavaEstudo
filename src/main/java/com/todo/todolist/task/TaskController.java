package com.todo.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todo.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

        // System.out.println(request.getAttribute("idUser"));
        // Pegando o idUser do request que vem do filter e adicionando na task
        var uuidUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) uuidUser);

        // VALIDAR DATAS
        var currentData = LocalDateTime.now();
        if (currentData.isAfter(taskModel.getStartAt()) || currentData.isAfter(taskModel.getEndAt())) {
            System.out.println("As datas não podem ser anteriores a data atual");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("As datas não podem ser anteriores a data atual");
        }
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            System.out.println("A data inicial deve ser anterior a data de término");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data inicial deve ser anterior a data de término");
        }

        var task = this.taskRepository.save(taskModel);
        System.out.println("A tarefa " + task.getTitle() + " foi criada");
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var uuidUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) uuidUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);
        var uuidUser = request.getAttribute("idUser");

        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada");
        }

        if (!task.getIdUser().equals(uuidUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário nao tem permissão para alterar essa tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task);
        var taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }
}
