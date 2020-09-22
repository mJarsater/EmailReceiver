import com.sun.mail.pop3.POP3Store;

import javax.mail.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Properties;

public class EmailReceiver extends JFrame {
    private JTextArea textArea;
    private JButton refreshBtn;


    public EmailReceiver(){
        this.setTitle("Email Recevier");
        textArea = new JTextArea();
        refreshBtn = new JButton("Refresh");



        refreshBtn.setBounds(100, 20, 100, 30);
        textArea.setBounds(100,100,400,400);
        textArea.setEditable(false);

        this.add(textArea);
        this.add(refreshBtn);
        refreshBtn.addActionListener(this:: refreshAction);

        this.setSize(600,600);
        this.setLayout(null);
        this.setVisible(true);
    }

    public void refreshAction(ActionEvent e){
        Email email = new Email("", "");
        email.run();
    }



    public static void main(String[] args) {
        EmailReceiver emailReceiver = new EmailReceiver();
    }
}

class Email{
    private Properties properties;
    private String username, password;

    public Email(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void run() {
        try {
            properties = new Properties();
            properties.put("mail.smtp.host", "smtp-mail.outlook.com");
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username,password);
                }
            });

            Store emailStore =  session.getStore("imaps");

            emailStore.connect("smtp-mail.outlook.com",username, password);

            Folder emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
            //TODO - Sätt max antal
            Message[] messages = emailFolder.getMessages() ;


            for(Message message : messages){

                //TODO - Formatera utskrift
                System.out.println(message.getSubject());



            }
        } catch (Exception e){
            System.out.println(e);

        }

    }
}

