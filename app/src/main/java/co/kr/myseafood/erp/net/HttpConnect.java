package co.kr.myseafood.erp.net;

import android.webkit.CookieManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.kr.myseafood.erp.define.TAGs;
import co.kr.myseafood.erp.util.DataStorage;
import co.kr.myseafood.erp.util.LogUtil;


/**
 * HTTP, HTTPS 연결을 설정한다
 */
public class HttpConnect {
    private ArrayList<ReqParam> params = null;
    private ArrayList<ReqHeader> headers = null;
    private String requestUrl;

    public HttpConnect(String url){
    	requestUrl = url;
    }

    /**
     * 전송할 파라키터를 설정
     * @param _param 전송 파라미터
     *
     */
    public void setParams(ArrayList<ReqParam> _param){
        params = _param;
    }

    public void setHeaders( ArrayList<ReqHeader> _headers ){
        headers = _headers;
    }
	
	public String requestPost() throws Exception {
		StringBuilder output = new StringBuilder();
        DataStorage dataStorage = DataStorage.getInstance();
        String cookieString = null;
        try {
            LogUtil.d(TAGs.NET, "URL open");
            URL url = new URL(requestUrl);
            LogUtil.d(TAGs.NET, "requestUrl : " + requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(headers!=null) {
                for (int i = 0; i < headers.size(); i++) {
                    ReqHeader reqHeader = headers.get(i);
                    LogUtil.d(TAGs.NET,"init headers : " + reqHeader.name + "/" + reqHeader.value);
                    conn.setRequestProperty(reqHeader.name, reqHeader.value);
                }
            }else{
                LogUtil.d(TAGs.NET,"no headerss");
            }

            if (conn != null) {

//            	if(dataStorage.hasData(DataStorage.COOKIE)){
//                    LogUtil.d(TAG.NET,"set cookie : " + dataStorage.getData(DataStorage.COOKIE));
//            		conn.setRequestProperty("cookie", dataStorage.getData(DataStorage.COOKIE));
//            	}
                cookieString = CookieManager.getInstance().getCookie(co.kr.myseafood.erp.define.URL.SERVER);
                if (cookieString != null) {
                    conn.setRequestProperty("Cookie", cookieString);
                }

                conn.setRequestProperty("Content-Type", "application/json");
	            conn.setConnectTimeout(10000);
	            conn.setRequestMethod("POST");
	            conn.setDoInput(true);
	            conn.setDoOutput(true);
	
	            OutputStream outputStream = conn.getOutputStream();
	            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
	
	            if(params!=null&&params.size()>0){
	            	bufferedWriter.write(getPostData(params));
	            }
	                
	
	            bufferedWriter.flush();
	            bufferedWriter.close();
	            outputStream.close();
	
	            conn.connect();
	            
	            int resCode = conn.getResponseCode();

                LogUtil.d(TAGs.NET,"HttpURLConnection.getResponseCode() : " + resCode);
	            if (resCode == HttpURLConnection.HTTP_OK){
	                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String line = null;
					while(true) {
						line = reader.readLine();
						if (line == null) {
							break;
						}
						output.append(line + "\n");
					}
	                reader.close();
                    LogUtil.d(TAGs.NET,"> send result");
	
	            }
	            
                // 다음 http 요청에 세션 유지를 위해서 쿠키값을 메모리에 저장한다.
//	            StringBuilder sb = new StringBuilder();
//	            Map map = conn.getHeaderFields();
//	            if(map.containsKey("Set-Cookie")){
//	            	Collection c = (Collection)map.get("Set-Cookie");
//	            	for (Iterator i = c.iterator(); i.hasNext(); )
//	            	{
//                        sb.append((String)i.next() + ", ");
//	            	}
//                    LogUtil.d(TAG.NET,"getCookie : " + sb.toString() + " url : " + requestUrl);
//                    dataStorage.setData(DataStorage.COOKIE, sb.toString());
//                }
                Map<String, List<String>> headerFields = conn.getHeaderFields();

                List<String> cookiesHeader = headerFields.get("Set-Cookie");

                if(cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        String cookieName = HttpCookie.parse(cookie).get(0).getName();
                        String cookieValue = HttpCookie.parse(cookie).get(0).getValue();

                        cookieString = cookieName + "=" + cookieValue;

                        dataStorage.setData(DataStorage.COOKIE_NAME, "Cookie");
                        dataStorage.setData(DataStorage.COOKIE_VALUE, cookie);
                        CookieManager.getInstance().setCookie(co.kr.myseafood.erp.define.URL.SERVER, cookieString);

                    }
                }

                conn.disconnect();
                LogUtil.d(TAGs.NET, output.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return output.toString();
    }

    public String requestGet() throws Exception {
        StringBuilder output = new StringBuilder();
        DataStorage dataStorage = DataStorage.getInstance();
        String cookieString = null;

        try {
            LogUtil.d(TAGs.NET, "URL open");
            
            URL url = new URL(requestUrl);
            LogUtil.d(TAGs.NET, "requestUrl : " + requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
            	
            	conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type", "application/json");
	            conn.setConnectTimeout(10000);
	            conn.setRequestMethod("GET");

                cookieString = CookieManager.getInstance().getCookie(co.kr.myseafood.erp.define.URL.SERVER);
                if (cookieString != null) {
                    conn.setRequestProperty("Cookie", cookieString);
                }

	            conn.connect();
	
	            int resCode = conn.getResponseCode();
                LogUtil.d(TAGs.NET,"HttpURLConnection.getResponseCode() : " + resCode);
                if (resCode == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        output.append(line + "\n");
                    }
                    reader.close();
                    LogUtil.d(TAGs.NET,"> send result");

                }

    // 다음 http 요청에 세션 유지를 위해서 쿠키값을 메모리에 저장한다.
//                StringBuilder sb = new StringBuilder();
//                Map map = conn.getHeaderFields();
//                if(map.containsKey("Set-Cookie")){
//                    Collection c = (Collection)map.get("Set-Cookie");
//                    for (Iterator i = c.iterator(); i.hasNext(); ) {
//                        sb.append((String)i.next() + ", ");
//                    }
//                    LogUtil.d(TAG.NET, "getCookie : " + sb.toString() + " url : " + requestUrl);
//                    dataStorage.setData(DataStorage.COOKIE, sb.toString());
//                }

                Map<String, List<String>> headerFields = conn.getHeaderFields();
                List<String> cookiesHeader = headerFields.get("Set-Cookie");

                if(cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        String cookieName = HttpCookie.parse(cookie).get(0).getName();
                        String cookieValue = HttpCookie.parse(cookie).get(0).getValue();

                        cookieString = cookieName + "=" + cookieValue;

                        CookieManager.getInstance().setCookie(co.kr.myseafood.erp.define.URL.SERVER, cookieString);

                    }
                }

                conn.disconnect();
                LogUtil.d(TAGs.NET, output.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    
    /*
    * 요청 메세지를 만든다
    * @param params 전송 파라미터
    * @return 요청 메세지
    * */

    private String getURLQuery(ArrayList<ReqParam> params) throws UnsupportedEncodingException{
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0;i<params.size();i++){
            if(i != 0)	stringBuilder.append("&");

            ReqParam reqParam = params.get(i);
            
            if(reqParam.name.equals("") || reqParam.name != null){
//	              stringBuilder.append(reqParam.name);
//	              stringBuilder.append("=");
//	              stringBuilder.append(reqParam.value);
                stringBuilder.append(URLEncoder.encode(reqParam.name, "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(reqParam.value, "UTF-8"));
            }
        }
        
        return stringBuilder.toString();
    }


    private String getPostData(ArrayList<ReqParam> params) {
        JSONObject json = new JSONObject();
        for(int i=0;i<params.size();i++){
            ReqParam reqParam = params.get(i);
//            LogUtil.d(TAG.NET, "reqParam.name : " + reqParam.name);
//            LogUtil.d(TAG.NET, "reqParam.value : " + reqParam.value);

            if(reqParam.name != null && reqParam.name.length()>0){
                try {
                    json.put(reqParam.name, reqParam.value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        LogUtil.d(TAGs.NET, "param [" + json.toString() + "]");
        return  json.toString();
    }

}
