package trainModel;

import opennlp.tools.tokenize.*;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import java.io.*;

/**
 * Created by asingh on 10/03/16.
 */
public class TrainOne {

    public static void storeModel(){
        BufferedOutputStream modelOutputStream = null;
        try {
            ObjectStream<String> lineStream = new PlainTextByLineStream(
                    new FileInputStream("src/main/resources/training-data.train"), "UTF-8");
            ObjectStream<TokenSample> sampleStream =
                    new TokenSampleStream(lineStream);
            TokenizerModel model = TokenizerME.train(
                    "en", sampleStream, true, 5, 100);
            modelOutputStream = new BufferedOutputStream(
                    new FileOutputStream(new File("mymodel.bin")));
            model.serialize(modelOutputStream);

                String paragraph = "A demonstration of how to train a tokenizer.";
                InputStream modelIn = new FileInputStream(new File(
                        ".", "mymodel.bin"));
                TokenizerModel model1 = new TokenizerModel(modelIn);
                Tokenizer tokenizer = new TokenizerME(model);
                String tokens[] = tokenizer.tokenize(paragraph);
                for (String token : tokens) {
                    System.out.println(token);
                }
        } catch (UnsupportedEncodingException ex) {
            System.out.println("TrainOne" + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("TrainOne" + ex.getMessage());
        }
    }
    public static void main(String [] args){
        storeModel();
    }
}
