package xcjavatestapp;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.http.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.urlfetch.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;

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
	public void postFile(URLFetchService us,String servername,String fileid,String filename){
		String fileurl;
		
		fileurl = "http://" + servername + "/down/" + fileid + "/" + filename;
		
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		int index;
		
		DatastoreService ds;
		URLFetchService us;
		
		ServletFileUpload upload;
		FileItemIterator iter;
		FileItemStream stream;
		
		BufferedReader bufReader;
		String posttype;
		String delpw;
		
		InputStream reader;
		byte[] buf;
		byte[] data;
		int ret;
		int bufOffset;
		int offset;
		DataObj dataObj;
		String[] fileNamePart;
		
		String picUrl;
		HTTPResponse picRes; 
		List<HTTPHeader> headerList;
		byte[] picUrlData;
		
		resp.setContentType("text/plain");
		
		try{
			ds = DatastoreServiceFactory.getDatastoreService();
			us = URLFetchServiceFactory.getURLFetchService();
			
			upload = new ServletFileUpload();
			posttype = "";
			delpw = "";
			buf = new byte[524288];
			
			iter = upload.getItemIterator(req);
			while(iter.hasNext()){
				stream = iter.next();
				
				if(stream.getFieldName().equals("input_post_type") == true){
					bufReader = new BufferedReader(new InputStreamReader(stream.openStream()));
					posttype = bufReader.readLine();
				}else if(stream.getFieldName().equals("input_post_delpw") == true){
					bufReader = new BufferedReader(new InputStreamReader(stream.openStream()));
					delpw = bufReader.readLine();
					if(delpw == null){
						delpw = "";
					}
				}else if(stream.getFieldName().equals("input_post_file") == true && posttype.equals("file") == true){	
					reader = stream.openStream();
					offset = 0;
					bufOffset = 0;
					dataObj = new DataObj();
					
					dataObj.fileid = createFileID();
					dataObj.type = stream.getContentType();
					dataObj.delpw = delpw;
					
					if(dataObj.type == null){
						throw new Exception();
					}
					
					while(true){
						ret = reader.read(buf,bufOffset,buf.length - bufOffset);
						if(ret == -1){
							break;
						}
												
						bufOffset += ret;
						if(bufOffset == buf.length){
							dataObj.offset = Long.valueOf(offset);
							dataObj.size = Long.valueOf(bufOffset);
							dataObj.data = new Blob(buf);
							dataObj.putDB(ds);
							
							offset += bufOffset;
							bufOffset = 0;
						}
					}
					if(bufOffset > 0){
						data = new byte[bufOffset];
						System.arraycopy(buf,0,data,0,bufOffset);
						
						dataObj.offset = Long.valueOf(offset);
						dataObj.size = Long.valueOf(bufOffset);
						dataObj.data = new Blob(data);
						dataObj.putDB(ds);
					}
					
					fileNamePart = stream.getName().split("[/\\\\]");
					resp.getWriter().print("link.jsp?link=http://" + req.getServerName() + "/down/" + dataObj.fileid + "/" + fileNamePart[fileNamePart.length - 1]);
				}else if(stream.getFieldName().equals("input_post_text") == true && posttype.equals("url") == true){
					bufReader = new BufferedReader(new InputStreamReader(stream.openStream()));
					picUrl = bufReader.readLine();
					
					picRes = us.fetch(new URL(picUrl));
					
					offset = 0;
					dataObj = new DataObj();
					
					dataObj.fileid = createFileID();
					dataObj.type = "";
					headerList = picRes.getHeaders();
					for(index = 0;index < headerList.size();index++){
						if(headerList.get(index).getName().compareToIgnoreCase("Content-Type") == 0){
							dataObj.type = headerList.get(index).getValue();
							break;
						}
					}
					dataObj.delpw = delpw;
					
					picUrlData = picRes.getContent();
					while(offset < picUrlData.length){
						ret = picUrlData.length - offset;
						if(ret >= buf.length){
							ret = buf.length;
							System.arraycopy(picUrlData,offset,buf,0,ret);
							
							dataObj.offset = Long.valueOf(offset);
							dataObj.size = Long.valueOf(ret);
							dataObj.data = new Blob(buf);
							dataObj.putDB(ds);
							
							offset += ret;
						}else{
							data = new byte[ret];
							System.arraycopy(picUrlData,offset,data,0,ret);
							
							dataObj.offset = Long.valueOf(offset);
							dataObj.size = Long.valueOf(ret);
							dataObj.data = new Blob(data);
							dataObj.putDB(ds);
							
							break;
						}
					}
					
					fileNamePart = picUrl.split("/");
					resp.getWriter().print("link.jsp?link=http://" + req.getServerName() + "/down/" + dataObj.fileid + "/" + fileNamePart[fileNamePart.length - 1]);
				}
			}		
		}catch(Exception e){
			e.printStackTrace(resp.getWriter());
		}
	}
}
