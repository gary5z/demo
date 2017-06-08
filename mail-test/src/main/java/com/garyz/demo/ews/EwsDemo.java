/**
 * 
 */
package com.garyz.demo.ews;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;

import com.garyz.util.HttpUtils;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.LogicalOperator;
import microsoft.exchange.webservices.data.core.enumeration.search.SortDirection;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.EmailAddressCollection;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.ItemAttachment;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

/**
 * 通过ews收取exchange收件箱的邮件，并保存到本地eml格式邮件文件
 * 
 * @author zengzhiqiang
 * @version 2017年6月5日
 *
 */
public class EwsDemo {

	public static void main(String[] args) throws Exception {

		AccessInfo info = new AccessInfo();
		info.setEmail("username@capol.cn");
		info.setDomain("CAPOL.CN");
		info.setUserName("username");
		info.setPassword("password");
		info.setServerUrl("https://mail.capol.cn/ews/exchange.asmx");

		readMail(info);
	}

	public static void readMail(AccessInfo info) throws Exception {
		// ExchangeService版本为2010
		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010);

		// 参数是用户名,密码,域
		ExchangeCredentials credentials = new WebCredentials(info.getUserName(), info.getPassword(), info.getDomain());
		service.setCredentials(credentials);

		// 给出Exchange Server的URL http://xxxxxxx
		URI uri = new URI(info.getServerUrl());
		service.setUrl(uri);

		// 你自己的邮件地址 xxx@xxx.xxx
		// service.autodiscoverUrl(info.getEmail());

		// 创建过滤器, 条件为邮件未读.
		// SearchFilter sf = new
		// SearchFilter.IsEqualTo(EmailMessageSchema.IsRead, false);
		SearchFilter sf = new SearchFilter.SearchFilterCollection(LogicalOperator.Or,
				new SearchFilter.ContainsSubstring(ItemSchema.Subject, "松山湖项目退回公司资产清单"),
				new SearchFilter.ContainsSubstring(ItemSchema.Subject, "应聘"));
		// 查找Inbox,加入过滤器条件,结果10条
		FindItemsResults<Item> findResults = null;
		try {
			ItemView view = new ItemView(10, 0);
			view.getOrderBy().add(ItemSchema.DateTimeCreated, SortDirection.Descending);
			findResults = service.findItems(WellKnownFolderName.Inbox, sf, view);
			for (Item item : findResults.getItems()) {
				EmailMessage email = EmailMessage.bind(service, item.getId());
				System.out.println(email.getSubject());

				if (email.getHasAttachments()) {
					saveMultiEmail(email);
				} else {
					saveSimpleEmail(email);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void saveSimpleEmail(EmailMessage email) throws Exception {

		MimeMessage msg = createMimeMessage(email);

		String body = email.getBody().toString();
		if (BodyType.HTML.equals(email.getBody().getBodyType())) {
			// 设置HTML格式的邮件正文
			msg.setContent(body, "text/html;charset=utf-8");
		} else {
			// 设置纯文本内容的邮件正文
			msg.setText(body);
		}

		// 保存并生成最终的邮件内容
		msg.saveChanges();
		// 把MimeMessage对象中的内容写入到文件中
		msg.writeTo(new FileOutputStream("D:\\simple_email.eml"));
	}

	public static void saveMultiEmail(EmailMessage email) throws Exception {
		MimeMessage msg = createMimeMessage(email);

		try {
			// 可以装载多个主体部件！可以把它当成是一个集合
			MimeMultipart partList = new MimeMultipart();
			msg.setContent(partList);// 把邮件的内容设置为多部件的集合对象
			// 创建一个部件
			MimeBodyPart part1 = new MimeBodyPart();
			// 给部件指定内容

			String body = email.getBody().toString();
			if (BodyType.HTML.equals(email.getBody().getBodyType())) {
				// 设置HTML格式的邮件正文
				part1.setContent(body, "text/html;charset=utf-8");
			} else {
				// 设置纯文本内容的邮件正文
				part1.setText(body);
			}

			// 部件添加到集合中
			partList.addBodyPart(part1);

			for (Attachment attachment : email.getAttachments()) {
				// 又创建一个部件
				MimeBodyPart part2 = new MimeBodyPart();

				attachment.load();
				if (attachment instanceof FileAttachment) {
					FileAttachment fileAttachment = (FileAttachment) attachment;
					byte[] content = fileAttachment.getContent();
					String contentType = attachment.getContentType();
					if (contentType == null) {
						contentType = HttpUtils.getContentType(attachment.getName());
					}
					DataSource source = new ByteArrayDataSource(content, contentType);
					part2.setDataHandler(new DataHandler(source));
					part2.setContentID(attachment.getContentId());
				} else {
					ItemAttachment itemAttachment = (ItemAttachment) attachment;
					System.out.println(itemAttachment.getContentId());
				}

				// 指定附件文件的名字
				// 使用MimeUtility.encodeText()对中文进行编码
				part2.setFileName(MimeUtility.encodeText(attachment.getName()));

				partList.addBodyPart(part2);
			}

			// 保存并生成最终的邮件内容
			msg.saveChanges();
			// 把MimeMessage对象中的内容写入到文件中
			msg.writeTo(new FileOutputStream("D:\\multi_email.eml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static MimeMessage createMimeMessage(EmailMessage email) throws Exception {

		InternetAddress from = new InternetAddress(email.getFrom().getAddress());
		if (StringUtils.isNotEmpty(email.getFrom().getName())) {
			from.setPersonal(MimeUtility.encodeText(email.getFrom().getName()));
		}

		EmailAddressCollection toRecipients = email.getToRecipients();
		InternetAddress[] to = new InternetAddress[toRecipients.getCount()];
		for (int i = 0; i < toRecipients.getCount(); i++) {
			EmailAddress address = toRecipients.getItems().get(i);
			to[i] = new InternetAddress(address.getAddress());
			if (StringUtils.isNotEmpty(address.getName())) {
				to[i].setPersonal(MimeUtility.encodeText(address.getName()));
			}
		}

		EmailAddressCollection ccRecipients = email.getCcRecipients();
		InternetAddress[] cc = new InternetAddress[ccRecipients.getCount()];
		for (int i = 0; i < ccRecipients.getCount(); i++) {
			EmailAddress address = ccRecipients.getItems().get(i);
			cc[i] = new InternetAddress(address.getAddress());
			if (StringUtils.isNotEmpty(address.getName())) {
				cc[i].setPersonal(MimeUtility.encodeText(address.getName()));
			}
		}

		EmailAddressCollection bccRecipients = email.getBccRecipients();
		InternetAddress[] bcc = new InternetAddress[bccRecipients.getCount()];
		for (int i = 0; i < bccRecipients.getCount(); i++) {
			EmailAddress address = bccRecipients.getItems().get(i);
			bcc[i] = new InternetAddress(address.getAddress());
			if (StringUtils.isNotEmpty(address.getName())) {
				bcc[i].setPersonal(MimeUtility.encodeText(address.getName()));
			}
		}

		String subject = email.getSubject();

		// 创建Session实例对象
		Session session = Session.getDefaultInstance(new Properties());
		// 创建MimeMessage实例对象
		MimeMessage msg = new MimeMessage(session);
		// 设置发件人
		msg.setFrom(from);
		// 设置收件人
		msg.setRecipients(Message.RecipientType.TO, to);
		msg.setRecipients(Message.RecipientType.CC, cc);
		msg.setRecipients(Message.RecipientType.BCC, bcc);
		// 设置发送日期
		msg.setSentDate(email.getDateTimeSent());
		// 设置邮件主题
		msg.setSubject(subject);

		return msg;
	}

	public static void sendEmail(String[] args) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		Session session = Session.getInstance(props);
		// 现有邮件文件
		File file = new File("C:\\textmail.eml");
		FileInputStream fis = new FileInputStream(file);
		// 创建邮件对象
		Message message = new MimeMessage(session, fis);
		message.setSentDate(new Date());
		message.saveChanges();
		// 发送邮件
		Transport transport = session.getTransport("smtp");
		transport.connect("smtp.163.com", 25, "test20120711120200", "test123456");
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		fis.close();
		System.out.println("发送完毕");
	}
}
