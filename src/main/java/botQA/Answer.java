package botQA;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.trees.*;
import opennlp.tools.tokenize.SimpleTokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Answer {


    public static void main(String[] args){
        String question =
                "Who is the 32nd president of the United States?";
        //String parserModel = "edu.stanford.nlp.parser.lexparser.LexicalizedParser";
        LexicalizedParser lexicalizedParser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

        TokenizerFactory<CoreLabel> tokenizerFactory =
                PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        Tokenizer<CoreLabel> tokenizer =
                tokenizerFactory.getTokenizer(new StringReader(question));
        List<CoreLabel> wordList = tokenizer.tokenize();
        Tree parseTree = lexicalizedParser.apply(wordList);

        TreebankLanguagePack tlp =
                lexicalizedParser.treebankLanguagePack();
        GrammaticalStructureFactory gsf =
                tlp.grammaticalStructureFactory();
        GrammaticalStructure gs =
                gsf.newGrammaticalStructure(parseTree);
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
        System.out.println(tdl);
        for (TypedDependency dependency : tdl) {
            System.out.println("Governor Word: [" + dependency.gov()
                    + "] Relation: [" + dependency.reln().getLongName()
                    + "] Dependent Word: [" + dependency.dep() + "]");
        }
        for (TypedDependency dependency : tdl) {
            if ("nominal subject".equals( dependency.reln().getLongName())
                    && "who".equalsIgnoreCase( dependency.gov().originalText())) {
                new Answer().processWhoQuestion(tdl);
            }
        }
    }

    public static void processWhoQuestion(TypedDependency tdl){

    }


        public List<President> createPresidentList() {
            ArrayList<President> list = new ArrayList<>();
            String line = null;
            try (FileReader reader = new FileReader("src/main/resources/PresidentList");
                 BufferedReader br = new BufferedReader(reader)) {
                while ((line = br.readLine()) != null) {
                    SimpleTokenizer simpleTokenizer =
                            SimpleTokenizer.INSTANCE;
                    String tokens[] = simpleTokenizer.tokenize(line);
                    String name = "";
                    String start = "";
                    String end = "";
                    int i = 0;
                    while (!"(".equals(tokens[i])) {
                        name += tokens[i] + " ";
                        i++;
                    }
                    start = tokens[i + 1];
                    end = tokens[i + 3];
                    if (end.equalsIgnoreCase("present")) {
                        end = start;
                    }
                    list.add(new President(name,
                            Integer.parseInt(start),
                            Integer.parseInt(end)));
                }
            } catch (IOException ex) {
                // Handle exceptions
            }
            return list;
        }

    public void processWhoQuestion(List<TypedDependency> tdl) {
        List<President> list = createPresidentList();
        for (TypedDependency dependency : tdl) {
            if ("president".equalsIgnoreCase(
                    dependency.gov().originalText())
                    && "adjectival modifier".equals(
                    dependency.reln().getLongName())) {
                String positionText =
                        dependency.dep().originalText();
                int position = getOrder(positionText)-1;
                System.out.println("The president is "
                        + list.get(position).getName());
            }
        }
    }

    private static int getOrder(String position) {
        String tmp = "";
        int i = 0;
        while (Character.isDigit(position.charAt(i))) {
            tmp += position.charAt(i++);
        }
        return Integer.parseInt(tmp);
    }


}
