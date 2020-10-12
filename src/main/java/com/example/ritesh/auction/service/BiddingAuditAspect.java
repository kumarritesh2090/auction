package com.example.ritesh.auction.service;

import com.example.ritesh.auction.annotation.Auditable;
import com.example.ritesh.auction.postgres.model.BidActivity;
import com.example.ritesh.auction.postgres.repository.BidActivityRepository;
import com.example.ritesh.auction.restweb.request.BidRequestVo;
import lombok.extern.log4j.Log4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
@Log4j
public class BiddingAuditAspect {

    @Autowired
    private BidActivityRepository bidActivityRepository;

    @AfterReturning(pointcut = "@annotation(auditable) && args(itemcode,bidRequestVo)",
            returning = "result")
    public void bidAudit(JoinPoint joinPoint, ResponseEntity result,
                         Auditable auditable, String itemcode, BidRequestVo bidRequestVo) {
        bidActivityRepository.save(new BidActivity(itemcode, bidRequestVo.getBidValue(), result.getStatusCode(), new Date()));
    }

    @AfterThrowing(pointcut = "@annotation(auditable) && args(itemcode,bidRequestVo)",
            throwing = "exception")
    public void bidAudit(JoinPoint joinPoint, Exception exception,
                         Auditable auditable, String itemcode, BidRequestVo bidRequestVo) {
        bidActivityRepository.save(new BidActivity(itemcode, bidRequestVo.getBidValue(), HttpStatus.INTERNAL_SERVER_ERROR, new Date()));
    }

}
