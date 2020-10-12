package com.example.ritesh.auction.service;

import com.example.ritesh.auction.annotation.Auditable;
import com.example.ritesh.auction.postgres.model.BidActivity;
import com.example.ritesh.auction.postgres.repository.BidActivityRepository;
import com.example.ritesh.auction.restweb.request.BidRequestVo;
import org.aspectj.lang.JoinPoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.mockito.MockitoAnnotations.initMocks;

public class BiddingAuditAspectTest {

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Auditable auditable;

    @Mock
    private BidActivityRepository bidActivityRepository;

    @InjectMocks
    private BiddingAuditAspect biddingAuditAspect;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void bidAuditReturn() {
        BidRequestVo bidRequestVo = new BidRequestVo(10f);
        BidActivity bidActivity = new BidActivity("itemcode", bidRequestVo.getBidValue(), HttpStatus.ACCEPTED, new Date());
        Mockito.when(bidActivityRepository.save(Mockito.any(BidActivity.class))).thenReturn(bidActivity);
        biddingAuditAspect.bidAudit(joinPoint, new ResponseEntity(HttpStatus.ACCEPTED), auditable, bidActivity.getItemCode(), bidRequestVo);
        Mockito.verify(bidActivityRepository, Mockito.atMost(1)).save(Mockito.any(BidActivity.class));
    }

    @Test
    public void bidAuditThrow() {
        BidRequestVo bidRequestVo = new BidRequestVo(10f);
        BidActivity bidActivity = new BidActivity("itemcode", bidRequestVo.getBidValue(), HttpStatus.NOT_ACCEPTABLE, new Date());
        Mockito.when(bidActivityRepository.save(Mockito.any(BidActivity.class))).thenReturn(bidActivity);
        biddingAuditAspect.bidAudit(joinPoint, new RuntimeException(), auditable, bidActivity.getItemCode(), bidRequestVo);
        Mockito.verify(bidActivityRepository, Mockito.atMost(1)).save(Mockito.any(BidActivity.class));
    }
}