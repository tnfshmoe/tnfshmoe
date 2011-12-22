package xcjavatestapp;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;

import datastore.*;

@SuppressWarnings("serial")
public class PostServlet extends HttpServlet{
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		DatastoreService ds;
			
		PostObj postObj;
		
		ds = DatastoreServiceFactory.getDatastoreService();
		
		postObj = new PostObj();
		postObj.fileid = req.getParameter("fileid");
		postObj.fileurl = req.getParameter("fileurl");
		postObj.delpw = req.getParameter("delpw");
		postObj.posttime = new Date().getTime();
		postObj.flag = "";
		
		postObj.putDB(ds);
	}
}
