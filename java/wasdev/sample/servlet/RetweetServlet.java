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

import Model.FollowerAnalytics;
import Model.RetweetAnalytics;

/**
 * Servlet implementation class RetweetServlet
 */
@WebServlet("/RetweetServlet")
public class RetweetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new Gson();
    public static String pipeKey = "RetweetAnalysis";   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RetweetServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	       response.setContentType("text/html");
	        
	        final ConcurrentLinkedQueue<List<RetweetAnalytics>> pipe = (ConcurrentLinkedQueue<List<RetweetAnalytics>>) SparkRunner.pipeBank.get(pipeKey);
	        List<RetweetAnalytics> retweetAnalytics = pipe.peek();
	        
	        if (retweetAnalytics != null) {
	            response.getWriter().print(gson.toJson(retweetAnalytics));        	
	        }
	        else {
	            response.getWriter().print("[]");
	        }
	}

}
