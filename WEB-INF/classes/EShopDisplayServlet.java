import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/eshopdisplay")
public class EShopDisplayServlet extends HttpServlet {

   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      HttpSession session = request.getSession(false);

      String user = "Guest";

      if(session != null && session.getAttribute("user") != null){
         user = (String) session.getAttribute("user");
      }

      response.sendRedirect("shop.html?user=" + user);
   }
}