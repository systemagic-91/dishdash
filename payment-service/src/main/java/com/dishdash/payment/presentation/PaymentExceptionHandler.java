package com.dishdash.payment.presentation;

import com.dishdash.payment.domain.exception.PaymentNotFoundException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class PaymentExceptionHandler {

  @ExceptionHandler(PaymentNotFoundException.class)
  public ProblemDetail handleNotFound(PaymentNotFoundException ex) {

    ProblemDetail problem = ProblemDetail
        .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

    problem.setTitle("Pagamento não encontrado");
    problem.setType(URI.create("/errors/payment-not-found"));
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

  @ExceptionHandler(WebExchangeBindException.class)
  public ProblemDetail handleValidation(WebExchangeBindException ex) {

    ProblemDetail problem = ProblemDetail
        .forStatusAndDetail(HttpStatus.BAD_REQUEST, "Erro de validação");

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
