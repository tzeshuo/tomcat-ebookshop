import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      PrintWriter out = response.getWriter();

      HttpSession session = request.getSession(false);
      String user = null;
      ArrayList<String> cart = null;
      if (session != null) {
         user = (String) session.getAttribute("user");
         cart = (ArrayList<String>) session.getAttribute("cart");
      }

      StringBuilder json = new StringBuilder();
      json.append("{");
      json.append("\"user\":").append(toJsonString(user)).append(",");
      json.append("\"items\":[");

      boolean first = true;

      if (cart != null && !cart.isEmpty()) {
         try (
               Connection conn = DriverManager.getConnection(
                     "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                     "myuser", "xxxx");
               PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT id, author, title, price FROM books WHERE id = ?")
         ) {
            for (String id : cart) {
               pstmt.setString(1, id);
               try (ResultSet rs = pstmt.executeQuery()) {
                  if (rs.next()) {
                     if (!first) {
                        json.append(",");
                     }
                     json.append("{");
                     json.append("\"id\":").append(toJsonString(rs.getString("id"))).append(",");
                     json.append("\"author\":").append(toJsonString(rs.getString("author"))).append(",");
                     json.append("\"title\":").append(toJsonString(rs.getString("title"))).append(",");
                     json.append("\"price\":").append(toJsonString(rs.getString("price")));
                     json.append("}");
                     first = false;
                  }
               }
            }
         } catch (SQLException ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Failed to load cart items.\"}");
            return;
         }
      }

      json.append("]}");
      out.print(json.toString());
   }

   private static String toJsonString(String value) {
      if (value == null) {
         return "null";
      }
      return "\"" + escapeJson(value) + "\"";
   }

   private static String escapeJson(String value) {
      StringBuilder escaped = new StringBuilder();
      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         switch (c) {
            case '"':
               escaped.append("\\\"");
               break;
            case '\\':
               escaped.append("\\\\");
               break;
            case '\b':
               escaped.append("\\b");
               break;
            case '\f':
               escaped.append("\\f");
               break;
            case '\n':
               escaped.append("\\n");
               break;
            case '\r':
               escaped.append("\\r");
               break;
            case '\t':
               escaped.append("\\t");
               break;
            default:
               if (c < 0x20) {
                  escaped.append(String.format("\\u%04x", (int) c));
               } else {
                  escaped.append(c);
               }
               break;
         }
      }
      return escaped.toString();
   }
}
