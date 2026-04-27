package com.dishdash.order.presentation;

import com.dishdash.order.domain.exception.OrderNotFoundException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

// intercepta exceções de todos os controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

  // Por que ProblemDetail e não um objeto customizado?
  // ProblemDetail é o padrão RFC 9457 um formato de erro HTTP padronizado pela indústria.
  // O Spring Boot 3 já suporta nativamente. Usar padrões abertos é sempre
  // preferível a reinventar a roda.
  @ExceptionHandler(OrderNotFoundException.class)
  public ProblemDetail handleOrderNotFoundException(OrderNotFoundException ex) {

    ProblemDetail problem = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

    problem.setTitle("Order Not Found");
    problem.setType(URI.create("/errors/order-not-found"));
    problem.setProperty("timestamp", LocalDateTime.now());
    problem.setProperty("message", ex.getMessage());

    return problem;
  }

  @ExceptionHandler(IllegalStateException.class)
  public ProblemDetail handleIllegalState(IllegalStateException ex) {

    ProblemDetail problem = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());

    problem.setTitle("Operação inválida");
    problem.setType(URI.create("/errors/invalid-operation"));
    problem.setProperty("timestamp", LocalDateTime.now());
    problem.setProperty("message", ex.getMessage());

    return problem;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {

    ProblemDetail problem = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());

    problem.setTitle("Argumento inválido");
    problem.setType(URI.create("/errors/invalid-argument"));
    problem.setProperty("timestamp", LocalDateTime.now());
    problem.setProperty("message", ex.getMessage());

    return problem;
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public ProblemDetail handleWebExchangeBindException(WebExchangeBindException ex) {

    ProblemDetail problem = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());

    List<String> fields = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .toList();

    problem.setTitle("Dados inválidos");
    problem.setType(URI.create("/errors/validation"));
    problem.setProperty("timestamp", LocalDateTime.now());
    problem.setProperty("fields", fields);
    problem.setProperty("message", ex.getMessage());

    return problem;
  }
}
