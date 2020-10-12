package com.example.ritesh.auction.mongo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BidItem {

    @Id
    private String itemCode;

    @Field
    private Float basePrice;

    @Field
    private Integer stepRate;

    @Field
    @Indexed
    private String status;

    @Field
    private Float highestBid;

    public BidItem(String itemCode, Float basePrice, Integer stepRate, String status, Float highestBid) {
        this.itemCode = itemCode;
        this.basePrice = basePrice;
        this.stepRate = stepRate;
        this.status = status;
        this.highestBid = highestBid;
    }
}
