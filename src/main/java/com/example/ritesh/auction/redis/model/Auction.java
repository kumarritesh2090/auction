package com.example.ritesh.auction.redis.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("auction")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Auction {
    @Id
    private String itemCode;

    private Float basePrice;

    private Integer stepRate;

    @Indexed
    private String status;

    private Float highestBid;

    public Auction(String itemCode, Float basePrice, Integer stepRate, String status, Float highestBid) {
        this.itemCode = itemCode;
        this.basePrice = basePrice;
        this.stepRate = stepRate;
        this.status = status;
        this.highestBid = highestBid;
    }

}
