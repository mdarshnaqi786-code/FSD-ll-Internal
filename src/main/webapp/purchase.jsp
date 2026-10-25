<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Music Concert Ticket Reservation</title>
</head>
<body>

<h2>Music Concert Ticket Reservation</h2>

<c:choose>
    <c:when test="${soldTickets >= totalTickets}">
        <h2>Sold Out</h2>
        <p>Sorry, all tickets have been sold.</p>
    </c:when>

    <c:otherwise>
        <p>Available Tickets: ${totalTickets - soldTickets}</p>

        <form action="tickets" method="post">
            Number of Tickets:
            <input type="number" name="quantity"
                   min="1"
                   max="${totalTickets - soldTickets}"
                   required>
            <br><br>
            <button type="submit">Purchase Tickets</button>
        </form>
    </c:otherwise>
</c:choose>

<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>

<c:if test="${not empty totalPrice}">
    <p>Tickets Purchased: ${quantity}</p>
    <p>Total Price: &#8377;${totalPrice}</p>
</c:if>

</body>
</html>
