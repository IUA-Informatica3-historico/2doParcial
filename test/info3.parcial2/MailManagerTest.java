package info3.parcial2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MailManagerTest {
    private MailManager mm;

    @BeforeEach
    void setUp() {
        mm = new MailManager();
    }

    @Test
    void addMail() {
        ArrayList<Email> mails = loadMails();
        for (Email m : mails) {
            mm.addMail(m);
        }

        assertEquals("2014-02-28 16:22", mails.get(0).date, "No se cargaron los mails");
    }


    ArrayList<Email> loadMails() {
        String line = null;
        String[] campos = null;
        Email mail = null;
        ArrayList<Email> ret = new ArrayList<>();

        try {
            FileReader in = new FileReader("mails2.txt");
            BufferedReader buf = new BufferedReader(in);

            while ((line = buf.readLine()) != null) {
                // proceso estructura del archivo
                campos = line.split(":",2);
                //System.out.println(line);

                if (campos.length > 0)
                    switch (campos[0]) {
                        case "-.-.-":
                            if (mail != null)
                                ret.add(mail);

                            mail = new Email();
                            mail.content = "";
                            break;
                        case "date":
                            mail.date = campos[1].trim();
                            break;
                        case "from":
                            mail.from = campos[1].trim();
                            break;
                        case "to":
                            mail.to = campos[1].trim();
                            break;
                        case "subject":
                            mail.subject = campos[1].trim();
                            break;
                        default:
                            mail.content += line + "\n";
                            break;
                    }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}