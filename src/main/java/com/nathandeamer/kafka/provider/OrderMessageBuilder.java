package com.nathandeamer.kafka.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class OrderMessageBuilder {

  private final ObjectMapper mapper = new ObjectMapper();
  private Order order;

  public OrderMessageBuilder withOrder(Order order) {
    this.order = order;
    return this;
  }

  public Message<String> build() throws JsonProcessingException {
    return MessageBuilder.withPayload(this.mapper.writeValueAsString(this.order))
        .setHeader(KafkaHeaders.TOPIC, "orders")
            .setHeader("Content-Type", "application/json; charset=utf-8")
        .build();
  }

}