package com.dishdash.payment.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "payments")
public class PaymentDocument {

  @Id
  private String id;

  @Indexed(unique = true)
  private String orderId;

  private String customerId;
  private BigDecimal amount;
  private String status;
  private String rejectionReason;
  private LocalDateTime updatedAt;
  private LocalDateTime createdAt;
}
