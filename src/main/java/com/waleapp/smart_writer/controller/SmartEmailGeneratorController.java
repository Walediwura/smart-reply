package com.waleapp.smart_writer.controller;

import com.waleapp.smart_writer.dto.EmailRequest;
import com.waleapp.smart_writer.service.SmartEmailGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class SmartEmailGeneratorController {

  private final SmartEmailGeneratorService smartEmailGeneratorService;

  @PostMapping("/generate")
  public ResponseEntity<String> generateEmail(
    @RequestBody EmailRequest emailRequest
  ) {
    String response = smartEmailGeneratorService.generateEmailReply(emailRequest);
    return ResponseEntity.ok(response);
  }
}
