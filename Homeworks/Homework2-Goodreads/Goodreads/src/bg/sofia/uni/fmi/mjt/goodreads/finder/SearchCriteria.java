package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;

@FunctionalInterface
public interface SearchCriteria {
    boolean matches(Book book);
}