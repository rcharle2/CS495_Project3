import java.util.concurrent.*;
import java.util.regex.*;
import java.util.*;
import java.io.*;

public class WordCountJ implements Runnable {
   private Scanner sc;
   private static ConcurrentHashMap<String, Integer> wordcount = new ConcurrentHashMap<String, Integer>();
   
   public WordCountJ(String filename) {
      try {
         sc = new Scanner(new File(filename));
      } catch (FileNotFoundException e) {
         System.exit(1);
      }
   }

   public void run() {
      wordCount();
   }

   public synchronized void wordCount() {
      String pattern = "\\w+([-']\\w+)?";
      Pattern regex = Pattern.compile(pattern);

      while (sc.hasNext()) {
         String line = sc.nextLine().toLowerCase();
         Matcher match = regex.matcher(line);
         while (match.find()) {
            int start = match.start(0);
            int end = match.end(0);
            String word = line.substring(start, end);

            if (wordcount.containsKey(word))
               wordcount.put(word, wordcount.get(word) + 1);
            else
               wordcount.put(word, 1);
         }
      }
      sc.close();
   }

   public static void writeResults() {
      String outputFileName = "result.txt";
      PrintWriter out;
      try {
         out = new PrintWriter(outputFileName);

         String[] keys = wordcount.keySet().toArray(new String[0]);
         for (String key : keys) {
            out.format("%-20s %s\n", key, wordcount.get(key));
         }

         out.close();
      } catch (FileNotFoundException e) {
         System.out.printf("File %s not found\n", outputFileName);
      }
   }

   public static void main(String[] args) {
      int numberOfThreads = 4;
      String[] filenames;
      Thread[] threads = new Thread[args.length];

      for (int i = 0; i < args.length; i++) {
         Runnable r = new WordCountJ(args[i]);
         threads[i] = new Thread(r);
         threads[i].start();
      }

      int completed = 0;
      while (completed < threads.length) {
         for (int i = 0; i < threads.length; i++) {
            //System.out.println("Thead state " + threads[i].getState() + "\n");
            if (threads[i].getState() == Thread.State.TERMINATED)
               completed++;
         }
      }

      writeResults();
   }
}
