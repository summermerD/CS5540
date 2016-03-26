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
import Model.TweetsAnalytics;

/**
 * Servlet implementation class TweetsServlet
 */
@WebServlet("/TweetsServlet")
public class TweetsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new Gson();
    public static String pipeKey = "TweetsAnalysis";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TweetsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		   response.setContentType("text/html");
	        
	        final ConcurrentLinkedQueue<List<TweetsAnalytics>> pipe = (ConcurrentLinkedQueue<List<TweetsAnalytics>>) SparkRunner.pipeBank.get(pipeKey);
	        List<TweetsAnalytics> tweetsAnalytics = pipe.peek();
	        
	        if (tweetsAnalytics != null) {
	            response.getWriter().print(gson.toJson(tweetsAnalytics));        	
	        }
	        else {
	            response.getWriter().print("[]");
	        }
	}


}
