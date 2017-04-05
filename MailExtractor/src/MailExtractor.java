import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailExtractor {

	private String subject;
	private String folder;
	private boolean dest;
	private boolean filterDialoger;

	public MailExtractor(String subject, String folder, boolean dest,boolean filterDialoger) {
		this.subject =subject;
		this.folder = folder;
		this.filterDialoger = filterDialoger;
		this.dest = dest;

	}

	// pour extraire le champ replyTo et date de r�ception de tous les mails envoy� par lbc 
	public Set<String> extractFirstMailFromLbc(String adresseClient, String password, boolean google){
		System.out.println("Début de l'extraction ...");
		Set<String> retour = new HashSet<String>();
		// un �lement de retour est un string avec le mail de replyTo et aussi la date de r�ception
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		Session session = Session.getDefaultInstance(props, null);

		try{
			Store store = session.getStore("imaps");
			// IMAP host for yahoo
			if(google)
				store.connect("imap.gmail.com", adresseClient, password);
			else
				store.connect("imap.mail.yahoo.com", adresseClient, password);
			System.out.println("Connecté !");
			Folder inbox = store.getFolder(folder);
			inbox.open(Folder.READ_ONLY);
			Message msg[] = inbox.getMessages();
			for(Message message:msg) {
				String expediteur; 

				expediteur = message.getFrom()[0].toString();



				

				// filtre sur le sujet et l'exp�diteur
				Pattern subjectFilter = Pattern.compile(subject);
				Pattern expediteurFilter = Pattern.compile("no.reply@leboncoin.fr");

				// r�cup�ration sujet
				String subject = message.getSubject().toString();
				// r�cup�ration exp�
				if(subjectFilter.matcher(subject).find() & (expediteurFilter.matcher(expediteur).find() | !filterDialoger)){
					// pour r�cup�rer le mail de du champ replyTo
					Matcher mailsExtracted;
					Pattern mailsFilter = Pattern.compile(".*<(.+)>.*");
					String mailsToExtract;
					if(dest){
						mailsToExtract = message.getAllRecipients()[0].toString();
						System.out.println("in");
					}else{
						mailsToExtract = message.getReplyTo()[0].toString();
						
					}
					System.out.println(message.getSubject().toString());
					System.out.println("--- "+mailsToExtract+" ---");
					
					mailsExtracted = mailsFilter.matcher(mailsToExtract );


					// lancement de la recherche de toutes les occurrences
					if(mailsExtracted.matches()) {
						String mailProspectFromLbc = mailsExtracted.group(1);
						Date sentDate = message.getSentDate();
						SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyy hh:mm:ss");
						String extracted = mailProspectFromLbc+","+format.format(sentDate);
						System.out.println(extracted);
						retour.add(extracted);

					}	
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return retour;
	}
}
