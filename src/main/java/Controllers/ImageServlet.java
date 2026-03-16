package Controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.*;
import java.nio.file.Files;

@WebServlet("/license-image")
public class ImageServlet extends HttpServlet {

    private static final String UPLOAD_DIR =
        "C:/Users/ADMIN/Documents/SWP391/Project_License/license_images";

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String fileName = request.getParameter("name");

        if (fileName == null) {
            response.sendError(404);
            return;
        }

        File file = new File(UPLOAD_DIR, fileName);

        if (!file.exists()) {
            response.sendError(404);
            return;
        }

        response.setContentType("image/jpeg");
        Files.copy(file.toPath(), response.getOutputStream());
    }
}