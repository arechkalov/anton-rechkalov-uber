package com.ar.uber.dto;

import java.time.ZonedDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TripSummary {
   private String startLocation;
   private String endLocation;

   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
   private ZonedDateTime startTime;

   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
   private ZonedDateTime endTime;
   private String tripStatus;
   private String price;
   private String reward;
   private String paidPrice;
   private String paymentStatus;

   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
   private ZonedDateTime paymentInitiationTime;

   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
   private ZonedDateTime paymentConfirmTime;

}
