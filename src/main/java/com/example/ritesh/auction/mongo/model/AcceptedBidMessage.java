package com.example.ritesh.auction.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptedBidMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemCode;

    private Float bidValue;

}
