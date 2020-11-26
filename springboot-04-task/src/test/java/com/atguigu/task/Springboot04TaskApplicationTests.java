package com.atguigu.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Springboot04TaskApplicationTests {

	@Autowired
	JavaMailSenderImpl mailSender;

	@Test
	public void contextLoads() {
		SimpleMailMessage message = new SimpleMailMessage();
		//邮件设置
		message.setSubject("通知-今晚开会");
		message.setText("今晚7:30开会");

		/*message.setTo("291373489@qq.com");
		message.setFrom("291373489@qq.com");*/
		message.setTo("gudong1998@outlook.com");
		message.setFrom("maggie.xu@cyberwisdom.net");

		mailSender.send(message);
	}

	@Test
	public void test02() throws  Exception{
		//1、创建一个复杂的消息邮件
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		//邮件设置
		helper.setSubject("通知-今晚开会");
		helper.setText("<b style='color:red'>今天 7:30 开会</b>",true);

		helper.setTo("291373489@qq.com");
		helper.setFrom("291373489@qq.com");

		//上传文件
		helper.addAttachment("1.jpg",new File("D:\\1.jpg"));
		helper.addAttachment("2.jpg",new File("D:\\2.jpg"));

		mailSender.send(mimeMessage);

	}
	@Test
	public void test3(){
		 	try {
				Properties props = new Properties();
				//发件人
			//	String fromEmail = props.getProperty("fromEmail", "291373489@qq.com");
				String fromEmail = props.getProperty("fromEmail", "maggie.xu@cyberwisdom.net");
				//收件人
				String toEmail = props.getProperty("toEmail", "gudong1998@outlook.com");
				props.put("mail.smtp.port", "25");
				props.put("mail.smtp.host", "mail.cyberwisdom.net");
				props.put("mail.smtp.ssl.trust", "mail.cyberwisdom.net");
				props.put("mail.transport.protocol", "smtp");
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.ssl", "true");
				//开启debug调试，控制台会打印相关信息
//            props.put("mail.debug", "true");
				Authenticator authenticator = new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						//发件人邮箱账号
						String userId = props.getProperty("userId", "maggie.xu@cyberwisdom.net");
						//发件人邮箱密码(qq、163等邮箱用的是授权码,outlook是密码)
						String password = props.getProperty("password", "cyber9295");
						return new PasswordAuthentication(userId, password);
					}
				};
				Session session = Session.getInstance(props, authenticator);
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(fromEmail));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
				//标题
				message.setSubject("课程:1天");
				StringBuffer buffer = new StringBuffer();

				buffer.append("BEGIN:VCALENDAR\n"
						+ "PRODID:-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN\n"
						+ "VERSION:2.0\n"
						//+ "METHOD:REQUEST\n"   //请求事件
					 + "METHOD:CANCEL\n" //取消事件
						+ "BEGIN:VEVENT\n"
						//参会者
						+ "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO: gudong1998@outlook.com" +" \n"
						//组织者
						//+ "ORGANIZER:MAILTO:张三\n"
						+ "DTSTAMP:" + getUtc(new Date().getTime()) + "\n"
						+ "DTSTART:" + getUtc(new Date().getTime()) + "\n"
						+ "DTEND:" + getUtc(new Date().getTime()) + "\n"
					//	+ "RRULE:FREQ=DAILY;COUNT=10" + "\n"
						//面试地点
						+ "LOCATION:MARS"  + "\n"
						//如果id相同的话，outlook会认为是同一个会议请求，所以使用uuid。
						//+ "UID:" + UUID.randomUUID().toString() + "\n"
						+ "UID:20201104" + "\n"
						+ "CATEGORIES:\n"
						//会议描述
						//+ "DESCRIPTION:Stay Hungry.<br>Stay Foolish.\n\n"
						+ "SUMMARY:课程日程表66\n" + "PRIORITY:5\n"
						//该属性定义了vCalendar实体被修改的次数
						+ "SEQUENCE:" + 4 + "\n"
						+ "CLASS:PUBLIC\n" + "BEGIN:VALARM\n"
						//提前10分钟提醒M分H小时
						+ "TRIGGER:-PT10H\n" + "ACTION:DISPLAY\n"
						+ "DESCRIPTION:Reminder\n" + "END:VALARM\n"
						+ "END:VEVENT\n" + "END:VCALENDAR");

				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(buffer.toString(),
						"text/calendar;method=CANCEL;charset=\"UTF-8\"")));
				MimeMultipart multipart = new MimeMultipart();
				MimeBodyPart mimeBodyPart = new MimeBodyPart();
				//html类型正文
				//mimeBodyPart.setContent(emailText,"text/html;charset=UTF-8");
				SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String start = s.format(new Date());
				String end = s.format(new Date());
				String text = "内部课程:66666666" + "\n" +
						"\n" +
						"日程开始时间：" + start + "\n" +
						"\n" +
						"日程结束时间：" + end + " ";
				//文本类型正文
				mimeBodyPart.setText(text);
				//添加正文
				multipart.addBodyPart(mimeBodyPart);
				//添加日历
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);
				message.setSentDate(new Date());
				message.saveChanges();
			 	Transport.send(message);
			//	mailSender.send(message);
			}catch (Exception e){
				e.printStackTrace();
			}
		}

	/**
	 * 转utc时间
	 * @return
	 */
	private static String getUtc(long millionSeconds) {
		millionSeconds = millionSeconds - (8*60*60*1000);
		Date date = new Date(millionSeconds);
		//格式化日期
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = "";
		nowTime = df.format(date);
		//转换utc时间
		String utcTime = nowTime.replace("-", "").replace(" ", "T").replace(":", "");
		return utcTime;
	}


}
