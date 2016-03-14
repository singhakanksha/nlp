package nlp;


import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

import java.io.*;
import java.text.BreakIterator;
import java.util.*;

public class App {

    public static void scanner(){
        Scanner scanner = new Scanner("Let's pause, and then "+ " reflect.");
        //regular expression that matches those three characters
        scanner.useDelimiter("[ ,.]");
        List<String> list = new ArrayList<String>();
        while(scanner.hasNext()) {
            String token = scanner.next();
            list.add(token);
        }
        for(String token : list) {
            System.out.println(token);
        }



        String text = "Mr. Smith went to 123 Washington avenue.";
        String tokens[] = text.split("\\s+");
        for (String token : tokens) {
            System.out.println(token);
        }
    }


    public  static void breakiterator(){
        BreakIterator wordIterator = BreakIterator.getWordInstance();
        String text = "Let's pause, and then reflect.";
        wordIterator.setText(text);
        int boundary = wordIterator.first();
        while (boundary != BreakIterator.DONE) {
            int begin = boundary;
            System.out.print(boundary + "-");
            boundary = wordIterator.next();
            int end = boundary;
            if(end == BreakIterator.DONE) break;
            System.out.println(boundary + " ["
                    + text.substring(begin, end) + "]");
        }
    }

    public static void streamtokenizer(){
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(
                    new StringReader("Let's pause, and then reflect."));
            boolean isEOF = false;
            while (!isEOF) {
                int token = tokenizer.nextToken();
                switch (token) {
                    case StreamTokenizer.TT_EOF:
                        isEOF = true;
                        break;
                    case StreamTokenizer.TT_EOL:
                        break;
                    case StreamTokenizer.TT_WORD:
                        System.out.println(tokenizer.sval);
                        break;
                    case StreamTokenizer.TT_NUMBER:
                        System.out.println(tokenizer.nval);
                        break;
                    default:
                        System.out.println((char) token);
                }
            }
        } catch (IOException ex) {
            // Handle the exception
        }
    }


    public static void TokenizeOPENNLP() throws InvalidFormatException, IOException {
        InputStream is = new FileInputStream("src/main/resources/en-token.bin");

        TokenizerModel model = new TokenizerModel(is);

        Tokenizer tokenizer = new TokenizerME(model);

        String tokens[] = tokenizer.tokenize("He lives at 1511 W. Randolph.");

        for (String a : tokens)
            System.out.println(a);

        is.close();
    }


    public static void FindPeopleAndThings() throws IOException {

        try {
            String[] sentences = { "Tim was a good neighbor. Perhaps not as good a Bob " +
                    "Haywood, but still pretty good. Of course Mr. Adam " +
                    "took the cake!"};
            // Insert code to find the names here
            Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
            TokenNameFinderModel model = new TokenNameFinderModel(
                    new File("src/main/resources/en-ner-person.bin"));

            NameFinderME finder = new NameFinderME(model);
            for (String sentence : sentences) {
                String[] tokens = tokenizer.tokenize(sentence);
                Span[] nameSpans = finder.find(tokens);
                System.out.println(Arrays.toString(
                        Span.spansToStrings(nameSpans, tokens)));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void taggingPos(){
        POSModel model = new POSModelLoader().load(
                new File("src/main/resources/en-pos-maxent.bin"));
        POSTaggerME tagger = new POSTaggerME(model);
        String sentence = "POS processing is useful for enhancing the "
                + "quality of data sent to other elements of a pipeline.";
        String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(sentence);
        String[] tags = tagger.tag(tokens);
        for(int i=0; i<tokens.length; i++) {
            System.out.print(tokens[i] + "[" + tags[i] + "] ");
        }
    }




    public static void TokenizeStanfordWay(){
        PTBTokenizer ptb = new PTBTokenizer(
                new StringReader("He lives at 1511 W. Randolph."),
                new CoreLabelTokenFactory(), null);
        while (ptb.hasNext()) {
            System.out.println(ptb.next());
        }

        String paragraph = "The first sentence. The second sentence.";
        Reader reader = new StringReader(paragraph);
        DocumentPreprocessor documentPreprocessor =
                new DocumentPreprocessor(reader);
        List<String> sentenceList = new LinkedList<String>();
        for (List<HasWord> element : documentPreprocessor) {
            StringBuilder sentence = new StringBuilder();
            List<HasWord> hasWordList = element;
            for (HasWord token : hasWordList) {
                sentence.append(token).append(" ");
            }
            sentenceList.add(sentence.toString());

        }
        for (String sentence : sentenceList) {
            System.out.println(sentence);
        }
    }

    public static void extractRelationship(){
        Properties properties = new Properties();
        properties.put("annotators", "tokenize, ssplit, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        Annotation annotation = new Annotation(
                "The meaning and purpose of life is plain to see.");
        pipeline.annotate(annotation);
        pipeline.prettyPrint(annotation, System.out);
    }

// STANFORD tokenization ways
    public static void ptbtokenizer(){
         String paragraph = "Let's pause, \nand then " + "reflect.";
        PTBTokenizer ptbSimple = new PTBTokenizer(
                new StringReader(paragraph), new CoreLabelTokenFactory(),null);
        while (ptbSimple.hasNext()) {
            System.out.println(ptbSimple.next());
        }


        CoreLabelTokenFactory ctf = new CoreLabelTokenFactory();
        PTBTokenizer ptbWithOption = new PTBTokenizer(
                new StringReader(paragraph),ctf,"invertible=true");
        while (ptbWithOption.hasNext()) {
            CoreLabel cl = (CoreLabel)ptbWithOption.next();
            System.out.println(cl.originalText() + " (" +
                    cl.beginPosition() + "-" + cl.endPosition() + ")");
        }
    }


    public static void docPreprocessr(){
        String paragraph = "Let's pause, \nand then " + "reflect.";
        Reader reader = new StringReader(paragraph);
        DocumentPreprocessor documentPreprocessor =
                new DocumentPreprocessor(reader);
        Iterator<List<HasWord>> it = documentPreprocessor.iterator();
        while (it.hasNext()) {
            List<HasWord> sentence = it.next();
            for (HasWord token : sentence) {
                System.out.println(token);
            }
        }
    }

    public static void usePipeline(){
        String paragraph = "Let's pause, \nand then " + "reflect.";
        Properties properties = new Properties();
        properties.put("annotators", "tokenize, ssplit");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
        Annotation annotation = new Annotation(paragraph);
        pipeline.annotate(annotation);
        pipeline.prettyPrint(annotation, System.out);
    }

    public static void main(String[] args) throws InvalidFormatException, IOException {

      //  System.out.println("Hello World!");
//        TokenizeOPENNLP();
//        TokenizeStanfordWay();
//        FindPeopleAndThings();
//        taggingPos();
     //   extractRelationship();
       // scanner();
      //  breakiterator();
      //  streamtokenizer();
     //   ptbtokenizer();
//        docPreprocessr();
        usePipeline();
    }

}
