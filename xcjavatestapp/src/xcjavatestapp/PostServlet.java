package xcjavatestapp;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.gson.*;

import datastore.*;

@SuppressWarnings("serial")
public class PostServlet extends HttpServlet{
	public void doPost(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		int index;
		
		DatastoreService ds;
			
		Gson gson;
		List<PostObj> postObjList;
		
		ds = DatastoreServiceFactory.getDatastoreService();
		
		gson = new Gson();
		postObjList = gson.fromJson(req.getParameter("postlist"),List.class);
		
		for(index = 0;index < postObjList.size();index++){
			postObjList.get(index).putDB(ds);
		}
	}
}
