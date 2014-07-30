import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public abstract class WithDictionary {
    protected List<String> readDictionary() {
        String word;
        List<String> words = new ArrayList<String>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("dict.txt"));

            while ((word = bufferedReader.readLine()) != null) {
                words.add(word);
            }

            bufferedReader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return words;
    }
}
