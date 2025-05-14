//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.bookstore.model.review;

import java.util.Date;

public class Review {
    private String reviewId;
    private String userId;
    private String userName;
    private String bookId;
    private String comment;
    private int rating;
    private Date reviewDate;
    private ReviewType reviewType;

    public Review() {
        this.reviewDate = new Date();
        this.reviewType = Review.ReviewType.STANDARD;
    }

    public Review(String reviewId, String userId, String userName, String bookId, String comment, int rating, Date reviewDate, ReviewType reviewType) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.userName = userName;
        this.bookId = bookId;
        this.comment = comment;
        this.rating = rating;
        this.reviewDate = reviewDate;
        this.reviewType = reviewType;
    }

    public String getReviewId() {
        return this.reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBookId() {
        return this.bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return this.rating;
    }

    public void setRating(int rating) {
        if (rating < 1) {
            rating = 1;
        }

        if (rating > 5) {
            rating = 5;
        }

        this.rating = rating;
    }

    public Date getReviewDate() {
        return this.reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public ReviewType getReviewType() {
        return this.reviewType;
    }

    public void setReviewType(ReviewType reviewType) {
        this.reviewType = reviewType;
    }

    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.reviewId).append(",");
        sb.append(this.userId).append(",");
        sb.append(this.userName.replace(",", "{{COMMA}}")).append(",");
        sb.append(this.bookId).append(",");
        sb.append(this.comment.replace(",", "{{COMMA}}").replace("\n", "{{NEWLINE}}")).append(",");
        sb.append(this.rating).append(",");
        sb.append(this.reviewDate.getTime()).append(",");
        sb.append(this.reviewType.name());
        return sb.toString();
    }

    public static Review fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        if (parts.length >= 8) {
            try {
                Review review = new Review();
                review.setReviewId(parts[0]);
                review.setUserId(parts[1]);
                review.setUserName(parts[2].replace("{{COMMA}}", ","));
                review.setBookId(parts[3]);
                review.setComment(parts[4].replace("{{COMMA}}", ",").replace("{{NEWLINE}}", "\n"));
                review.setRating(Integer.parseInt(parts[5]));
                review.setReviewDate(new Date(Long.parseLong(parts[6])));
                review.setReviewType(Review.ReviewType.valueOf(parts[7]));
                return review;
            } catch (Exception e) {
                System.err.println("Error parsing review from file: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public String toString() {
        return "Review{reviewId='" + this.reviewId + '\'' + ", userId='" + this.userId + '\'' + ", userName='" + this.userName + '\'' + ", bookId='" + this.bookId + '\'' + ", rating=" + this.rating + ", reviewDate=" + this.reviewDate + ", reviewType=" + this.reviewType + '}';
    }

    public static enum ReviewType {
        GUEST("Guest Review"),
        STANDARD("User Review"),
        VERIFIED_PURCHASE("Verified Purchase");

        private final String displayName;

        private ReviewType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }
    }
}
