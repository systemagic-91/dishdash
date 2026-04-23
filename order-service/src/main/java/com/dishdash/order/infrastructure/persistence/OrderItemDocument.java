package com.dishdash.order.infrastructure.persistence;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDocument {

  private String productId;
  private String productName;
  private int quantity;
  private BigDecimal unitPrice;
}
