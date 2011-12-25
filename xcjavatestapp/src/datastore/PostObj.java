package datastore;

import java.io.*;
import com.google.appengine.api.datastore.*;

@SuppressWarnings("serial")
public class PostObj implements Serializable{
	public String fileid;
	public String filelink;
	public Long posttime;
	public String delpw;
	public String flag;
	
	public PostObj(){};
	public PostObj(String fileid,String filelink,Long posttime,String delpw,String flag){
		this.fileid = fileid;
		this.filelink =filelink;
		this. posttime = posttime;
		this .delpw = delpw;
		this.flag = flag;
	}
	
	public void getDB(DatastoreService ds,Entity entity){
		this.fileid = (String)entity.getProperty("fileid");
		this.filelink = (String)entity.getProperty("filelink");
		this.posttime = (Long)entity.getProperty("posttime");
		this.delpw = (String)entity.getProperty("delpw");
		this.flag = (String)entity.getProperty("flag");
	}
	
	public void putDB(DatastoreService ds){
		Key groupKey;
		Entity entity;
		
		groupKey = KeyFactory.createKey("PostObjGroup",1L);
		entity = new Entity("PostObj",groupKey);
		
		entity.setProperty("fileid",this.fileid);
		entity.setProperty("filelink",this.filelink);
		entity.setProperty("posttime",this.posttime);
		entity.setProperty("delpw",this.delpw);
		entity.setProperty("flag",this.flag);
		
		ds.put(entity);
	}
}
