package botQA;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.trees.*;

import java.io.StringReader;
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
                processWhoQuestion(tdl);
            }
        }
    }
}
