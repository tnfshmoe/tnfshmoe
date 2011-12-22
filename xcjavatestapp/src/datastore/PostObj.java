package datastore;

import com.google.appengine.api.datastore.*;

public class PostObj{
	public String fileid;
	public String fileurl;
	public Long posttime;
	public String delpw;
	public String flag;
	
	public void getDB(DatastoreService ds,Entity entity){
		this.fileid = (String)entity.getProperty("fileid");
		this.fileurl = (String)entity.getProperty("fileurl");
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
		entity.setProperty("fileurl",this.fileurl);
		entity.setProperty("posttime",this.posttime);
		entity.setProperty("delpw",this.delpw);
		entity.setProperty("flag",this.flag);
		
		ds.put(entity);
	}
}
