package home.playground.filters

import javax.servlet.*
import javax.servlet.http.HttpServletResponse
import java.io.IOException

/**
 * Created by cuong on 11/15/15.
 */
class CORSFilter : Filter
{
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain)
    {
        val httpResponse = response as HttpServletResponse

        httpResponse.setHeader("Access-Control-Allow-Origin", "*")
        httpResponse.setHeader(
            "Access-Control-Allow-Headers",
            "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
        )

        chain.doFilter(request, response)
    }

    override fun init(filterConfig: FilterConfig) {}

    override fun destroy() {}
}
