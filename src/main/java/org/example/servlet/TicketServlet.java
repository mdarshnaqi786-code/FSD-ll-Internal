
package org.example.servlet;

import org.example.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/tickets")
public class TicketServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String sql = "SELECT * FROM event_tickets WHERE event_name = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "Music Concert");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_tickets");
                    int sold = rs.getInt("sold_tickets");

                    request.setAttribute("totalTickets", total);
                    request.setAttribute("soldTickets", sold);
                } else {
                    request.setAttribute("message", "Event not found!");
                }
            }

            request.getRequestDispatcher("purchase.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        int price = 500;
        int quantity;

        try {
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Please enter a valid quantity!");
            doGet(request, response);
            return;
        }

        if (quantity <= 0) {
            request.setAttribute("message", "Please select at least 1 ticket!");
            doGet(request, response);
            return;
        }

        String selectSQL =
                "SELECT * FROM event_tickets WHERE event_name = ? FOR UPDATE";

        String updateSQL =
                "UPDATE event_tickets SET sold_tickets = ? WHERE event_name = ?";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(selectSQL)) {

                ps.setString(1, "Music Concert");

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        int total = rs.getInt("total_tickets");
                        int sold = rs.getInt("sold_tickets");
                        int available = total - sold;

                        if (quantity <= available) {

                            try (PreparedStatement ups =
                                         con.prepareStatement(updateSQL)) {

                                ups.setInt(1, sold + quantity);
                                ups.setString(2, "Music Concert");
                                ups.executeUpdate();
                            }

                            con.commit();

                            request.setAttribute(
                                    "message", "Tickets booked successfully!");
                            request.setAttribute("quantity", quantity);
                            request.setAttribute("totalPrice", quantity * price);

                            sold += quantity;

                        } else {
                            con.rollback();
                            request.setAttribute(
                                    "message",
                                    "Not enough tickets available!");
                        }

                        request.setAttribute("totalTickets", total);
                        request.setAttribute("soldTickets", sold);
                    } else {
                        con.rollback();
                        request.setAttribute("message", "Event not found!");
                    }
                }

            } catch (Exception e) {
                con.rollback();
                throw e;
            }

            request.getRequestDispatcher("purchase.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
