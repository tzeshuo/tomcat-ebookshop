import java.io.*;
import java.util.ArrayList;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/removefromcart")
public class RemoveFromCartServlet extends HttpServlet {

   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      String id = request.getParameter("id");

      HttpSession session = request.getSession();

      ArrayList<String> cart = (ArrayList<String>) session.getAttribute("cart");

      if(cart != null){
         cart.remove(id);
      }

      session.setAttribute("cart", cart);

      response.sendRedirect("cart.html");
   }
}
