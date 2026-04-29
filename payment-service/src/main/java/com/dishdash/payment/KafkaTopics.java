package com.dishdash.payment;

// Para centralizar o nome dos topicos
public final class KafkaTopics {

  private KafkaTopics() {}

  public static final String ORDER_CREATED = "order-created";
  public static final String STOCK_RESERVED = "stock-reserved";
  public static final String PAYMENT_PROCESSED = "payment-processed";
}
