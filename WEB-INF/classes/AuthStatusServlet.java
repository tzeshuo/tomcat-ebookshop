import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/authstatus")
public class AuthStatusServlet extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      HttpSession session = request.getSession(false);
      String user = session == null ? null : (String) session.getAttribute("user");

      PrintWriter out = response.getWriter();
      out.print("{\"user\":" + toJsonString(user) + "}");
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
