package bg.sofia.uni.fmi.mjt.sentiment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MovieReviewSentimentAnalyzerTest {
    private static SentimentAnalyzer sentimentAnalyzer;
    private static final int UNKNOWN = -1;
    private static final int NEGATIVE = 0;
    private static final int SOMEWHAT_NEGATIVE = 1;
    private static final int NEUTRAL = 2;
    private static final int SOMEWHAT_POSITIVE = 3;
    private static final int POSITIVE = 4;
    private static final int NEGATIVE_NUMBER = -4;
    private static final int COUNT_WORDS = 10;


    @BeforeAll
    static void setUp() {
        try {
            Reader r = new FileReader("stopwords.txt");
            Reader r1 = new FileReader("smallReviewDataset.txt");
            Writer w = new FileWriter("smallReviewDataset.txt", true);
            sentimentAnalyzer = new MovieReviewSentimentAnalyzer(r, r1, w);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetReviewSentimentImpossibleWord() {
        assertEquals(UNKNOWN, sentimentAnalyzer.getReviewSentiment("impossibleWord"));
    }

    @Test
    void testGetReviewSentimentCaseInsensitive() {
        assertEquals(POSITIVE, sentimentAnalyzer.getReviewSentiment("Introspective"));
    }

    @Test
    void testGetReviewSentimentPositive() {

        assertEquals(POSITIVE, sentimentAnalyzer.getReviewSentiment("introspective"));
    }

    @Test
    void testGetReviewSentimentSomewhatPositive() {
        assertEquals(SOMEWHAT_POSITIVE, sentimentAnalyzer.getReviewSentiment("positively"));
    }

    @Test
    void testGetReviewSentimentNeutral() {

        assertEquals(NEUTRAL, sentimentAnalyzer.getReviewSentiment("dizzily"));
    }

    @Test
    void testGetReviewSentimentSomewhatNegative() {
        assertEquals(SOMEWHAT_NEGATIVE, sentimentAnalyzer.getReviewSentiment("hard"));
    }

    @Test
    void testGetReviewSentimentNegative() {
        assertEquals(NEGATIVE, sentimentAnalyzer.getReviewSentiment("Hampered"));
    }

    @Test
    void testGetReviewSentimentAsNameImpossibleWord() {
        assertEquals("unknown", sentimentAnalyzer.getReviewSentimentAsName("impossibleWord"));
    }

    @Test
    void testGetReviewSentimentAsNameCaseInsensitive() {
        assertEquals("positive", sentimentAnalyzer.getReviewSentimentAsName("Introspective"));
    }

    @Test
    void testGetReviewSentimentAsNamePositive() {
        assertEquals("positive", sentimentAnalyzer.getReviewSentimentAsName("introspective"));
    }

    @Test
    void testGetReviewSentimentAsNameSomewhatPositive() {
        assertEquals("somewhat positive", sentimentAnalyzer.getReviewSentimentAsName("positively"));
    }

    @Test
    void testGetReviewSentimentAsNameNeutral() {
        assertEquals("neutral", sentimentAnalyzer.getReviewSentimentAsName("dizzily"));
    }

    @Test
    void testGetReviewSentimentAsNameSomewhatNegative() {
        assertEquals("somewhat negative", sentimentAnalyzer.getReviewSentimentAsName("hard"));
    }

    @Test
    void testGetReviewSentimentAsNameNegative() {
        assertEquals("negative", sentimentAnalyzer.getReviewSentimentAsName("Hampered"));
    }

    @Test
    void testGetWordFrequencyImpossibleWord() {
        assertEquals(NEGATIVE, sentimentAnalyzer.getWordFrequency("impossibleWord"));
    }

    @Test
    void testGetWordFrequency() {

        assertEquals(2, sentimentAnalyzer.getWordFrequency("story"));
    }

    @Test
    void testGetMostFrequentWordsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> sentimentAnalyzer.getMostFrequentWords(NEGATIVE_NUMBER));
    }

    @Test
    void testGetMostFrequentWords() {
        List<String> list = new LinkedList<>(List.of(new String[] {"movie", "good", "like", "much",
            "entertaining", "one", "story", "opera", "almost", "none"}));

        List<String> result = sentimentAnalyzer.getMostFrequentWords(COUNT_WORDS);

        assertTrue(list.containsAll(result)
            && result.containsAll(list)
            && result.size() == list.size());
    }

    @Test
    void testGetMostFrequentWordsDecreasing() {
        List<String> result = sentimentAnalyzer.getMostFrequentWords(COUNT_WORDS);

        assertTrue(sentimentAnalyzer.getWordFrequency(result.get(0)) >=
            sentimentAnalyzer.getWordFrequency(result.get(result.size() - 1)));
    }

    @Test
    void testGetMostPositiveWordsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> sentimentAnalyzer.getMostPositiveWords(NEGATIVE_NUMBER));
    }

    @Test
    void testGetMostPositiveWords() {
        List<String> list = new LinkedList<>(List.of(
            new String[] {"independent", "worth", "quiet", "seeking", "introspective", "opera", "betrayal",
                "soon", "positively", "judge"}));

        List<String> result = sentimentAnalyzer.getMostPositiveWords(COUNT_WORDS);

        assertTrue(list.containsAll(result)
            && result.containsAll(list)
            && result.size() == list.size());
    }

    @Test
    void testGetMostPositiveWordsDecreasing() {
        List<String> result = sentimentAnalyzer.getMostPositiveWords(COUNT_WORDS);

        assertTrue(sentimentAnalyzer.getWordFrequency(result.get(0)) >=
            sentimentAnalyzer.getWordFrequency(result.get(result.size() - 1)));
    }

    @Test
    void testGetMostNegativeWordsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> sentimentAnalyzer.getMostNegativeWords(NEGATIVE_NUMBER));
    }

    @Test
    void testGetMostNegativeWords() {
        List<String> list = new LinkedList<>(List.of(
            new String[] {"sounding", "script", "ends", "poetry", "hampered", "aims", "paralyzed",
                "satire", "almost", "none"}));

        List<String> result = sentimentAnalyzer.getMostNegativeWords(COUNT_WORDS);
        //assertEquals(null,result);

        assertTrue(list.containsAll(result)
            && result.containsAll(list)
            && result.size() == list.size());
    }

    @Test
    void testGetMostNegativeWordsDecreasing() {
        List<String> result = sentimentAnalyzer.getMostNegativeWords(COUNT_WORDS);

        assertTrue(sentimentAnalyzer.getWordFrequency(result.get(0)) <=
            sentimentAnalyzer.getWordFrequency(result.get(result.size() - 1)));
    }

    @Test
    void testAppendReviewNullReview() {
        assertThrows(IllegalArgumentException.class,
            () -> sentimentAnalyzer.appendReview(null, POSITIVE));
    }

    @Test
    void testAppendReviewEmptyReview() {
        assertThrows(IllegalArgumentException.class,
            () -> sentimentAnalyzer.appendReview("", POSITIVE));
    }

    @Test
    void testAppendReviewBlankReview() {
        assertThrows(IllegalArgumentException.class,
            () -> sentimentAnalyzer.appendReview("  ", POSITIVE));
    }

    @Test
    void testAppendReviewNegativeSentiment() {
        assertThrows(IllegalArgumentException.class,
            () -> sentimentAnalyzer.appendReview("review", NEGATIVE_NUMBER));
    }

    @Test
    void testAppendReviewLargeSentiment() {
        assertThrows(IllegalArgumentException.class,
            () -> sentimentAnalyzer.appendReview("review", COUNT_WORDS));
    }

    @Test
    void testAppendReview() {
        sentimentAnalyzer.appendReview("newWord'", NEUTRAL);
        assertEquals(2, sentimentAnalyzer.getWordSentiment("newWord'"));
    }

    @Test
    void testAppendReviewNotAddStopWord() {
        sentimentAnalyzer.appendReview("yourself", NEUTRAL);
        assertFalse(sentimentAnalyzer.recognizesWord("yourself"));
    }

    @Test
    void testAppendReviewNotAddPunctuation() {
        sentimentAnalyzer.appendReview("...", NEUTRAL);
        assertFalse(sentimentAnalyzer.recognizesWord("..."));
    }
}
