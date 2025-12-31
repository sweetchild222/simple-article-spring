package net.inkuk.simple_article.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.inkuk.simple_article.util.Log;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CustomInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {

        final String queryString = request.getQueryString();

        String sessionId = request.getSession().getId();
        String url = request.getMethod() + " " + request.getRequestURI() + (queryString != null ? ("?" + queryString) : "");
        Log.info(url + " {" + sessionId + "}");

        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {

        String sessionId = request.getSession().getId();
        String status = String.valueOf(response.getStatus());

        Log.info(status + " {" + sessionId + "}");
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex){

        String sessionId = request.getSession().getId();
        String status = String.valueOf(response.getStatus());

        Log.info(status + " {" + sessionId + "}");

        if(ex != null)
            Log.error(ex.toString());
    }
}

