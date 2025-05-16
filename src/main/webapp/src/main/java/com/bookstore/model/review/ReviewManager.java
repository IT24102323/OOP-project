package com.bookstore.model.review;

import java.io.*;
import java.util.*;
import javax.servlet.ServletContext;

import com.bookstore.model.book.Book;
import com.bookstore.model.book.BookManager;


public class ReviewManager {
    private static final String REVIEWS_FILE = "reviews.txt";
    private List<Review> reviews;
    private ServletContext servletContext;
    private String dataFilePath;


    public ReviewManager(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.reviews = new ArrayList<>();
        initializeFilePath();
        loadReviews();
    }


    private void initializeFilePath() {
        if (servletContext != null) {
            // Use WEB-INF/data within the application context
            String dataPath = "/WEB-INF/data";
            dataFilePath = servletContext.getRealPath(dataPath) + File.separator + REVIEWS_FILE;

            // Create directory if it doesn't exist
            File dataDir = new File(servletContext.getRealPath(dataPath));
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                System.out.println("Created data directory: " + dataDir.getAbsolutePath() + " - Success: " + created);
            }
        } else {
            // Fallback for non-web applications
            String dataPath = "data";
            dataFilePath = dataPath + File.separator + REVIEWS_FILE;

            // Create directory if it doesn't exist
            File dataDir = new File(dataPath);
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                System.out.println("Created data directory: " + dataPath + " - Success: " + created);
            }
        }

        System.out.println("Reviews file path: " + dataFilePath);
    }


    private void loadReviews() {
        File file = new File(dataFilePath);

        // Create the file if it doesn't exist
        if (!file.exists()) {
            try {
                // Ensure directory exists
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
                System.out.println("Created reviews file: " + dataFilePath);
                return;
            } catch (IOException e) {
                System.err.println("Error creating reviews file: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                Review review = Review.fromFileString(line);
                if (review != null) {
                    reviews.add(review);
                }
            }
            System.out.println("Loaded " + reviews.size() + " reviews");
        } catch (IOException e) {
            System.err.println("Error loading reviews: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean saveReviews() {
        try {
            // Ensure directory exists
            File file = new File(dataFilePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean created = parent.mkdirs();
                System.out.println("Created directory: " + parent.getAbsolutePath() + " - Success: " + created);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Review review : reviews) {
                    writer.write(review.toFileString());
                    writer.newLine();
                }
            }

            System.out.println("Saved " + reviews.size() + " reviews");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving reviews: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public Review addStandardReview(String userId, String userName, String bookId, String comment, int rating) {
        if (hasUserReviewedBook(userId, bookId)) {
            return null; // User already reviewed this book
        }

        Review review = new Review();
        review.setReviewId(UUID.randomUUID().toString());
        review.setUserId(userId);
        review.setUserName(userName);
        review.setBookId(bookId);
        review.setComment(comment);
        review.setRating(rating);
        review.setReviewDate(new Date());
        review.setReviewType(Review.ReviewType.STANDARD);

        reviews.add(review);
        saveReviews();

        // Update book rating
        updateBookRating(bookId, rating);

        return review;
    }


    public Review addVerifiedReview(String userId, String userName, String bookId, String comment, int rating) {
        if (hasUserReviewedBook(userId, bookId)) {
            return null; // User already reviewed this book
        }

        // TODO: Check if the user has purchased the book
        boolean hasPurchased = false; // This would be checked against order history

        if (!hasPurchased) {
            return null; // User hasn't purchased the book, can't leave verified review
        }

        Review review = new Review();
        review.setReviewId(UUID.randomUUID().toString());
        review.setUserId(userId);
        review.setUserName(userName);
        review.setBookId(bookId);
        review.setComment(comment);
        review.setRating(rating);
        review.setReviewDate(new Date());
        review.setReviewType(Review.ReviewType.VERIFIED_PURCHASE);

        reviews.add(review);
        saveReviews();

        // Update book rating
        updateBookRating(bookId, rating);

        return review;
    }


    public Review addGuestReview(String guestName, String bookId, String comment, int rating) {
        Review review = new Review();
        review.setReviewId(UUID.randomUUID().toString());
        review.setUserId("guest");
        review.setUserName(guestName);
        review.setBookId(bookId);
        review.setComment(comment);
        review.setRating(rating);
        review.setReviewDate(new Date());
        review.setReviewType(Review.ReviewType.GUEST);

        reviews.add(review);
        saveReviews();

        // Update book rating
        updateBookRating(bookId, rating);

        return review;
    }


    private void updateBookRating(String bookId, int rating) {
        BookManager bookManager = new BookManager(servletContext);
        Book book = bookManager.getBookById(bookId);
        if (book != null) {
            book.addRating(rating);
            bookManager.updateBook(book);
        }
    }


    public boolean updateReview(String reviewId, String userId, String comment, int rating) {
        for (Review review : reviews) {
            if (review.getReviewId().equals(reviewId)) {
                // Verify the user owns this review
                if (!review.getUserId().equals(userId)) {
                    return false;
                }

                int oldRating = review.getRating();

                // Update review details
                review.setComment(comment);
                review.setRating(rating);
                review.setReviewDate(new Date()); // Update review date on edit

                saveReviews();

                // Update book's rating
                if (oldRating != rating) {
                    // In a real implementation, we'd need to properly recalculate the book's rating
                    // This is simplified for demonstration
                    BookManager bookManager = new BookManager(servletContext);
                    Book book = bookManager.getBookById(review.getBookId());
                    if (book != null) {
                        // Simple approximation - not accurate for multiple rating changes
                        book.addRating(rating);
                        bookManager.updateBook(book);
                    }
                }

                return true;
            }
        }
        return false; // Review not found
    }


    public boolean deleteReview(String reviewId, String userId) {
        for (Review review : reviews) {
            if (review.getReviewId().equals(reviewId)) {
                // Verify the user owns this review or is an admin
                if (!review.getUserId().equals(userId) && !userId.equals("admin")) {
                    return false;
                }

                reviews.remove(review);
                return saveReviews();
            }
        }
        return false; // Review not found
    }


    public List<Review> getBookReviews(String bookId) {
        List<Review> bookReviews = new ArrayList<>();
        for (Review review : reviews) {
            if (review.getBookId().equals(bookId)) {
                bookReviews.add(review);
            }
        }

        // Sort reviews by date, newest first
        Collections.sort(bookReviews, (r1, r2) -> r2.getReviewDate().compareTo(r1.getReviewDate()));

        return bookReviews;
    }


    public Review getReviewById(String reviewId) {
        for (Review review : reviews) {
            if (review.getReviewId().equals(reviewId)) {
                return review;
            }
        }
        return null;
    }


    public boolean hasUserReviewedBook(String userId, String bookId) {
        if (userId == null) {
            return false; // Guest users or not logged in
        }

        for (Review review : reviews) {
            if (review.getUserId().equals(userId) && review.getBookId().equals(bookId)) {
                return true;
            }
        }
        return false;
    }


    public Review getUserReviewForBook(String userId, String bookId) {
        if (userId == null) {
            return null; // Guest users or not logged in
        }

        for (Review review : reviews) {
            if (review.getUserId().equals(userId) && review.getBookId().equals(bookId)) {
                return review;
            }
        }
        return null;
    }


    public List<Review> getUserReviews(String userId) {
        List<Review> userReviews = new ArrayList<>();
        for (Review review : reviews) {
            if (review.getUserId().equals(userId)) {
                userReviews.add(review);
            }
        }
        return userReviews;
    }


    public Map<String, Object> getReviewStatistics(String bookId) {
        Map<String, Object> stats = new HashMap<>();
        List<Review> bookReviews = getBookReviews(bookId);

        // Count total reviews
        int totalReviews = bookReviews.size();
        stats.put("totalReviews", totalReviews);

        // Calculate distribution of ratings
        Map<Integer, Integer> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingDistribution.put(i, 0);
        }

        double totalRating = 0;
        for (Review review : bookReviews) {
            int rating = review.getRating();
            ratingDistribution.put(rating, ratingDistribution.get(rating) + 1);
            totalRating += rating;
        }
        stats.put("ratingDistribution", ratingDistribution);

        // Calculate average rating
        double averageRating = totalReviews > 0 ? totalRating / totalReviews : 0;
        stats.put("averageRating", averageRating);

        return stats;
    }
}
