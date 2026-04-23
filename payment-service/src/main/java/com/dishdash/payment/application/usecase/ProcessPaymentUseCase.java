package com.dishdash.payment.application.usecase;

import com.dishdash.payment.application.dto.PaymentResponse;
import com.dishdash.payment.application.dto.ProcessPaymentRequest;
import com.dishdash.payment.domain.model.Payment;
import com.dishdash.payment.domain.repository.PaymentRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessPaymentUseCase {

  private final PaymentRepository paymentRepository;

  public Mono<PaymentResponse> execute(ProcessPaymentRequest request) {

    return paymentRepository.findByOrderId(request.orderId())
        .switchIfEmpty(Mono.defer(() -> createAndProcess(request)))
        .map(PaymentResponse::from);
  }

  private Mono<Payment> createAndProcess(ProcessPaymentRequest request) {

    Payment payment = Payment.create(
        request.orderId(),
        request.customerId(),
        request.amount()
    );

    // simulacao de processamento de pagamento:
    if(request.amount().compareTo(new BigDecimal("10000")) > 0) {

      log.info("Pagamento rejeitado para pedido {} - valor acima do limite", request.orderId());

      payment.reject("Valor acima do limite permitido");
    } else {

      log.info("Pagamento aprovado para pedido {}",  request.orderId());

      payment.approve();
    }

    return paymentRepository.save(payment);
  }
}
