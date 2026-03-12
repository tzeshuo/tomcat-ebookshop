// To save as "ebookshop\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import jakarta.servlet.*;            // Tomcat 10 (Jakarta EE 9)
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
//import javax.servlet.*;            // Tomcat 9 (Java EE 8 / Jakarta EE 8)
//import javax.servlet.http.*;
//import javax.servlet.annotation.*;

@WebServlet("/eshopquery")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class EshopQueryServlet extends HttpServlet {

   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
	  
	  HttpSession session = request.getSession(false);
	  String user = (String) session.getAttribute("user");		
      // Set the MIME type for the response message
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      // Print an HTML page as the output of the query
      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head><title>Query Response</title></head>");
      out.println("<body>");

      try (
         // Step 1: Allocate a database 'Connection' object
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");   // For MySQL
               // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

         // Step 2: Allocate a 'Statement' object in the Connection
         Statement stmt = conn.createStatement();
      ) {
         // Step 3: Execute a SQL SELECT query
         // === Form the SQL command - BEGIN ===
			String[] authors = request.getParameterValues("author");
			String search = request.getParameter("search");

			boolean hasAuthor = (authors != null && authors.length > 0);
			boolean hasSearch = (search != null && !search.trim().isEmpty());

			String sqlStr = "SELECT * FROM books WHERE qty > 0";

			if (hasAuthor) {
			   sqlStr += " AND author IN (";
			   for (int i = 0; i < authors.length; ++i) {
				  if (i < authors.length - 1) {
					 sqlStr += "'" + authors[i] + "', ";
				  } else {
					 sqlStr += "'" + authors[i] + "'";
				  }
			   }
			   sqlStr += ")";
			}

			if (hasSearch) {
			   sqlStr += " AND title LIKE '%" + search + "%'";
			}

			sqlStr += " ORDER BY author ASC, title ASC";
		
         out.println("<h3>Thank you for your query.</h3>");
         //out.println("<p>Your SQL statement is: " + sqlStr + "</p>"); // Echo for debugging
         ResultSet rset = stmt.executeQuery(sqlStr);  // Send the query to the server
		 boolean hasResult = false;
		 
         // Step 4: Process the query result
         // Print the <form> start tag
         out.println("<form method='get' action='addtocart'>");
                  		 
         // For each row in ResultSet, print one checkbox inside the <form>
         
		out.println("<table border='1'>");
		out.println("<tr>");
		out.println("<th>Select</th>");
		out.println("<th>ID</th>");
		out.println("<th>Author</th>");
		out.println("<th>Title</th>");
		out.println("<th>Price</th>");
		out.println("</tr>");
		 
		 while(rset.next()) {
			   hasResult = true;
			   out.println("<tr>");
			   out.println("<td><input type='checkbox' name='id' value='"
					 + rset.getString("id") + "' /></td>");
			   out.println("<td>" + rset.getString("id") + "</td>");
			   out.println("<td>" + rset.getString("author") + "</td>");
			   out.println("<td>" + rset.getString("title") + "</td>");
			   out.println("<td>$" + rset.getString("price") + "</td>");
			   out.println("</tr>");
			}
		 if (!hasResult) {
		   out.println("<tr><td colspan='5'>No books found.</td></tr>");
			}
		 
		 out.println("</table>");
		 
		 /*
		 out.println("<p>Enter your Name: <input type='text' name='cust_name' required /></p>");
         out.println("<p>Enter your Email: <input type='email' name='cust_email' required /></p>");
         out.println("<p>Enter your Phone Number: <input type='text' name='cust_phone' /></p>");
		*/
		
         // Print the submit button and </form> end-tag
         out.println("<p><input type='submit' value='ORDER' />");
         out.println("</form>");

         // === Step 4 ends HERE - Do NOT delete the following codes ===
      } catch(SQLException ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }  // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)
 
      out.println("</body></html>");
      out.close();
   }
}