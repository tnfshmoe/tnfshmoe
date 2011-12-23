package xcjavatestapp;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.urlfetch.*;
import com.google.appengine.api.files.*;

import datastore.*;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet{
	public final char[] FileIDMap = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','Z','Y','Z',
		'0','1','2','3','4','5','6','7','8','9'};
	public String createFileID(){
		Long num;
		String postid;
		
		postid = "";
		num = new Date().getTime();
		while(num > 0){
			postid += FileIDMap[(int)(num % 62)];
			num /= 62;
		}
		
		postid += Math.round(Math.random()*9.0);
		
		return postid;
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
		BlobstoreService bs;
		URLFetchService us;
		FileService fs;
		
		Map<String,BlobKey> blobMap;
		BlobKey blobKey;
		
		String postType;
		String delpw;
		DataObj dataObj;
		String filelink;
		
		String picUrl;
		HTTPResponse picRes; 
		List<HTTPHeader> headerList;
		String picMime;
		String[] fileNamePart;
		AppEngineFile picFile;
		FileWriteChannel writeChannel;
		
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/plain");
		
		try{
			ds = DatastoreServiceFactory.getDatastoreService();
			bs = BlobstoreServiceFactory.getBlobstoreService();
			us = URLFetchServiceFactory.getURLFetchService();
			fs = FileServiceFactory.getFileService();
			
			blobMap = bs.getUploadedBlobs(req);
			blobKey = blobMap.get("input_post_file");
			
			postType = req.getParameter("input_post_type");
			delpw = req.getParameter("input_post_delpw");
			
			if(postType.equals("file") == true){
				if(blobKey == null){
					throw new Exception();
				}
				
				dataObj = new DataObj();
				dataObj.fileid = createFileID();
				dataObj.posttime = new Date().getTime();
				dataObj.delpw = delpw;
				dataObj.blobkey = blobKey;
				dataObj.putDB(ds);
				
				filelink = "http://" + req.getServerName() + "/down/" + dataObj.fileid + "/" + req.getParameter("input_post_text");
				resp.getWriter().print("link.jsp?link=" + filelink);
				
				postFile(us,dataObj.fileid,dataObj.posttime,dataObj.delpw,filelink);
			}else if(postType.equals("url") == true){
				picUrl = req.getParameter("input_post_text");
				
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
				dataObj.fileid = createFileID();
				dataObj.posttime = new Date().getTime();
				dataObj.delpw = delpw;
				dataObj.blobkey = fs.getBlobKey(picFile);
				dataObj.putDB(ds);
				
				filelink = "http://" + req.getServerName() + "/down/" + dataObj.fileid + "/" + fileNamePart[fileNamePart.length - 1];
				resp.getWriter().print("link.jsp?link=" + filelink);
				
				postFile(us,dataObj.fileid,dataObj.posttime,dataObj.delpw,filelink);
			}
		}catch(Exception e){}
	}
}
