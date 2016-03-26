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
import Model.VerificationAnalytics;

/**
 * Servlet implementation class VerificationServlet
 */
@WebServlet("/VerificationServlet")
public class VerificationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new Gson();
    public static String pipeKey = "VerificationAnalysis";
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerificationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	     response.setContentType("text/html");
	        
	        final ConcurrentLinkedQueue<List<VerificationAnalytics>> pipe = (ConcurrentLinkedQueue<List<VerificationAnalytics>>) SparkRunner.pipeBank.get(pipeKey);
	        List<VerificationAnalytics> verificationAnalytics = pipe.peek();
	        
	        if (verificationAnalytics != null) {
	            response.getWriter().print(gson.toJson(verificationAnalytics));        	
	        }
	        else {
	            response.getWriter().print("[]");
	        }
	}



}
