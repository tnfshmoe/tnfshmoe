package xcjavatestapp;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.memcache.*;
import com.google.gson.*;

@SuppressWarnings("serial")
public class PostLinkServlet extends HttpServlet{
	public void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		MemcacheService ms;
		
		Gson gson;
		String linkID;
		List<String> linkList;
		
		ms = MemcacheServiceFactory.getMemcacheService();
		
		gson = new Gson();
		linkID = req.getParameter("linkid");
		linkList = gson.fromJson(req.getParameter("linklist"),List.class);
		
		ms.put("LinkList_" + linkID,linkList);
	}
}