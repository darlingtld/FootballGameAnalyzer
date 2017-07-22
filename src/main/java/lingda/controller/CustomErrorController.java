package lingda.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH, method = RequestMethod.GET)
    public void method(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect("/");
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}