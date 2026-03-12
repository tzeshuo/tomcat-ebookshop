import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/eshoporder")
public class EshopOrderServlet extends HttpServlet {

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      HttpSession session = request.getSession(false);
      if (session == null || session.getAttribute("user") == null) {
         response.sendRedirect("login.html?required=1");
         return;
      }

      String email = (String) session.getAttribute("user");
      String[] ids = request.getParameterValues("id");
      if (ids == null || ids.length == 0) {
         response.sendRedirect("order-placed.html?status=empty");
         return;
      }

      String custName = email;
      String custEmail = email;
      String custPhone = "";
      int successCount = 0;

      try (
            Connection conn = DriverManager.getConnection(
                  "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                  "myuser", "xxxx");
            PreparedStatement userStmt = conn.prepareStatement(
                  "SELECT name, email, phone FROM users WHERE email = ?");
            PreparedStatement updateBookStmt = conn.prepareStatement(
                  "UPDATE books SET qty = qty - 1 WHERE id = ? AND qty > 0");
            PreparedStatement insertOrderStmt = conn.prepareStatement(
                  "INSERT INTO order_records (id, qty_ordered, cust_name, cust_email, cust_phone) VALUES (?, 1, ?, ?, ?)")
      ) {
         userStmt.setString(1, email);
         try (ResultSet userSet = userStmt.executeQuery()) {
            if (userSet.next()) {
               custName = userSet.getString("name");
               custEmail = userSet.getString("email");
               custPhone = userSet.getString("phone");
            }
         }

         for (String id : ids) {
            int bookId;
            try {
               bookId = Integer.parseInt(id);
            } catch (NumberFormatException ex) {
               continue;
            }

            updateBookStmt.setInt(1, bookId);
            int updatedRows = updateBookStmt.executeUpdate();
            if (updatedRows == 0) {
               continue;
            }

            insertOrderStmt.setInt(1, bookId);
            insertOrderStmt.setString(2, custName);
            insertOrderStmt.setString(3, custEmail);
            insertOrderStmt.setString(4, custPhone);
            insertOrderStmt.executeUpdate();
            successCount++;
         }
      } catch (SQLException ex) {
         response.sendRedirect("order-placed.html?status=error");
         return;
      }

      if (successCount > 0) {
         removePurchasedFromCart(session, ids);
         response.sendRedirect("order-placed.html?status=success&count=" + successCount);
      } else {
         response.sendRedirect("order-placed.html?status=outofstock");
      }
   }

   private void removePurchasedFromCart(HttpSession session, String[] ids) {
      ArrayList<String> cart = (ArrayList<String>) session.getAttribute("cart");
      if (cart == null || cart.isEmpty()) {
         return;
      }
      for (String id : ids) {
         cart.remove(id);
      }
      session.setAttribute("cart", cart);
   }
}
