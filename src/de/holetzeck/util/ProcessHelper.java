// Taken from https://bitbucket.org/jholetzeck/ministatus-android/src/0d594b5dbe0f9c838c4a61cb0e339f1ae11cb883/src/de/holetzeck/util/ProcessHelper.java?at=default
// Permission from here http://stackoverflow.com/questions/15668688/calling-ls-with-root-on-anrdoid-returns-nothing-application-hangs
package de.holetzeck.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Pair;

public class ProcessHelper
{
        /**
         * run external command
         * 
         * @param addCommandline
         *            add commandline to stdout
         * @param commands
         *            list of command and arguments to run
         * @return Pair of exitCode and stdout/stderr
         */
        static public Pair<Integer, String> runCmd(boolean addCommandline, String... commands)
        {
                final int BUFSIZE = 1024;
                StringBuilder sb = new StringBuilder(BUFSIZE);

                if (addCommandline)
                {
                        // add input string to stdout
                        for (String c : commands)
                        {
                                sb.append(c);
                                sb.append(" ");
                        }
                        sb.append("\n");
                }

                int exitVal = -1;
                ProcessBuilder builder = new ProcessBuilder(commands);
                builder.redirectErrorStream(true);
                try
                {
                        Process p = builder.start();

                        InputStream in = p.getInputStream();
                        InputStreamReader isr = new InputStreamReader(in);
                        char[] buf = new char[BUFSIZE];
                        int len = -1;
                        while (-1 != (len = isr.read(buf)))
                        {
                                sb.append(buf, 0, len);
                        }
                        exitVal = p.waitFor();

                        p.destroy();
                } catch (IOException e)
                {
                        e.printStackTrace();
                } catch (InterruptedException e)
                {
                        e.printStackTrace();
                }

                return new Pair<Integer, String>(exitVal, sb.toString());
        }

}