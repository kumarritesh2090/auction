package com.example.ritesh.auction.restweb.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionResponse {

    @JsonProperty(value = "Item code")
    private String itemCode;

    @JsonProperty(value = "Highest bid value")
    private Float highestBid;

    @JsonProperty(value = "Step rate")
    private Integer stepRate;
}
