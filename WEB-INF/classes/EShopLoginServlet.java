import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/eshoplogin")
public class EShopLoginServlet extends HttpServlet {

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {

      String email = request.getParameter("email");
      String password = request.getParameter("password");
	  
		if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
			response.sendRedirect("login.html?error=email");
			return;
		}

		if (password == null || password.trim().isEmpty()) {
			response.sendRedirect("login.html?error=password");
			return;
		}

      try (
         Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
            "myuser", "xxxx");

         PreparedStatement pstmt = conn.prepareStatement(
            "SELECT * FROM users WHERE email=? AND password=?");
      ) {

         pstmt.setString(1, email);
         pstmt.setString(2, password);

         ResultSet rs = pstmt.executeQuery();

         if (rs.next()) {

            HttpSession session = request.getSession(true);
            session.setAttribute("user", email);

            response.sendRedirect("eshopdisplay");

         } else {

            response.sendRedirect("login.html?error=1");

         }

      } catch(SQLException ex) {
         ex.printStackTrace();
      }
   }
}