package com.bookstore.servlet.review;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bookstore.model.review.Review;
import com.bookstore.model.review.ReviewManager;
import com.bookstore.util.ValidationUtil;

@WebServlet("/delete-book-review")
public class DeleteReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reviewId = request.getParameter("reviewId");

        if (ValidationUtil.isNullOrEmpty(reviewId)) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }


        HttpSession session = request.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("userId") : null;

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
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
            session.setAttribute("errorMessage", "You are not authorized to delete this review");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }


        request.setAttribute("review", review);
        request.setAttribute("bookId", review.getBookId());
        request.getRequestDispatcher("/review/delete-review.jsp").forward(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reviewId = request.getParameter("reviewId");
        String confirm = request.getParameter("confirm");

        if (ValidationUtil.isNullOrEmpty(reviewId)) {
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }


        HttpSession session = request.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("userId") : null;

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
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
            session.setAttribute("errorMessage", "You are not authorized to delete this review");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }


        if ("yes".equals(confirm)) {

            boolean deleted = reviewManager.deleteReview(reviewId, userId);

            if (deleted) {
                session.setAttribute("successMessage", "Review deleted successfully");
            } else {
                session.setAttribute("errorMessage", "Failed to delete review");
            }
        }


        response.sendRedirect(request.getContextPath() + "/book-details?id=" + review.getBookId());
    }
}