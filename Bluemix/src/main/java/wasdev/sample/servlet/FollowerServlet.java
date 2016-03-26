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
import Model.LanguageAnalytics;

/**
 * Servlet implementation class FollowerServlet
 */
@WebServlet("/FollowerServlet")
public class FollowerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new Gson();
    public static String pipeKey = "FollowerAnalysis";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        
        final ConcurrentLinkedQueue<List<FollowerAnalytics>> pipe = (ConcurrentLinkedQueue<List<FollowerAnalytics>>) SparkRunner.pipeBank.get(pipeKey);
        List<FollowerAnalytics> followerAnalytics = pipe.peek();
        
        if (followerAnalytics != null) {
            response.getWriter().print(gson.toJson(followerAnalytics));        	
        }
        else {
            response.getWriter().print("[]");
        }
	}

}
