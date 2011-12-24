<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="com.google.appengine.api.datastore.Query.*" %>
<%@ page import="datastore.*" %>

<%
	int index;

	DatastoreService ds;

	Key postObjGroupKey;
	Query q;
	List<Entity> postObjList;
	PostObj postObj;

	ds = DatastoreServiceFactory.getDatastoreService();
	
	postObjGroupKey = KeyFactory.createKey("PostObjGroup",1L);
	q = new Query("PostObj",postObjGroupKey);
	q.addSort("posttime",SortDirection.DESCENDING);
	postObjList = ds.prepare(q).asList(FetchOptions.Builder.withLimit(1024).offset(0));
	postObj = new PostObj();
%>

<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<title>Hello App Engine</title>
		
		<script type="text/javascript" src="/highslide/highslide.js"></script>
		<link rel="stylesheet" type="text/css" href="/highslide/highslide.css" />
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/prototype/1.7.0.0/prototype.js"></script>
	</head>
	<body>
		<table style="position:absolute; top:10px; left:10px;">
			<tr>
				<td><a href="/index.jsp" style="font-weight:bold;">上傳</a></td>
				<td><a href="/gallery.jsp" style="font-weight:bold;">畫廊</a></td>
			</tr>
		</table>
		<div style="width:100%; margin:60px 0px 0px 0px;">
		<%
		for(index = 0;index < postObjList.size();index++){
			postObj.getDB(ds,postObjList.get(index));
			%>
			<div style="width:150px; height:150px; margin:5px 5px 5px 5px; text-align:center; float:left;">
				<a href="<%= postObj.filelink %>" class="highslide" onclick="return hs.expand(this);">
					<img src="<%= postObj.filelink %>?size=150" />
				</a>
			</div>
			<%
		}
		%>
		</div>
	</body>
</html>