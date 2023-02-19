package bg.sofia.uni.fmi.mjt.sentiment;

import java.io.*;
import java.util.*;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {
    private static final int UNKNOWN = -1;
    private static final double NEGATIVE = 0.5;
    private static final double SOMEWHAT_NEGATIVE = 1.5;
    private static final double NEUTRAL = 2.5;
    private static final double SOMEWHAT_POSITIVE = 3.5;
    private static final int MAX_SENTIMENT = 4;

    private final Map<String, Integer> reviews;
    private final Writer reviewsFile;
    private final Set<String> words;
    private final List<String> stopWords;

    private int countThisWord(String word, String review) {
        String[] reviewWords = review.split(" ");

        int count = 0;
        if (validWord(word) && !isStopWord(word)) {
            for (String s : reviewWords) {
                if (s.equalsIgnoreCase(word)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean validWord(String word) {
        if (word.length() <= 1) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            if (!Character.isLetterOrDigit(word.charAt(i))
                && word.charAt(i) != '\'') {
                return false;
            }
        }
        return true;
    }

    public boolean recognizesWord(String word) {
        return words.stream().anyMatch(x -> x.equalsIgnoreCase(word));
    }

    private String processReview(String review) {
        StringBuilder result = new StringBuilder();

        String[] separateWords = review.split(" ");
        for (String s : separateWords) {
            if (validWord(s) && !isStopWord(s)) {
                result.append(" ").append(s.trim());
            }
        }
        return result.toString();
    }

    public MovieReviewSentimentAnalyzer(Reader stopWordsIn, Reader reviewsIn, Writer reviewsOut) {
        stopWords = new ArrayList<>();
        words = new HashSet<>();
        reviews = new HashMap<>();
        reviewsFile = reviewsOut;

        try (var bufferedReader = new BufferedReader(stopWordsIn)) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stopWords.add(line.trim());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var bufferedReader = new BufferedReader(reviewsIn)) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isBlank() && !line.isEmpty()) {
                    int sentiment = line.charAt(0) - '0';
                    String review = line.substring(line.indexOf(" "));
                    review = processReview(review);

                    reviews.put(review, sentiment);

                    String[] w = review.split(" ");
                    for (String s : w) {
                        if (validWord(s) && !isStopWord((s))) {
                            words.add(s.toLowerCase(Locale.ROOT));
                        }
                    }

                }
            }

        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        }
    }

    @Override
    public double getReviewSentiment(String review) {
        String[] reviewWords = review.split(" ");
        double average = 0.0;
        int count = 0;
        for (String word : reviewWords) {
            if (recognizesWord(word.trim()) && !isStopWord(word.trim())) {
                average += getWordSentiment(word);
                count++;
            }
        }
        if (count == 0) {
            return -1;
        }
        return average / count;
    }

    @Override
    public String getReviewSentimentAsName(String review) {
        double rating = getReviewSentiment(review);
        if (rating == UNKNOWN) {
            return "unknown";
        }
        if (rating < NEGATIVE) {
            return "negative";
        }
        if (rating < SOMEWHAT_NEGATIVE) {
            return "somewhat negative";
        }
        if (rating < NEUTRAL) {
            return "neutral";
        }
        if (rating < SOMEWHAT_POSITIVE) {
            return "somewhat positive";
        }
        return "positive";
    }

    @Override
    public double getWordSentiment(String word) {
        int count = 0;
        double average = 0;

        for (Map.Entry<String, Integer> line : reviews.entrySet()) {
            String[] lineWords = line.getKey().split(" ");
            for (String s : lineWords) {
                if (s.equalsIgnoreCase(word)) {
                    average += line.getValue();
                    count++;
                }
            }
        }

        return average / count;
    }

    @Override
    public int getWordFrequency(String word) {
        return reviews.keySet()
            .stream()
            .map((x) -> countThisWord(word, x))
            .reduce(0, Integer::sum);
    }

    @Override
    public List<String> getMostFrequentWords(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Can't get most frequent words with a negative number.");
        }

        return words.stream()
            .sorted(Comparator.comparing(this::getWordFrequency).reversed())
            .limit(n)
            .toList();
    }

    @Override
    public List<String> getMostPositiveWords(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Can't get most positive words with a negative number.");
        }

        return words.stream()
            .sorted(Comparator.comparing(this::getWordSentiment).reversed())
            .limit(n)
            .toList();
    }

    @Override
    public List<String> getMostNegativeWords(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Can't get most negative words with a negative number.");
        }

        return words.stream()
            .sorted(Comparator.comparing(this::getWordSentiment))
            .limit(n)
            .toList();
    }

    @Override
    public boolean appendReview(String review, int sentiment) {
        if (review == null || review.isBlank() || review.isEmpty()) {
            throw new IllegalArgumentException("Tried to append a review with an invalid text.");
        }
        if (sentiment < 0 || sentiment > MAX_SENTIMENT) {
            throw new IllegalArgumentException("Tried to create a review with an illegal rating.");
        }
        try (var bufferedWriter = new BufferedWriter(reviewsFile)) {

            String line = processReview(review);
            if (!reviews.containsKey(line) && !line.isBlank()) {
                bufferedWriter.append(String.valueOf(sentiment))
                    .append(" ").append(line).append(System.lineSeparator());
                bufferedWriter.flush();

                String[] lineWords = line.split(" ");
                for (String s : lineWords) {
                    if (validWord(s)) {
                        this.words.add(s.toLowerCase(Locale.ROOT));
                    }
                }
                reviews.put(line, sentiment);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public int getSentimentDictionarySize() {
        return words.size();
    }

    @Override
    public boolean isStopWord(String word) {
        return stopWords.stream().anyMatch(x -> x.equalsIgnoreCase(word.trim()));
    }
}
