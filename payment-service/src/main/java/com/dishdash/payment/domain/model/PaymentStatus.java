package com.dishdash.payment.domain.model;

public enum PaymentStatus {

  PENDING, // pagamento criado e aguarda processamento
  APPROVED, // pagamento aprovado
  REJECTED, // pagamento recusado (por saldo, dados invalidos, etc)
  REFUNDED // pagamento estornado (pedido cancelado apos pagamento)
}
