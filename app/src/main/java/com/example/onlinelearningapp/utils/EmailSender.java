package com.example.onlinelearningapp.utils;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    // !!! CẢNH BÁO BẢO MẬT: Không nên nhúng trực tiếp USERNAME và PASSWORD vào ứng dụng Android.
    // Đây là một rủi ro bảo mật lớn.
    // Giải pháp an toàn hơn là sử dụng một máy chủ backend để gửi email.
    private static final String SENDER_EMAIL = "your_email@example.com"; // Thay thế bằng email của bạn
    private static final String SENDER_PASSWORD = "your_email_password"; // Thay thế bằng mật khẩu email của bạn (hoặc App Password nếu dùng Gmail)

    public interface EmailSendListener {
        void onEmailSent(boolean success, String message);
    }

    public static void sendResetPasswordEmail(String recipientEmail, String newPassword, EmailSendListener listener) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Ví dụ cho Gmail. Thay đổi nếu dùng nhà cung cấp khác.
        props.put("mail.smtp.port", "587"); // TLS port
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Enable TLS

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        new Thread(() -> {
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
                message.setSubject("Online Learning App - Password Reset");
                message.setText("Dear user,\n\nYour new password is: " + newPassword + "\n\nPlease login with this new password and consider changing it to something more memorable.\n\nThank you,\nOnline Learning App Team");

                Transport.send(message);
                if (listener != null) {
                    listener.onEmailSent(true, "Password reset email sent successfully to " + recipientEmail);
                }
            } catch (MessagingException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onEmailSent(false, "Failed to send reset email: " + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onEmailSent(false, "An unexpected error occurred: " + e.getMessage());
                }
            }
        }).start(); // Run on a new thread
    }
}

