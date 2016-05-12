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
import Model.FriendAnalytics;

/**
 * Servlet implementation class FriendServlet
 */
@WebServlet("/FriendServlet")
public class FriendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new Gson();
    public static String pipeKey = "FriendAnalysis"; 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FriendServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/html");
        
        final ConcurrentLinkedQueue<List<FriendAnalytics>> pipe = (ConcurrentLinkedQueue<List<FriendAnalytics>>) SparkRunner.pipeBank.get(pipeKey);
        List<FriendAnalytics> friendAnalytics = pipe.peek();
        
        if (friendAnalytics != null) {
            response.getWriter().print(gson.toJson(friendAnalytics));        	
        }
        else {
            response.getWriter().print("[]");
        }
	}


}
