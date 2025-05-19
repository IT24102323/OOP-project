package com.bookstore.servlet.review;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

@WebServlet("/book-reviews")
public class ViewReviewsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String bookId = request.getParameter("bookId");

        if (ValidationUtil.isNullOrEmpty(bookId)) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }


        BookManager bookManager = new BookManager(getServletContext());
        Book book = bookManager.getBookById(bookId);

        if (book == null) {
            request.getSession().setAttribute("errorMessage", "Book not found");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }


        ReviewManager reviewManager = new ReviewManager(getServletContext());


        List<Review> reviews = reviewManager.getBookReviews(bookId);


        Map<String, Object> reviewStats = reviewManager.getReviewStatistics(bookId);


        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            String userId = (String) session.getAttribute("userId");
            Review userReview = reviewManager.getUserReviewForBook(userId, bookId);
            request.setAttribute("userReview", userReview);
            request.setAttribute("hasReviewed", (userReview != null));
        } else {
            request.setAttribute("hasReviewed", false);
        }


        request.setAttribute("book", book);
        request.setAttribute("reviews", reviews);
        request.setAttribute("reviewStats", reviewStats);


        request.getRequestDispatcher("/review/book-reviews.jsp").forward(request, response);
    }
}