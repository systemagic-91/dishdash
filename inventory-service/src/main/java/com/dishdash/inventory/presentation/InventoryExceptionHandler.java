package com.dishdash.inventory.presentation;

import com.dishdash.inventory.domain.exception.InsufficientStockException;
import com.dishdash.inventory.domain.exception.ProductNotFoundException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class InventoryExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ProblemDetail handleNotFound(ProductNotFoundException ex) {

    ProblemDetail problem = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

    problem.setTitle("Produto não encontrado");
    problem.setType(URI.create("/errors/product-not-found"));
    problem.setProperty("timestamp", LocalDateTime.now());

    return problem;
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ProblemDetail handleInsufficientStock(InsufficientStockException ex) {

    // 422 Unprocessable Content -> a requisição é válida, mas a regra de negócio não permite
    ProblemDetail problem = ProblemDetail
        .forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());

    problem.setTitle("Estoque insuficiente");
    problem.setType(URI.create("/errors/insufficient-stock"));
    problem.setProperty("timestamp", LocalDateTime.now());

    return problem;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {

    ProblemDetail problem = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());

    problem.setTitle("Argumento inválido");
    problem.setType(URI.create("/errors/invalid-argument"));
    problem.setProperty("timestamp", LocalDateTime.now());

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

    return problem;
  }
}
