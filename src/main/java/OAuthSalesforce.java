import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

@WebServlet(name = "oauth", urlPatterns = { "/oauth/*", "/oauth" })
public class OAuthSalesforce extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String access_Token = "ACCESS_TOKEN";

	private String consumerKey = "3MVG96_7YM2sI9wQH_AWszGz5FS52NzjPqH9XJer04D9F8oneWt34ZSH4VknKFnQFW5ASVtcUcYSo2bEl9yIw";
	private String consumerSecret = "3B3E21D393667500272CB7B3A7574E791088A6FE19236CB6B8D8E79E2367DACA";
	private String redirectUri = "https://localhost:8443/TestOauth/oauth/call";
	private String loginUrl = "https://login.salesforce.com";
	private String authUrl = null;
	private String tokenUrl = null;

	public void init() throws ServletException {
		try {

			authUrl = loginUrl + "/services/oauth2/authorize?response_type=code&client_id=" + consumerKey
					+ "&redirect_uri="
					+ URLEncoder.encode(redirectUri, java.nio.charset.StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new ServletException(e);
		}

		tokenUrl = loginUrl + "/services/oauth2/token";
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String accessToken = (String) request.getSession().getAttribute(access_Token);

		if (accessToken == null) {
			String instanceUrl = null;

			if (request.getRequestURI().endsWith("oauth")) {
				response.sendRedirect(authUrl);
				return;
			} else {
				String code = request.getParameter("code");
				CloseableHttpClient httpclient = HttpClients.createDefault();
				try {
					HttpPost post = new HttpPost(tokenUrl);
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("code", code));
					nvps.add(new BasicNameValuePair("grant_type", "authorization_code"));
					nvps.add(new BasicNameValuePair("client_id", consumerKey));
					nvps.add(new BasicNameValuePair("client_secret", consumerSecret));
					nvps.add(new BasicNameValuePair("redirect_uri", redirectUri));

					post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

					CloseableHttpResponse closeableresponse = httpclient.execute(post);
					System.out.println("Status:" + closeableresponse.getStatusLine());

					try {
						HttpEntity entity = closeableresponse.getEntity();
						InputStream rstream = entity.getContent();
						JSONObject authResponse = new JSONObject(new JSONTokener(rstream));

						accessToken = authResponse.getString("access_token");

					} catch (JSONException e) {

						e.printStackTrace();
					} finally {
						closeableresponse.close();
					}
				} finally {
					httpclient.close();
				}

			}
			request.getSession().setAttribute("access_Token", accessToken);
		}
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
}