import org.apache.commons.lang3.ArrayUtils;

import javax.mail.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Properties;


public class EmailReceiver extends JFrame {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel usernameLabel, passwordLabel, unreadLabel,totalCountLabel;
    private JButton refreshBtn;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private String username, password;


    //Konstruktor för klassen EmailReceiver - GUI
    public EmailReceiver(){
        this.setTitle("Email Recevier");
        textArea = new JTextArea();
        refreshBtn = new JButton("Refresh");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        usernameLabel = new JLabel("Username");
        passwordLabel = new JLabel("Password");
        unreadLabel = new JLabel("Unread messeges: -");
        totalCountLabel = new JLabel("Total amount of emails: -");

        usernameLabel.setBounds(10,10,70,20);
        usernameField.setBounds(100, 10, 100, 20);
        passwordLabel.setBounds(10,40,70,20);
        passwordField.setBounds(100, 40, 100, 20);
        unreadLabel.setBounds(10, 70,200,20);
        totalCountLabel.setBounds(200, 70,200,20);
        refreshBtn.setBounds(300, 20, 100, 30);


        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(570,450));
        scrollPane.setBounds(5,100,570,450);
        this.add(usernameLabel);
        this.add(usernameField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.add(scrollPane);
        this.add(refreshBtn);
        this.add(unreadLabel);
        this.add(totalCountLabel);
        refreshBtn.addActionListener(this:: refreshAction);

        this.setSize(600,600);
        this.setLayout(null);
        this.setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    // Metod som tömmer textArean på text
    public void clearTextArea(){
        textArea.setText("");
    }

    // Metod som returnerar textarean
    public JTextArea getTextArea(){
        return textArea;
    }

    // Metod som returnerar unreadLabel
    public JLabel getUnreadLabel(){
        return unreadLabel;
    }
    // Metod som returnerar totalCountLabel
    public JLabel getTotalCountLabel(){
        return totalCountLabel;
    }


    /* Metod som skapar en scroll för textarean
    * samt häntar texten i username- och passwordfälten.
    * Skapar sen ett nytt object av klassen EmailInbox
    * */
    public void refreshAction(ActionEvent e){

        JScrollBar scroll = scrollPane.getVerticalScrollBar();
        scroll.setValue(scroll.getMaximum());
        username = usernameField.getText();
        password = new String(passwordField.getPassword());
        clearTextArea();
        EmailInbox email = new EmailInbox(username, password, this);
        email.getInbox();
    }

    // ------------- MAIN ----------------------------
    public static void main(String[] args) {
        EmailReceiver emailReceiver = new EmailReceiver();
    }
    // ------------- MAIN END ------------------------
}

class EmailInbox{
    private JTextArea textArea;
    private JLabel unreadLabel, totalCountLabel;
    private Properties properties;
    private String username, password;
    private int unreadCount,totalCount;

    public EmailInbox(String username, String password, EmailReceiver emailReceiver){
        this.username = username;
        this.password = password;
        this.textArea = emailReceiver.getTextArea();
        this.unreadLabel = emailReceiver.getUnreadLabel();
        this.totalCountLabel = emailReceiver.getTotalCountLabel();
    }

    public void clearTextArea(){
        textArea.setText(null);
    }



    public void print(Message email){
        try {
            textArea.append("\n");
            textArea.append("---------------------------------------\n");
            textArea.append("From: " + email.getFrom()[0]+"\n");
            textArea.append("Subject: "+checkSubject((email.getSubject())+"\n"));
            textArea.append("Date"+email.getSentDate()+"\n");
            textArea.append("---------------------------------------");
            textArea.append("\n");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String checkSubject(String subject){
        if(subject == null){
            return "No subject";
        } else {
            return subject;
        }

    }

    public void print(String msg){
        textArea.append(msg +"\n");
    }

    public void setLabels(int unreadCount, int totalCount){
        unreadLabel.setText("Unread messages: "+unreadCount);
        totalCountLabel.setText("Total amount of messages: "+totalCount);
    }



    public void getInbox() {
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

            unreadCount = emailFolder.getUnreadMessageCount();
            totalCount = emailFolder.getMessageCount();
            setLabels(unreadCount, totalCount);


            emailFolder.open(Folder.READ_ONLY);
            Message[] messages = emailFolder.getMessages() ;
            ArrayUtils.reverse(messages);
            clearTextArea();
            for(int i = 0; i < messages.length; i++ ){
                if(i >= 10){
                    emailFolder.close();
                    emailStore.close();
                    break;
                }
                print(messages[i]);
            }

        } catch (AuthenticationFailedException e){
            System.out.println(e);
            print("Authentication failed.");
            print("Wrong email or password.");
        } catch (MessagingException me){
            System.out.println(me);
        }

    }
}

