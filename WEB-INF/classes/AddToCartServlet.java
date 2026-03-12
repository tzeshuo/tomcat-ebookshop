import java.io.*;
import java.util.ArrayList;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/addtocart")
public class AddToCartServlet extends HttpServlet {

   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      String[] ids = request.getParameterValues("id");

      HttpSession session = request.getSession(false);
      if (session == null || session.getAttribute("user") == null) {
         response.sendRedirect("login.html?required=1");
         return;
      }

      ArrayList<String> cart = (ArrayList<String>) session.getAttribute("cart");

      if(cart == null){
         cart = new ArrayList<>();
      }

      if(ids != null){
         for(String id : ids){
            cart.add(id);
         }
      }

      session.setAttribute("cart", cart);

      response.sendRedirect("cart.html");
   }
}
