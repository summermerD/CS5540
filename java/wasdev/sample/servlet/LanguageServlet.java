package wasdev.sample.servlet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import Model.LanguageAnalytics;

/**
 * Servlet implementation class SimpleServlet
 */
@WebServlet("/LanguageServlet")
public class LanguageServlet extends HttpServlet {
	private static final Gson gson = new Gson();
    private static final long serialVersionUID = 1L;
    public static String pipeKey = "languageAnalysis";

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        
        final ConcurrentLinkedQueue<List<LanguageAnalytics>> pipe = (ConcurrentLinkedQueue<List<LanguageAnalytics>>) SparkRunner.pipeBank.get(pipeKey);
        List<LanguageAnalytics> langAnalytics = pipe.peek();
        
        if (langAnalytics != null) {
            response.getWriter().print(gson.toJson(langAnalytics));        	
        }
        else {
            response.getWriter().print("[]");
        }
        
    }

}
