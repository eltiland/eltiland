package com.eltiland.model.search;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Extension of the Morphology analyzer. Adding an extra step of getting the stop words out (after the morphology
 * normalization)
 */
public class RussianAnalyzerStopWords extends RussianAnalyzer {

    private static final String[] RUSSIAN_STOP_WORDS_30 = {
            "а",
            "без",
            "более",
            "бы",
            "был",
            "была",
            "были",
            "было",
            "быть",
            "в",
            "вам",
            "вас",
            "во",
            "вот",
            "все",
            "всего",
            "всех",
            "вы",
            "где",
            "даже",
            "для",
            "до",
            "его",
            "ее",
            "ей",
            "ею",
            "если",
            "есть",
            "еще", "же", "за", "здесь", "и", "из", "или", "к", "как",
            "ко", "когда", "кто", "ли", "либо", "мне", "может", "на",
            "наш", "не", "него", "ни", "них", "но", "ну", "о", "об",
            "однако", "он", "она", "они", "оно", "от", "по", "под", "при",
            "с", "со", "так", "также", "там", "те", "тем", "то", "того",
            "тоже", "той", "том", "ты", "у", "уже", "хотя", "чего", "чей",
            "чем", "что", "чтобы", "чье", "чья", "эта", "эти", "это", "я"
    };

    private static Set<String> stopWords = new HashSet<String>();

    public RussianAnalyzerStopWords() throws IOException {
        super();
    }

    static {
        stopWords.addAll(Arrays.asList(RUSSIAN_STOP_WORDS_30));
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream afterMorphologyApplied = super.tokenStream(fieldName, reader);
        return new StopFilter(Version.LUCENE_36, afterMorphologyApplied, stopWords);
    }
}
