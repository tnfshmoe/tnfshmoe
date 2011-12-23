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
		postObj.filelink = req.getParameter("filelink");
		postObj.posttime = Long.valueOf(req.getParameter("posttime"));
		postObj.delpw = req.getParameter("delpw");
		postObj.flag = "";
		postObj.putDB(ds);
	}
}
