package heitor;

import density.ParamsPre;
import gnu.getopt.Getopt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class AppCheck {
    public static void main(String[] args) throws IOException {
        String baseName = "MessagesBundle";
        ResourceBundle _messages = PropertyResourceBundle.getBundle(baseName, Locale.getDefault());


        String name = "/parameters.csv";
        InputStream resourceAsStream = ParamsPre.class.getResourceAsStream(name);
        System.out.println(resourceAsStream);
    }
}
