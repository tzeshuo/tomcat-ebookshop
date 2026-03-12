import java.io.*;
import java.util.ArrayList;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.sql.*;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
			
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();

      HttpSession session = request.getSession();
	  String user = (String) session.getAttribute("user");

      ArrayList<String> cart = (ArrayList<String>) session.getAttribute("cart");

      out.println("<html>");
      out.println("<head><title>Shopping Cart</title></head>");
      out.println("<body>");

      out.println("<h2>Your Shopping Cart</h2>");
	  out.println("<p>Logged in as: "+user+"</p>");

      if(cart == null || cart.size() == 0){

         out.println("<p>Your cart is empty</p>");

      }else{

         out.println("<form method='get' action='eshoporder'>");

         out.println("<table border='1'>");
		out.println("<tr>");
		out.println("<th>Select</th>");
		out.println("<th>ID</th>");
		out.println("<th>Author</th>");
		out.println("<th>Title</th>");
		out.println("<th>Price</th>");
		out.println("<th>Action</th>");
		out.println("</tr>");

         try(

				Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
				"myuser","xxxx");

				Statement stmt = conn.createStatement();

				){

				for(String id : cart){

				String sql = "SELECT * FROM books WHERE id=" + id;

				ResultSet rs = stmt.executeQuery(sql);

				if(rs.next()){

				out.println("<tr>");

				out.println("<td><input type='checkbox' name='id' value='" + rs.getString("id") + "'></td>");

				out.println("<td>" + rs.getString("id") + "</td>");

				out.println("<td>" + rs.getString("author") + "</td>");

				out.println("<td>" + rs.getString("title") + "</td>");

				out.println("<td>$" + rs.getString("price") + "</td>");
				
				out.println("<td><a href='removefromcart?id="+id+"'>Remove</a></td>");

				out.println("</tr>");

				}

				}

				}catch(Exception e){

				out.println("<p>Error: "+e.getMessage()+"</p>");

				}

         out.println("</table>");

         out.println("<input type='hidden' name='cust_name' value='"+user+"'>");
		out.println("<input type='hidden' name='cust_email' value='"+user+"'>");
		out.println("<input type='hidden' name='cust_phone' value=''>");

         out.println("<input type='submit' value='CHECKOUT'>");

         out.println("</form>");
      }

      out.println("<br><a href='shop.html'>Continue Shopping</a>");

      out.println("</body>");
      out.println("</html>");
   }
}
