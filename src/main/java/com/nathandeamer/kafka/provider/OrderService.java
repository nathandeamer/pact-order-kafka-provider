package com.nathandeamer.kafka.provider;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class OrderService {

  private final KafkaTemplate<String, String> kafkaTemplate;

  public void publishOrder(final Order order) {
    log.info("writing order to kafka: {}", order);

    try {
      Message<String> message = new OrderMessageBuilder().withOrder(order).build();
      kafkaTemplate.send(message);
    } catch (final JsonProcessingException e) {
      log.error("unable to serialise product to JSON", e);
    }
  }

}