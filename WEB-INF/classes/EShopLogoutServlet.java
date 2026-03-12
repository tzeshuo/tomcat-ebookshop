import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/eshoplogout")
public class EShopLogoutServlet extends HttpServlet {

   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      HttpSession session = request.getSession(false);

      if (session != null) {
         session.invalidate();
      }

      response.sendRedirect("login.html");
   }
}