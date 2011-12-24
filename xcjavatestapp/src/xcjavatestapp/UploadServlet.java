package xcjavatestapp;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.*;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.urlfetch.*;
import com.google.appengine.api.files.*;

import datastore.*;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet{
	public final char[] UIDMap = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','Z','Y','Z',
		'0','1','2','3','4','5','6','7','8','9'};
	public String createUID(){
		Long num;
		String uid;
		
		uid = "";
		num = new Date().getTime();
		while(num > 0){
			uid += UIDMap[(int)(num % 62)];
			num /= 62;
		}
		
		uid += Math.round(Math.random()*9.0);
		
		return uid;
	}
	public void postFile(URLFetchService us,String fileid,Long posttime,String delpw,String filelink) throws Exception{
		HTTPRequest req;
		String param;
		
		param  = URLEncoder.encode("fileid","UTF-8") + "=" + URLEncoder.encode(fileid,"UTF-8") + "&" +
				URLEncoder.encode("filelink","UTF-8") + "=" + URLEncoder.encode(filelink,"UTF-8") + "&" +
				URLEncoder.encode("posttime","UTF-8") + "=" + URLEncoder.encode(String.valueOf(posttime),"UTF-8") + "&" +
				URLEncoder.encode("delpw","UTF-8") + "=" + URLEncoder.encode(delpw,"UTF-8") + "&"; 
		
		req = new HTTPRequest(new URL("http://xcjavatestapp.appspot.com/post"),HTTPMethod.POST);
		//req = new HTTPRequest(new URL("http://localhost:8888/post"),HTTPMethod.POST);
		req.setPayload(param.getBytes());
		us.fetch(req);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		int index;
		
		DatastoreService ds;
		MemcacheService ms;
		BlobstoreService bs;
		URLFetchService us;
		FileService fs;
		
		Map<String,BlobKey> blobMap;
		BlobKey blobKey;
		
		int postCount;
		int postIndex;
		String postType;
		String postText;
		String postDelpw;
		DataObj dataObj;
		String filelink;
		
		String picUrl;
		HTTPResponse picRes; 
		List<HTTPHeader> headerList;
		String picMime;
		String[] fileNamePart;
		AppEngineFile picFile;
		FileWriteChannel writeChannel;
		
		List<String> linkList;
		String linkID;
		
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/plain");
		
		//try{
			ds = DatastoreServiceFactory.getDatastoreService();
			ms = MemcacheServiceFactory.getMemcacheService();
			bs = BlobstoreServiceFactory.getBlobstoreService();
			us = URLFetchServiceFactory.getURLFetchService();
			fs = FileServiceFactory.getFileService();
			
			blobMap = bs.getUploadedBlobs(req);
			postCount = Integer.valueOf(req.getParameter("input_post_count"));
			linkList = new ArrayList<String>();
			
			for(postIndex = 0;postIndex < postCount;postIndex++){
				try{
					postType = req.getParameter("input_post_type_" + postIndex);
					if(postType == null){
						continue;
					}
					
					postText = req.getParameter("input_post_text_" + postIndex);
					postDelpw = req.getParameter("input_post_delpw_" + postIndex);
					
					if(postType.equals("file") == true){
						blobKey = blobMap.get("input_post_file_" + postIndex);
						if(blobKey == null){
							throw new Exception();
						}
						
						dataObj = new DataObj();
						dataObj.fileid = createUID();
						dataObj.posttime = new Date().getTime();
						dataObj.delpw = postDelpw;
						dataObj.blobkey = blobKey;
						dataObj.putDB(ds);
						
						filelink = "http://" + req.getServerName() + "/down/" + dataObj.fileid + "/" + postText;
						linkList.add(filelink);
						postFile(us,dataObj.fileid,dataObj.posttime,dataObj.delpw,filelink);
					}else if(postType.equals("url") == true){
						picUrl = postText;
						
						picRes = us.fetch(new URL(picUrl));
						headerList = picRes.getHeaders();
						picMime = "application/octet-stream";
						for(index = 0;index < headerList.size();index++){
							if(headerList.get(index).getName().compareToIgnoreCase("Content-Type") == 0){
								picMime = headerList.get(index).getValue();
								break;
							}
						}
						
						fileNamePart = picUrl.split("/");
						
						picFile = fs.createNewBlobFile(picMime,fileNamePart[fileNamePart.length - 1]);
						writeChannel = fs.openWriteChannel(picFile,true);
						writeChannel.write(ByteBuffer.wrap(picRes.getContent()));
						writeChannel.closeFinally();
						
						dataObj = new DataObj();
						dataObj.fileid = createUID();
						dataObj.posttime = new Date().getTime();
						dataObj.delpw = postDelpw;
						dataObj.blobkey = fs.getBlobKey(picFile);
						dataObj.putDB(ds);
						
						filelink = "http://" + req.getServerName() + "/down/" + dataObj.fileid + "/" + fileNamePart[fileNamePart.length - 1];
						linkList.add(filelink);
						
						postFile(us,dataObj.fileid,dataObj.posttime,dataObj.delpw,filelink);
					}
				}catch(Exception e){}
			}
			
			linkID = createUID();
			ms.put("LinkList_" + linkID,linkList);
			
			if(req.getParameter("specflag") != null){
				resp.getWriter().print("/link.jsp?linkid=" + linkID);
			}else{
				resp.sendRedirect("/link.jsp?linkid=" + linkID);
			}
		//}catch(Exception e){}
	}
}
