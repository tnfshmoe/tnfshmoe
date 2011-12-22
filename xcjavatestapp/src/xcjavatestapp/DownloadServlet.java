package xcjavatestapp;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.memcache.*;

import datastore.*;

@SuppressWarnings("serial")
public class DownloadServlet extends HttpServlet{
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String[] urlPart;
		String fileid;
		
		DatastoreService ds;
		MemcacheService ms;
		
		int index;
		List<String> dataObjCacheList;
		Map<String,Object> dataObjMap;
		Query q;
		List<Entity> dataObjList;
		DataObj dataObj;

		OutputStream writer;
		
	
			urlPart = req.getRequestURI().split("/");
			if(urlPart.length != 4){
				//throw new Exception();
			}
		
			ds = DatastoreServiceFactory.getDatastoreService();
			ms = MemcacheServiceFactory.getMemcacheService();
			
			fileid = urlPart[2];
			
			writer = resp.getOutputStream();
			
			try{
				dataObjCacheList = (List<String>)ms.get("DataObjCacheList_" + fileid);
				if(dataObjCacheList == null){
					throw new Exception();
				}
				
				dataObjMap = (Map<String,Object>)ms.getAll(dataObjCacheList);
				
				dataObjList = new ArrayList<Entity>();
				for(index = 0;index < dataObjCacheList.size();index++){
					if(dataObjMap.containsKey(dataObjCacheList.get(index)) == false){
						throw new Exception();
					}
					dataObjList.add((Entity)dataObjMap.get(dataObjCacheList.get(index)));
				}
			}catch(Exception e){
				q = new Query("DataObj");
				q.addFilter("fileid",FilterOperator.EQUAL,fileid);
				q.addSort("fileid");
				q.addSort("offset",SortDirection.ASCENDING);
				
				dataObjList = ds.prepare(q).asList(FetchOptions.Builder.withLimit(1024));
				
				dataObjCacheList = new ArrayList<String>();
				dataObjMap = new HashMap<String,Object>();
				
				for(index = 0;index < dataObjList.size();index++){
					dataObjCacheList.add("DataObjEntity_" + fileid + "_" + index);
					dataObjMap.put("DataObjEntity_" + fileid + "_" + index,dataObjList.get(index));
				}
				ms.putAll(dataObjMap);
				ms.put("DataObjCacheList_" + fileid,dataObjCacheList);
			}
			
			dataObj = new DataObj();
			dataObj.getDB(dataObjList.get(0));
			if(dataObj.type.equals("") == true){
				resp.setContentType("application/octet-stream");
			}else{
				resp.setContentType(dataObj.type);
			}
				
			for(index = 0;index < dataObjList.size();index++){
				dataObj.getDB(dataObjList.get(index));
				
				writer.write(dataObj.data.getBytes(),0,(int)(dataObj.size + 0));
			}
		
	}
}
