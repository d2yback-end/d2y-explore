package com.d2y.d2yapiofficial.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.d2y.d2yapiofficial.models.NotificationEmail;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;
  private final ObjectMapper objectMapper;

  @Value("${spring.mail.sender}")
  private String emailSenderAddress;

  @Async
  @KafkaListener(topics = "email-topic", groupId = "d2y-group")
  void sendMail(String jsonMailMessage) throws JsonProcessingException {
    NotificationEmail notificationEmail = objectMapper.readValue(jsonMailMessage, NotificationEmail.class);
    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setFrom(emailSenderAddress, "D2Y OFFICIAL");
      messageHelper.setTo(notificationEmail.getRecipient());
      messageHelper.setSubject(notificationEmail.getSubject());

      String emailContent = generateEmailContent(notificationEmail);
      messageHelper.setText(emailContent, true);
    };
    try {
      mailSender.send(messagePreparator);
      log.info(ConstantMessage.EMAIL_NOTIF_SENT);
    } catch (MailException ex) {
      throw new MailSendException(ConstantMessage.EXCEPTION_EMAIL_SENT + notificationEmail.getRecipient(), ex);
    }
  }

  private String generateEmailContent(NotificationEmail notificationEmail) {
    Context context = new Context();
    context.setVariable("recipient", notificationEmail.getRecipient());
    context.setVariable("username", notificationEmail.getUsername());
    context.setVariable("verificationUrl", notificationEmail.getVerificationUrl());

    return templateEngine.process("notification-email", context);
  }
}
