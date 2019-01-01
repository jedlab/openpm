package com.jedlab.framework.mail;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class MailClient
{

    private JavaMailSender mailSender;
    private VelocityEngine velocityEngine;
    public static final String TEMPLATE = "com/core/mail/template.vm";

    public void setVelocityEngine(VelocityEngine velocityEngine)
    {
        this.velocityEngine = velocityEngine;
    }

    public void setMailSender(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    /**
     * sends a message based on a velocity template
     * 
     * @param from
     * @param to
     * @param subject
     * @param templateLocation
     * @param model
     */
    public void send(String from, String to, String subject, String templateLocation, Map<String, Object> model, UncaughtExceptionHandler uc, String... cc)
    {
        MessagePreparator mp = new MessagePreparator(from, to, subject, templateLocation, model, cc);
        Thread thread = new Thread(new MessageSender(mp));
        if (uc != null)
            thread.setUncaughtExceptionHandler(uc);
        thread.start();
    }

    public void send(String from, String to, String subject, String templateLocation, Map<String, Object> model, String... cc)
    {
        MessagePreparator mp = new MessagePreparator(from, to, subject, templateLocation, model, cc);
        try
        {
            mailSender.send(mp);
        }
        catch (MailException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new MailPreparationException(ex);
        }
    }

    /**
     * sends an email with attachement and template
     * 
     * @param from
     * @param to
     * @param subject
     * @param templateLocation
     * @param model
     * @param attachmentPath
     * @param attachmentName
     */
    public void send(String from, String to, String subject, String templateLocation, Map<String, Object> model, String attachmentPath,
            String attachmentName, UncaughtExceptionHandler uc, String...cc )
    {
        MessagePreparator mp = new MessagePreparator(from, to, subject, templateLocation, model, attachmentPath, attachmentName, cc);        
        Thread thread = new Thread(new MessageSender(mp));
        if (uc != null)
            thread.setUncaughtExceptionHandler(uc);
        thread.start();
    }
    
    public void send(String from, String to, String subject, String templateLocation, Map<String, Object> model, String attachmentPath,
            String attachmentName, String[] cc )
    {
        MessagePreparator mp = new MessagePreparator(from, to, subject, templateLocation, model, attachmentPath, attachmentName, cc);
        try
        {
            mailSender.send(mp);
        }
        catch (MailException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new MailPreparationException(ex);
        }        
    }

    /**
     * send a simple text message
     * 
     * @param from
     * @param to
     * @param subject
     * @param msg
     */
    public void send(String from, String to, String subject, String msg, String... cc)
    {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(msg);
        if(cc.length > 0)
            message.setCc(cc);
        mailSender.send(message);
    }

    private class MessageSender implements Runnable
    {

        private MessagePreparator mp;

        public MessageSender(MessagePreparator mp)
        {
            this.mp = mp;
        }

        public void run()
        {
            mailSender.send(mp);
        }

    }

    private class MessagePreparator implements MimeMessagePreparator
    {

        private String from;
        private String to;
        private String subject;
        private String templateLocation;
        private Map<String, Object> model;
        private String attachmentPath;
        private String attachmentName;

        private String[] ccList;

        public MessagePreparator(String from, String to, String subject, String templateLocation, Map<String, Object> model, String... ccList)
        {
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.templateLocation = templateLocation;
            this.model = model;
            this.ccList = ccList;
        }

        public MessagePreparator(String from, String to, String subject, String templateLocation, Map<String, Object> model,
                String attachmentPath, String attachmentName, String... ccList)
        {
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.templateLocation = templateLocation;
            this.model = model;
            this.attachmentPath = attachmentPath;
            this.attachmentName = attachmentName;
            this.ccList = ccList;
        }

        public void prepare(MimeMessage mimeMessage) throws Exception
        {
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, "UTF-8", model);
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text, true);
            if(ccList.length > 0)
                message.setCc(ccList);
            if (attachmentPath != null  && attachmentName.trim().length() > 0)
            {
                FileSystemResource file = new FileSystemResource(attachmentPath);
                message.addAttachment(attachmentName, file);
            }
        }

    }
    
    
   

}
