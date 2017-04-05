import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;

public class Main {

	public static void main(String[] args) {
		// r�cup�ration des donn�es de connexion � la boite mail
		Scanner sc = new Scanner(System.in);
		System.out.println("Entrez l'adresse mail du compte");
		String compte = sc.nextLine();
		System.out.println("Entrez le mot de passe ");
		String password = sc.nextLine();
		System.out.println("Entrez le sujet des mails à extraire "
				+ "\n (exemple : Renouvelez votre annonce ou a répondu à votre annonce");
		String subject = sc.nextLine();
		System.out.println("Entrez le dossier dans lequel faire l'extraction"
				+ "\n (exemple : Inbox ou leboncoin)");
		String folder = sc.nextLine();
		System.out.println("Extraire destinataire(true) ou répondre à(false)");
		Boolean dest = Boolean.parseBoolean(sc.nextLine());
		System.out.println("Filter selon le dialoger (true ou false)? ");
		Boolean filterDialoger = Boolean.parseBoolean(sc.nextLine());

		// extraction de la boite mail
		MailExtractor mailExtractor = new MailExtractor(subject,folder, dest,filterDialoger);
		Set<String> mailsFromLbc = mailExtractor.extractFirstMailFromLbc(compte, password,true);
		System.out.println(mailsFromLbc.size());
		// �criture des mails extraits dans un csv
		Date today = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		try(FileWriter fw = new FileWriter("mailextracted-"+compte.split("@")[0]+"-"+format.format(today)+".csv")){
				for(String mail : mailsFromLbc){
					System.out.println(mail+" a été extrait");
					fw.write(mail+'\n');
				}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
