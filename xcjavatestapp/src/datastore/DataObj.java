package datastore;

import com.google.appengine.api.datastore.*;

public class DataObj {
	public String fileid;
	public Long offset;
	public Long size;
	public String type;
	public String delpw;
	public Blob data;
	
	public void getDB(Entity entity){
		this.fileid = (String)entity.getProperty("fileid");
		this.offset = (Long)entity.getProperty("offset");
		this.size = (Long)entity.getProperty("size");
		this.type = (String)entity.getProperty("type");
		this.delpw = (String)entity.getProperty("delpw");
		this.data = (Blob)entity.getProperty("data");
	}
	
	public void putDB(DatastoreService ds){
		Entity entity;
		
		entity = new Entity("DataObj");
		entity.setProperty("fileid",this.fileid);
		entity.setProperty("offset",this.offset);
		entity.setProperty("size",this.size);
		entity.setProperty("type",this.type);
		entity.setProperty("delpw",this.delpw);
		entity.setProperty("data",this.data);
		
		ds.put(entity);
	}
}
