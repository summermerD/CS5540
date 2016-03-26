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

import Model.HashtagAnalytics;
import Model.LanguageAnalytics;

/**
 * Servlet implementation class SparkServlet
 */
@WebServlet("/HashtagServlet")
public class HashtagServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new Gson();
    public static String pipeKey = "hashtagAnalysis";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HashtagServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	      response.setContentType("text/html");
	        
	        final ConcurrentLinkedQueue<List<HashtagAnalytics>> pipe = (ConcurrentLinkedQueue<List<HashtagAnalytics>>) SparkRunner.pipeBank.get(pipeKey);
	        List<HashtagAnalytics> hashtagAnalytics = pipe.peek();
	        
	        if (hashtagAnalytics != null) {
	            response.getWriter().print(gson.toJson(hashtagAnalytics));        	
	        }
	        else {
	            response.getWriter().print("[]");
	        }
	}



}
