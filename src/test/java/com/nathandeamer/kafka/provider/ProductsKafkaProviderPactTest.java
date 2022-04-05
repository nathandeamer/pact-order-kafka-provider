package com.nathandeamer.kafka.provider;

import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.VerificationReports;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Provider("pact-order-kafka-provider")
@PactBroker(url = "${PACT_BROKER_BASE_URL}", authentication = @PactBrokerAuth(token = "${PACT_BROKER_TOKEN}"))
@VerificationReports(value={"console", "markdown", "json"}, reportDir = "build/pact/reports")
public class ProductsKafkaPactProviderTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductsKafkaPactProviderTest.class);

  @MockBean
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private OrderService orderService;

  @Captor
  private ArgumentCaptor<Message<String>> messageArgumentCaptor;


  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void pactVerificationTestTemplate(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @BeforeEach
  void before(PactVerificationContext context) {
    context.setTarget(new MessageTestTarget());
  }

  @PactVerifyProvider("a order created event")
  public MessageAndMetadata orderCreatedEvent() throws JsonProcessingException {
    Order order = Order.builder()
            .id(999)
            .items(List.of(Order.Item.builder()
                            .description("Some Item Description")
                            .qty(99)
                            .sku("SKU1")
                    .build()))
            .build();

    orderService.publishOrder(order);

    // Capture the actual message that was sent to the topic:
    verify(kafkaTemplate).send(messageArgumentCaptor.capture());
    Message<String> message = messageArgumentCaptor.getValue();

    return generateMessageAndMetadata(message);
  }

  private MessageAndMetadata generateMessageAndMetadata(Message<String> message) {
    HashMap<String, Object> metadata = new HashMap<>(message.getHeaders());
    return new MessageAndMetadata(message.getPayload().getBytes(), metadata);
  }
}