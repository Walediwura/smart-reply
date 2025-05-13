package com.waleapp.smart_writer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waleapp.smart_writer.dto.EmailRequest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SmartEmailGeneratorService {

  private final WebClient webClient;

  @Value("${gemini.api.url}")
  private String geminiApiUrl;

  @Value("${gemini.api.key}")
  private String geminiApiKey;

    public SmartEmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    public String generateEmailReply(EmailRequest emailRequest) {
    // Build our Prompt
    String prompt = buildPrompt(emailRequest);

    // Structure thE request to fit gemini's format
    Map<String, Object> requestBody = Map.of(
      "contents",
      new Object[] { Map.of("parts", new Object[] { Map.of("text", prompt) }) }
    );

    // Dispatch request and get response
    String response = webClient.post().uri(geminiApiUrl + geminiApiKey).header("Content-Type", "application/json").bodyValue(requestBody).retrieve().bodyToMono(String.class).block();

    // Return extracted response

      return extractResponseContent(response);
  }

  private String extractResponseContent(String response){
      try{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response);
        return jsonNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

      }catch(Exception err){
        return "Error processing request: " +  err.getMessage();

    }
  }


  private String buildPrompt(EmailRequest emailRequest) {
    StringBuilder prompt = new StringBuilder();
    prompt.append(
      "Generate a professional email reply for the following email content. Please don't generate a subject line and reply to this email with only the message body. No subject, no unnecessary A.I explanations you can daa placeholders for variables in the text — just the raw reply text. "
    );

    if (
      emailRequest.getResponseTone() != null &&
      !emailRequest.getResponseTone().isEmpty()
    ) {
      prompt
        .append("Use a ")
        .append(emailRequest.getResponseTone())
        .append(" tone.");
    }

    prompt
      .append("\n Original Email: \n")
      .append(emailRequest.getEmailContent());

    return prompt.toString();
  }
}
