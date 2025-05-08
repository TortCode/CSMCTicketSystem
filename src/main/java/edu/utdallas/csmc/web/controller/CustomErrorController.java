package edu.utdallas.csmc.web.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomErrorController  implements ErrorController {

    @GetMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        String errorPage = "error";	// default

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // handle HTTP 404 Not Found error
                errorPage = "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>404 Error - Page Not Found</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: Arial, Helvetica, sans-serif;\n" +
                        "            background-color: #f7f7f7;\n" +
                        "            display: flex;\n" +
                        "            justify-content: center;\n" +
                        "            align-items: center;\n" +
                        "            height: 100vh;\n" +
                        "            margin: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .error-container {\n" +
                        "            text-align: center;\n" +
                        "        }\n" +
                        "\n" +
                        "        .error-code {\n" +
                        "            font-size: 100px;\n" +
                        "            color: #333;\n" +
                        "            margin-bottom: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .error-message {\n" +
                        "            font-size: 24px;\n" +
                        "            color: #666;\n" +
                        "            margin-bottom: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .home-link {\n" +
                        "            text-decoration: none;\n" +
                        "            color: #007bff;\n" +
                        "            font-weight: bold;\n" +
                        "            font-size: 18px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <div class=\"error-container\">\n" +
                        "        <div class=\"error-code\">404</div>\n" +
                        "        <div class=\"error-message\">Oops! The page you're looking for doesn't exist.</div>\n" +
                        "        <a class=\"home-link\" href=\"/\">Go back to homepage</a>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>\n";

            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                // handle HTTP 403 Forbidden error
                errorPage = "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>404 Error - Page Not Found</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: Arial, Helvetica, sans-serif;\n" +
                        "            background-color: #f7f7f7;\n" +
                        "            display: flex;\n" +
                        "            justify-content: center;\n" +
                        "            align-items: center;\n" +
                        "            height: 100vh;\n" +
                        "            margin: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .error-container {\n" +
                        "            text-align: center;\n" +
                        "        }\n" +
                        "\n" +
                        "        .error-code {\n" +
                        "            font-size: 100px;\n" +
                        "            color: #333;\n" +
                        "            margin-bottom: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .error-message {\n" +
                        "            font-size: 24px;\n" +
                        "            color: #666;\n" +
                        "            margin-bottom: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .home-link {\n" +
                        "            text-decoration: none;\n" +
                        "            color: #007bff;\n" +
                        "            font-weight: bold;\n" +
                        "            font-size: 18px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <div class=\"error-container\">\n" +
                        "        <div class=\"error-code\">403</div>\n" +
                        "        <div class=\"error-message\">Oops! The page you're looking for doesn't exist.</div>\n" +
                        "        <a class=\"home-link\" href=\"/\">Go back to homepage</a>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>\n";

            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // handle HTTP 500 Internal Server error
                errorPage = "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>404 Error - Page Not Found</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: Arial, Helvetica, sans-serif;\n" +
                        "            background-color: #f7f7f7;\n" +
                        "            display: flex;\n" +
                        "            justify-content: center;\n" +
                        "            align-items: center;\n" +
                        "            height: 100vh;\n" +
                        "            margin: 0;\n" +
                        "        }\n" +
                        "\n" +
                        "        .error-container {\n" +
                        "            text-align: center;\n" +
                        "        }\n" +
                        "\n" +
                        "        .error-code {\n" +
                        "            font-size: 100px;\n" +
                        "            color: #333;\n" +
                        "            margin-bottom: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .error-message {\n" +
                        "            font-size: 24px;\n" +
                        "            color: #666;\n" +
                        "            margin-bottom: 20px;\n" +
                        "        }\n" +
                        "\n" +
                        "        .home-link {\n" +
                        "            text-decoration: none;\n" +
                        "            color: #007bff;\n" +
                        "            font-weight: bold;\n" +
                        "            font-size: 18px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <div class=\"error-container\">\n" +
                        "        <div class=\"error-code\">500</div>\n" +
                        "        <div class=\"error-message\">Oops! Something went wrong.</div>\n" +
                        "        <a class=\"home-link\" href=\"/\">Go back to homepage</a>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>\n";

            }
        }

        return errorPage;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
