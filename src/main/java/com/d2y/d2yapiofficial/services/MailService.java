package com.d2y.d2yapiofficial.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.sender}")
  private String emailSenderAddress;

  @Async
  void sendMail(NotificationEmail notificationEmail) {
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
