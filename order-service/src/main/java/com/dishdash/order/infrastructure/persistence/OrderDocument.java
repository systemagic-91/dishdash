package com.dishdash.order.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class OrderDocument {

  @Id
  private String id;

  private String customerId;
  private List<OrderItemDocument> items;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
