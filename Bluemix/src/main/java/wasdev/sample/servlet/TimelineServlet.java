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
import Model.TimelineAnalytics;

/**
 * Servlet implementation class TimelineServlet
 */
@WebServlet("/TimelineServlet")
public class TimelineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new Gson();
    public static String pipeKey = "TimelineAnalysis";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TimelineServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	      response.setContentType("text/html");
	        
	        final ConcurrentLinkedQueue<List<TimelineAnalytics>> pipe = (ConcurrentLinkedQueue<List<TimelineAnalytics>>) SparkRunner.pipeBank.get(pipeKey);
	        List<TimelineAnalytics> timelineAnalytics = pipe.peek();
	        
	        if (timelineAnalytics != null) {
	            response.getWriter().print(gson.toJson(timelineAnalytics));        	
	        }
	        else {
	            response.getWriter().print("[]");
	        }
	}


}
