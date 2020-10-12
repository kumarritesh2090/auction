package com.example.ritesh.auction.service;

import com.example.ritesh.auction.postgres.model.BidActivity;
import com.example.ritesh.auction.postgres.repository.BidActivityRepository;
import com.example.ritesh.auction.restweb.response.BidActivityResponse;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j
public class BidActivityService {

    @Autowired
    private BidActivityRepository bidActivityRepository;

    public List<BidActivityResponse> getAllBiddingActivityByItemCode(String itemCode, PageRequest pageRequest) {
        try {
            Page<BidActivity> bidActivities = bidActivityRepository.getByItemCode(itemCode, pageRequest);
            return bidActivities.getContent().stream().map(bidActivity -> new BidActivityResponse(bidActivity.getItemCode(), bidActivity.getBidValue(),
                    bidActivity.getBidStatus().name(), bidActivity.getBiddingTime())).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch bidding activities, please retry ", e.getCause());

        }

    }

}
