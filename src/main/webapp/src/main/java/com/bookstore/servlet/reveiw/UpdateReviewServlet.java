package com.bookstore.servlet.review;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bookstore.model.book.Book;
import com.bookstore.model.book.BookManager;
import com.bookstore.model.review.Review;
import com.bookstore.model.review.ReviewManager;
import com.bookstore.util.ValidationUtil;


@WebServlet("/update-book-review")
public class UpdateReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("userId") : null;

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String reviewId = request.getParameter("reviewId");

        if (ValidationUtil.isNullOrEmpty(reviewId)) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }


        ReviewManager reviewManager = new ReviewManager(getServletContext());
        Review review = reviewManager.getReviewById(reviewId);

        if (review == null) {
            session.setAttribute("errorMessage", "Review not found");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }


        if (!userId.equals(review.getUserId())) {
            session.setAttribute("errorMessage", "You do not have permission to edit this review");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }


        BookManager bookManager = new BookManager(getServletContext());
        Book book = bookManager.getBookById(review.getBookId());

        if (book == null) {
            session.setAttribute("errorMessage", "Book not found");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }


        request.setAttribute("review", review);
        request.setAttribute("book", book);


        request.getRequestDispatcher("/review/edit-review.jsp").forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("userId") : null;

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }


        String reviewId = request.getParameter("reviewId");
        String rating = request.getParameter("rating");
        String comment = request.getParameter("comment");


        if (ValidationUtil.isNullOrEmpty(reviewId) ||
                ValidationUtil.isNullOrEmpty(rating) ||
                ValidationUtil.isNullOrEmpty(comment)) {
            request.setAttribute("errorMessage", "All fields are required");
            doGet(request, response);
            return;
        }

        try {
            int ratingValue = Integer.parseInt(rating);

            if (ratingValue < 1 || ratingValue > 5) {
                request.setAttribute("errorMessage", "Rating must be between 1 and 5");
                doGet(request, response);
                return;
            }


            ReviewManager reviewManager = new ReviewManager(getServletContext());


            Review review = reviewManager.getReviewById(reviewId);
            if (review == null) {
                session.setAttribute("errorMessage", "Review not found");
                response.sendRedirect(request.getContextPath() + "/books");
                return;
            }


            if (!userId.equals(review.getUserId())) {
                session.setAttribute("errorMessage", "You do not have permission to edit this review");
                response.sendRedirect(request.getContextPath() + "/books");
                return;
            }


            boolean updated = reviewManager.updateReview(reviewId, userId, comment, ratingValue);

            if (updated) {
                session.setAttribute("successMessage", "Review updated successfully");
                response.sendRedirect(request.getContextPath() + "/book-reviews?bookId=" + review.getBookId());
            } else {
                request.setAttribute("errorMessage", "Failed to update review");
                doGet(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid rating format");
            doGet(request, response);
        }
    }
}