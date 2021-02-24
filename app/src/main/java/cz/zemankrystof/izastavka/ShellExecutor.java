package cz.zemankrystof.izastavka;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellExecutor {

        public ShellExecutor() {

        }

        public String Executor(String command) {

            StringBuffer output = new StringBuffer();

            Process p;
            try {
                p = Runtime.getRuntime().exec(command);
                p.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                String line = "";
                while ((line = reader.readLine())!= null) {
                    output.append(line + "n");
                    Log.e("SHELL", "" + line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            String response = output.toString();
            return response;

        }
}
