package com.example.ritesh.auction.postgres.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = BidActivity.BID_ACTIVITY)
public class BidActivity {
    public static final String BID_ACTIVITY = "bid_activity";

    @Id
    @GeneratedValue
    private long id;

    private String itemCode;

    private Float bidValue;

    @Enumerated(value = EnumType.STRING)
    private HttpStatus bidStatus;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date biddingTime;

    public BidActivity(String itemCode, Float bidValue, HttpStatus bidStatus, Date biddingTime) {
        this.itemCode = itemCode;
        this.bidValue = bidValue;
        this.bidStatus = bidStatus;
        this.biddingTime = biddingTime;
    }
}
