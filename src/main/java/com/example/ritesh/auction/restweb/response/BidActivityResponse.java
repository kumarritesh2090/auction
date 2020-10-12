package com.example.ritesh.auction.restweb.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidActivityResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String itemCode;

    private Float bidValue;

    private String bidStatus;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date biddingTime;
}
