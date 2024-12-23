package bg.sofia.uni.fmi.mjt.goodreads;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.BookRecommender;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite.CompositeSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions.TFIDFSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres.GenresOverlapSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Path dataFilePath = Path.of("goodreads_data.csv");
        Path stopwordsFilePath = Path.of("stopwords.txt");
        try {
            TextTokenizer tokenizer = new TextTokenizer(new FileReader(stopwordsFilePath.toFile()));

            Set<Book> books = BookLoader.load(new FileReader(dataFilePath.toFile()));

            SimilarityCalculator genresCalculator = new GenresOverlapSimilarityCalculator();
            SimilarityCalculator descriptionsCalculator = new TFIDFSimilarityCalculator(books, tokenizer);

            SimilarityCalculator compositeCalculator = new CompositeSimilarityCalculator(
                    Map.of(genresCalculator, 0.5, descriptionsCalculator, 0.5)
            );

            BookRecommender recommender = new BookRecommender(books, compositeCalculator);

            Book originBook = books.stream().filter(book -> book.ID().equals("2")).findFirst().get();
            System.out.println("Origin Book: " + originBook.title() + " by " + originBook.author());

            System.out.println("\nTop 5 Recommended Books:");
            Map<Book, Double> recommendations = recommender.recommendBooks(originBook, 5);

            recommendations.forEach((book, similarity) -> {
                System.out.printf("Title: %s | Author: %s | Similarity: %.2f%n",
                        book.title(), book.author(), similarity);
            });

        } catch (IOException e) {
            System.err.println("Error reading input files: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error initializing components: " + e.getMessage());
        }
    }

    private static Book getRandomBook(Set<Book> books) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Books collection cannot be null or empty");
        }

        List<Book> bookList = new ArrayList<>(books);
        Random random = new Random();
        return bookList.get(random.nextInt(bookList.size()));
    }
}