import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/eshopquery")
public class EshopQueryServlet extends HttpServlet {

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      PrintWriter out = response.getWriter();

      HttpSession session = request.getSession(false);
      String user = session == null ? null : (String) session.getAttribute("user");

      String[] authors = request.getParameterValues("author");
      String search = request.getParameter("search");
      if (search != null) {
         search = search.trim();
      }

      StringBuilder sql = new StringBuilder("SELECT id, author, title, price FROM books WHERE qty > 0");
      List<String> sqlParams = new ArrayList<>();

      if (authors != null && authors.length > 0) {
         sql.append(" AND author IN (");
         for (int i = 0; i < authors.length; i++) {
            sql.append("?");
            if (i < authors.length - 1) {
               sql.append(",");
            }
            sqlParams.add(authors[i]);
         }
         sql.append(")");
      }

      if (search != null && !search.isEmpty()) {
         sql.append(" AND title LIKE ?");
         sqlParams.add("%" + search + "%");
      }

      sql.append(" ORDER BY author ASC, title ASC");

      StringBuilder json = new StringBuilder();
      json.append("{");
      json.append("\"user\":").append(toJsonString(user)).append(",");
      json.append("\"books\":[");

      try (
            Connection conn = DriverManager.getConnection(
                  "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                  "myuser", "xxxx");
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())
      ) {
         for (int i = 0; i < sqlParams.size(); i++) {
            pstmt.setString(i + 1, sqlParams.get(i));
         }

         try (ResultSet rset = pstmt.executeQuery()) {
            boolean first = true;
            while (rset.next()) {
               if (!first) {
                  json.append(",");
               }
               json.append("{");
               json.append("\"id\":").append(toJsonString(rset.getString("id"))).append(",");
               json.append("\"author\":").append(toJsonString(rset.getString("author"))).append(",");
               json.append("\"title\":").append(toJsonString(rset.getString("title"))).append(",");
               json.append("\"price\":").append(toJsonString(rset.getString("price")));
               json.append("}");
               first = false;
            }
         }
      } catch (SQLException ex) {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         out.print("{\"error\":\"Failed to load query results.\"}");
         return;
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
