package com.dishdash.inventory;

// Para centralizar o nome dos topicos
public final class KafkaTopics {

  private KafkaTopics() {}

  private static final String ORDER_CREATED = "order-created";
  private static final String STOCK_RESERVED = "stock-reserved";
  private static final String PAYMENT_PROCESSED = "payment-processed";
}
