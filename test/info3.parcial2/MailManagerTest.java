package info3.parcial2;

import javafx.util.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MailManagerTest {
    static private MailManager mm;
    static private ArrayList<Email> mails;
    private Email[] tmp1, tmp2;
    private long stopTime;
    private long startTime;


    @BeforeAll
    static void load() {
        mails = loadMails();
    }

    @BeforeEach
    void setUp() {
        mm = new MailManager();
        for (Email m : mails) {
            mm.addMail(m);
        }
    }

    @AfterEach
    void showBenchmark() {
        float time = (stopTime - startTime);
        time /= 1000000;
        System.out.println("Tardó: " + time + "ms");
    }

    @Test
    void pruebaAgregado() {
        startTime = System.nanoTime();
        mm = new MailManager();
        for (Email m : mails) {
            mm.addMail(m);
        }
        stopTime = System.nanoTime();
    }

    @Test
    void pruebaMostrarPorFecha() {
        startTime = System.nanoTime();
        tmp1 = mm.getSortedByDate();
        stopTime = System.nanoTime();


        assertEquals(6517, tmp1.length, "No devolvió todos los mails esperados");
        if (tmp1.length == 6517)
            assertEquals("2017-10-27 17:50", tmp1[6516].date, "No está el mail mas nuevo");
        if (tmp1.length > 0)
            assertEquals("2010-02-01 14:46", tmp1[0].date, "No está el mail mas viejo");
    }

    @Test
    void pruebaMostrarPorRemitente() {
        startTime = System.nanoTime();
        tmp1 = mm.getSortedByFrom();
        stopTime = System.nanoTime();

        assertEquals(6517, tmp1.length, "No devolvió todos los mails esperados");
        if (tmp1.length == 6517)
            assertEquals("yomatiascalde---@gmail.com", tmp1[6516].from, "No está el ultimo remitente");
        if (tmp1.length > 0)
            assertEquals("---@criterionet.com", tmp1[0].from, "No está el primer remitente");
    }

    @Test
    void pruebaMostrarPorRangoFecha() {
        startTime = System.nanoTime();
        tmp1 = mm.getSortedByDate("2011-07-25 17:10", "2013-09-24 00:10");
        stopTime = System.nanoTime();

        assertTrue(tmp1.length > 5, "No devolvió todos los mails esperados");

        assertEquals("2011-07-25 17:21", tmp1[0].from, "No está el primer mail del rango");
        assertEquals("2013-09-24 00:09", tmp1[tmp1.length - 1].from, "No está el ultimo mail del rango");

    }

    @Test
    void pruebaBuscarPorRemitente() {
        startTime = System.nanoTime();
        tmp1 = mm.getByFrom("alam---@gmail.com");
        stopTime = System.nanoTime();
        assertEquals(30, tmp1.length, "Mal la cantidad de mails de alam---@gmail.com");

        tmp1 = mm.getByFrom("ramonreta---@gmail.com");
        assertEquals(38, tmp1.length, "Mal la cantidad de mails de ramonreta---@gmail.com");

        tmp1 = mm.getByFrom("lru---@inti.gob.ar");
        assertEquals(49, tmp1.length, "Mal la cantidad de mails de lru---@inti.gob.ar");

        tmp1 = mm.getByFrom("franco---@gmail.com");
        assertEquals(65, tmp1.length, "Mal la cantidad de mails de franco---@gmail.com");

        tmp1 = mm.getByFrom("sebas---@gmail.com");
        assertEquals(355, tmp1.length, "Mal la cantidad de mails de sebas---@gmail.com");

    }

    @Test
    void pruebaByQuery() {
        ArrayList<Pair<String, Integer>> palabras = new ArrayList<Pair<String, Integer>>();
        palabras.add(new Pair<>("Chau", 19));
        palabras.add(new Pair<>("compilador", 7));
        palabras.add(new Pair<>("java", 90));
        palabras.add(new Pair<>("linux", 1553));
        palabras.add(new Pair<>("debian", 493));
        palabras.add(new Pair<>("ubuntu", 1380));
        palabras.add(new Pair<>("kde", 43));

        startTime = System.nanoTime();
        tmp1 = mm.getByQuery("Hola");
        stopTime = System.nanoTime();

        System.out.println("Hola: " + tmp1.length); // 1794
        assertEquals(1794, tmp1.length, "Cantidad distinta de resultados para Hola");

        for (Pair<String, Integer> p : palabras) {
            tmp1 = mm.getByQuery(p.getKey());
            System.out.println(p.getKey() + ": " + tmp1.length);
            assertEquals(p.getValue().intValue(), tmp1.length, "Cantidad distinta de resultados para " + p.getKey());
        }
    }

    @Test
    void pruebaBorrar() {
        tmp1 = mm.getByQuery("Hola");

        startTime = System.nanoTime();
        assertEquals(1794 , tmp1.length, "Cantidad distinta de resultados para Hola");

        for (int i = 0; i < 300; i++) {
            mm.deleteMail(tmp1[i].id);
        }
        stopTime = System.nanoTime();

        tmp1 = mm.getByQuery("Hola");
        System.out.println("Borrados de Hola: " + tmp1.length); // 1794
        assertEquals(1794 - 300, tmp1.length, "No se borraron todos los mails con Hola");
    }

    static ArrayList<Email> loadMails() {
        String line = null;
        String fechaMax = "0", fechaMin = "9";
        String[] campos = null;
        Email mail = null;
        ArrayList<Email> ret = new ArrayList<>();

        try {
            FileReader in = new FileReader("mails-6517.txt");
            BufferedReader buf = new BufferedReader(in);

            while ((line = buf.readLine()) != null) {
                // proceso estructura del archivo
                campos = line.split(":", 2);
                //System.out.println(line);

                if (campos.length > 0)
                    switch (campos[0]) {
                        case "-.-.-":
                            if (mail != null) {
                                ret.add(mail);
                            }

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