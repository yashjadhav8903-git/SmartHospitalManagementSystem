//package com.example.HospitalManagement.EmailServices;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//
//@Service
//@RequiredArgsConstructor
//public class NormalEmailService {
//
//    private final JavaMailSender javaMailSender;
//
//    @Async
//    public void sendTestMail(String toEmail,String patientName,String appointmentTime){
//
//        // -->***NormalEmail
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage(); // Normal mail
//        simpleMailMessage.setFrom("LifeCarehospital@gmail.com");
//        simpleMailMessage.setTo(toEmail);
//        simpleMailMessage.setSubject("System Succesfull : Teating chal rahi hai 🤣");
//        simpleMailMessage.setText("Bhai," +patientName +" agar ye mail dikh rahi hai, toh setup successful hai! 🎉 or appointment pr aa ja samja " + appointmentTime);
//        javaMailSender.send(simpleMailMessage);
//        System.out.println("Mail send to Mailtrasp!");
//    }
//
//
//
//    // -->***HtmlEmail ( Save Patient )
//    @Async
//    public void sendHtmlEmail(String toEmail,String patientName){
//        try{
//
//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
//
//            mimeMessageHelper.setFrom("LifeCarehospital@gmail.com");
//            mimeMessageHelper.setTo(toEmail);
////            mimeMessageHelper.setSubject("Appointment Confirmed ! LifeCare Hospital");
////            String htmlContent = "<h1>Namaste " + patientName + "!</h1>" +
////                    "<p>Aapki appointment book ho gayi hai.</p>" +
////                    "<p style='color:green;'><b>Status: Confirmed ✅</b></p>";
//
//            // for Fun
//            mimeMessageHelper.setSubject(" System Succesfull : Teating chal rahi hai 🤣 ! Email Confirmed ! LifeCare Hospital");
//            String htmlContent = "<h1>Namaste " + patientName + "!</h1>" +
//                    "<p> Bhai , " + patientName + " agar ye mail dikh rahi hai, toh setup successful hai ! 🎉</p>" +
//                    "<p style='color:green;'><b>Status: Confirmed ✅</b></p>";
//
//            mimeMessageHelper.setText(htmlContent,true);
//            javaMailSender.send(mimeMessage);
//
//        }catch (MessagingException e){
//            System.out.println("HTML email me Error " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    // --> *** SendConfirmed Email ( if appointment is save than it Confirmed )
//    @Async
//    public void SendConfirmedEmail(String toEmail, String patientName, String doctorName, Integer AppointmentId, String time, String reason) throws MessagingException {
//
//        // Hospital Details (Ek jagah define kar do)
//        final String hospitalName = "LifeCare Hospital";
//        final String hospitalAddress = "Main Gate ,Pimpri, Pune - 411044";
//        final String contactNumber = "+91 96650 42716";
//        System.out.println("Email logic started for: " + toEmail);
//        try{
//
//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
//            mimeMessageHelper.setFrom("LifeCarehospital@gmail.com");
//            mimeMessageHelper.setTo(toEmail);
//            mimeMessageHelper.setSubject("Appointment Confirmed ! LifeCare Hospital");
//
//            // --- Safe Date Formatting ---
//            String displayTime = time;
//            try {
//                // Agar 'T' hai toh LocalDateTime use karo
//                if(time.contains("T") && !time.contains("+")) {
//                    java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(time);
//                    displayTime = ldt.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
//                }
//                // Agar offset wala format hai
//                else if(time.contains("+")) {
//                    java.time.OffsetDateTime odt = java.time.OffsetDateTime.parse(time);
//                    displayTime = odt.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
//                }
//            } catch (Exception e) {
//                System.out.println("Parsing failed, using raw time string");
//            }
//
//            String htmlContent =
//                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 12px; overflow: hidden;'>" +
//                    "  <div style='background-color: #2e7d32; padding: 25px; text-align: center;'>" +
//                    "    <h1 style='color: white; margin: 0;'>Appointment Confirmed!</h1>" +
//                    "  </div>" +
//                    "  <div style='padding: 30px; color: #333;'>" +
//                    "    <p style='font-size: 16px;'>Dear <strong>" + patientName + "</strong>,</p>" +
//                    "    <p>We are pleased to inform you that your appointment has been successfully confirmed.</p>" +
//                    "    <div style='background: #f1f8e9; padding: 20px; border-left: 6px solid #2e7d32; border-radius: 4px; margin: 25px 0;'>" +
//                    "      <p style='margin: 8px 0;'><strong>Appointment ID:</strong> #" + AppointmentId + "</p>" +
//                    "      <p style='margin: 8px 0;'><strong>Time:</strong> " + displayTime + "</p>" +
//                    "      <p style='margin: 8px 0;'><strong>Doctor:</strong> " + doctorName + "</p>" +
//                    "      <p style='margin: 8px 0;'><strong>Reason:</strong> " + reason + "</p>" +
//                    "      <p style='margin: 8px 0;'><strong>Status:</strong> <span style='color: #2e7d32; font-weight: bold;'>CONFIRMED ✅</span></p>" +
//                    "    </div>" +
//                            "<div style='margin-top: 30px; border-top: 1px solid #eee; padding-top: 15px; font-size: 13px; color: #777;'>" +
//                            "<p style='margin: 0;'><b>📍 Address:</b> " + hospitalAddress + "</p>" +
//                            "<p style='margin: 5px 0;'><b>📞 Contact:</b> " + contactNumber + "</p>" +
//                    "    <p>Please arrive 15 minutes before your scheduled time.</p>" +
//                    "    <br><p>Best Regards,<br><strong>LifeCare Hospital Team</strong></p>" +
//                    "  </div>" +
//                    "</div>";
//
//            mimeMessageHelper.setText(htmlContent,true);
//            javaMailSender.send(mimeMessage);
//            System.out.println("Email sent successfully to: " + toEmail);
//        } catch (MessagingException e){
//            System.out.println("Confirmed Message me koi dhikat.....🥺");
//            e.printStackTrace();
//        }
//    }
//
//
//    // --> *** SendPending Email ( if appointment is save than it Pending )
//    @Async
//    public void SendPendingEmail(String toEmail,String reason,Integer appointmentId,String patientName){
//        try{
//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
//
//            mimeMessageHelper.setFrom("LifeCarehospital@gmail.com");
//            mimeMessageHelper.setTo(toEmail);
//            mimeMessageHelper.setSubject("Appointment Received (Pending) ! LifeCare Hospital");
//            String htmlContent =
//                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 12px; overflow: hidden;'>" +
//                            "  <div style='background-color: #ef6c00; padding: 25px; text-align: center;'>" +
//                            "    <h1 style='color: white; margin: 0;'>Appointment Pending</h1>" +
//                            "  </div>" +
//                            "  <div style='padding: 30px; color: #333;'>" +
//                            "    <p style='font-size: 16px;'>Dear <strong>" + patientName + "</strong>,</p>" +
//                            "    <p>Your appointment request has been received. Currently, the status is <strong>Pending</strong> as the doctor is busy during the requested time.</p>" +
//                            "    <div style='background: #fff3e0; padding: 20px; border-left: 6px solid #ef6c00; border-radius: 4px; margin: 25px 0;'>" +
//                            "      <p style='margin: 8px 0;'><strong>Appointment ID:</strong> #" + appointmentId + "</p>" +
//                            "      <p style='margin: 8px 0;'><strong>Reason:</strong> " + reason + "</p>" +
//                            "      <p style='margin: 8px 0;'><strong>Status:</strong> <span style='color: #ef6c00; font-weight: bold;'>PENDING ⏳</span></p>" +
//                            "    </div>" +
//                            "    <p>We will notify you once the doctor confirms your slot.</p>" +
//                            "    <br><p>Best Regards,<br><strong>LifeCare Hospital Team</strong></p>" +
//                            "  </div>" +
//                            "</div>";
//            mimeMessageHelper.setText(htmlContent,true);
//            javaMailSender.send(mimeMessage);
//        } catch (MessagingException e){
//            System.out.println("Pending Message me koi dhikat.....🥺");
//            e.printStackTrace();
//        }
//    }
//}
