package com.dishdash.order.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Order {

  private final String id;
  private final CustomerId customerId;
  private final List<OrderItem> items;
  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private OrderStatus status;

  // construtor privado para a propria classe controlar COMO um Order é criado
  private Order(String id, CustomerId customerId, List<OrderItem> items,  LocalDateTime createdAt,
      LocalDateTime updatedAt, OrderStatus status) {

    this.id = id;
    this.customerId = customerId;
    // copia a lista recebida em vez de guardar referencia da original
    // mesmo conceito de deep copy e shadow copy em python
    // evita que alteracoes na referencia recebida de fora reflitam no pedido criado
    this.items = new ArrayList<>(items);
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.status = status;
  }

  // esse metodo define como um produto novo deve nascer
  public static Order create(CustomerId customerId, List<OrderItem> items) {

    if (items == null || items.isEmpty()) {
      throw new IllegalArgumentException("Pedido deve ter pelo menos um item");
    }

    return new Order(
        UUID.randomUUID().toString(),
        customerId,
        items,
        LocalDateTime.now(),
        LocalDateTime.now(),
        OrderStatus.PENDING);
  }

  // quando é necessario buscar um registro no banco nao e necessario validar regras de
  // criacao de produto novamente, é para esse tipo de criacao que esse metodo existe.
  public static Order reconstitute(String id, CustomerId customerId, List<OrderItem> items,
      LocalDateTime createdAt,  LocalDateTime updatedAt, OrderStatus status) {

    return new Order(id, customerId, items, createdAt, updatedAt, status);
  }

  // garante regras de negocio para cancelar pedidos
  public void cancel() {

    if (status == OrderStatus.PAID)
      throw new IllegalStateException("Pedido já pago não pode ser cancelado");

    if (status == OrderStatus.CANCELLED)
      throw new IllegalStateException("Pedido já está cancelado");

    this.status = OrderStatus.CANCELLED;
    this.updatedAt = LocalDateTime.now();
  }

  public void confirm() {

    if (this.status == OrderStatus.PENDING)
      throw new IllegalStateException("Apenas pedidos PENDING podem ser confirmados");

    this.status = OrderStatus.CONFIRMED;
    this.updatedAt = LocalDateTime.now();
  }

  // para que nao seja possivel modificar a lista de itens por fora
  public List<OrderItem> getItems() {
    return Collections.unmodifiableList(items);
  }

  public BigDecimal totalAmount(){

    return items.stream()
        .map(OrderItem::totalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
