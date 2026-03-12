import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/eshopregister")
public class EShopRegisterServlet extends HttpServlet {

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      response.setContentType("text/html");
      PrintWriter out = response.getWriter();

      String name = request.getParameter("name");
      String email = request.getParameter("email");
      String phone = request.getParameter("phone");
      String password = request.getParameter("password");
	  
		if (name == null || name.trim().isEmpty()) {
			out.println("<h3>Name is required.</h3>");
			return;
		}

		if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
			out.println("<h3>Invalid email format.</h3>");
			return;
		}

		if (phone == null || !phone.matches("\\d{8}")) {
			out.println("<h3>Phone number must be exactly 8 digits.</h3>");
			return;
		}

		if (password == null || password.trim().isEmpty()) {
			out.println("<h3>Password is required.</h3>");
			return;
		}

      try (
         Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
            "myuser", "xxxx");

         PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO users (name,email,phone,password) VALUES (?,?,?,?)");
      ) {

         pstmt.setString(1, name);
         pstmt.setString(2, email);
         pstmt.setString(3, phone);
         pstmt.setString(4, password);

         pstmt.executeUpdate();

         response.sendRedirect("login.html?register=success");

      } catch(SQLException ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
      }

      out.close();
   }
}