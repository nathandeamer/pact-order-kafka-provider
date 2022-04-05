package com.nathandeamer.kafka.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Order {

  private int id;
  private List<Item> items;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Item {
    private String sku;
    private String description;
    private int qty;
  }

}