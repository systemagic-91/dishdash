package com.dishdash.payment.domain.exception;

public class PaymentNotFoundException extends RuntimeException {

  public PaymentNotFoundException(String paymentId) {
    super("Pagamento não encontrado: " +  paymentId);
  }
}
